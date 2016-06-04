package org.themachineproject.machine;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by nathr on 3/19/2016.
 */
public class FacialDetection {

    private Mat imageMat;
    private Permissions.PermissionLevel permissionLevel;

    public FacialDetection(Mat mat) {
        imageMat = mat;
        permissionLevel = null;
    }

    public Rect[] detectFaces(CascadeClassifier cascadeClassifier) {
        MatOfRect matOfRect = new MatOfRect();
        Mat dest = new Mat();
        Imgproc.cvtColor(imageMat, dest, Imgproc.COLOR_RGB2GRAY);
        cascadeClassifier.detectMultiScale(dest, matOfRect);
        return matOfRect.toArray();
    }

    public Image draw(RecognitionMode rm, ArrayList<Face> faceList, int count) {
        String prefix = "";
        if(rm == RecognitionMode.MODE_INITIAL_TRAINING || rm == RecognitionMode.MODE_TRAINING)
            prefix = "TRAINING ";
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(toImage(imageMat), null);
        Graphics g = bufferedImage.getGraphics();
        g.setFont(Assets.MagdaCleanMonoMedium);
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR), minute = calendar.get(Calendar.MINUTE), second = calendar.get(Calendar.SECOND),
                amPm = calendar.get(Calendar.AM_PM);
        String currentHour = hour < 10 ? "0" + hour : hour + "", currentMinute = minute < 10 ? "0" + minute : minute + "",
                currentSecond = second < 10 ? "0" + second : second + "", currentHalf = amPm == Calendar.AM ? "AM" : "PM";
        String currentTime = currentHour + ":" + currentMinute + ":" + currentSecond + " " + currentHalf;
        ((Graphics2D) g).setPaint(Assets.TransparentBlack);
        g.setFont(Assets.MagdaCleanMonoMedium);
        double currentTimeHeight = Assets.MagdaCleanMonoMedium.getLineMetrics(currentTime, g.getFontMetrics().getFontRenderContext()).getHeight();
        g.drawString(currentTime, 5, 5 + (int) currentTimeHeight);
        g.setFont(Assets.MagdaCleanMonoSmall);
        double machineV2Height = Assets.MagdaCleanMonoSmall.getLineMetrics(Assets.MachineV2, g.getFontMetrics().getFontRenderContext()).getHeight();
        g.drawString(Assets.MachineV2, 5, 5 + (int) currentTimeHeight + (int) machineV2Height + 5);
        permissionLevel = faceList.size() == 0 ? Permissions.PermissionLevel.KIND_REGULAR : Permissions.PermissionLevel.KIND_READ_WRITE_MODIFY;
        for(Face face : faceList) {
            Rect rect = face.getRect();
            BufferedImage designation = null;
            String str = "", uname = "User Name: ???", ssn = "SSN: ???-??-????", about = "About: ???", id = "ID: ???";
            Color color = null;
            if(face.getIdentity() == null) {
                designation = SwingFXUtils.fromFXImage(Assets.SecondaryDesignation, null);
                str = "SECONDARY";
                color = Assets.SecondaryTextColor;
            }
            else {
                switch (face.getIdentity().getPermissionsKind()) {
                    case KIND_ADMIN:
                    case KIND_ASSET:
                    case KIND_AUX_ADMIN:
                        designation = SwingFXUtils.fromFXImage(Assets.AdminAuxAdminAssetDesignation, null);
                        break;
                    case KIND_THREAT:
                        designation = SwingFXUtils.fromFXImage(Assets.ThreatDesignation, null);
                        break;
                    case KIND_ANALOG_INTERFACE:
                        designation = SwingFXUtils.fromFXImage(Assets.AnalogInterfaceDesignation, null);
                        break;
                    default:
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Internal Machine error, exiting.", ButtonType.OK);
                        alert.showAndWait();
                        System.exit(1);
                        break;
                }
                if(face.getIdentity().getPermissionsKind() == Identity.PermissionsKind.KIND_THREAT)
                    color = Assets.ThreatTextColor;
                else
                    color = Assets.AdminTextColor;
                str = face.getIdentity().getPermissionsString();
                uname = "User Name: " + face.getIdentity().getUserName().substring(0, face.getIdentity().getUserName().length() < 10 ? face.getIdentity().getUserName().length() : 10);
                if(face.getIdentity().getUserName().length() > 10)
                    uname += "...";
                ssn = "SSN: " + face.getIdentity().getSocialSecurityNumber();
                about = "About: " + face.getIdentity().getAbout().substring(0, face.getIdentity().getAbout().length() < 10 ? face.getIdentity().getAbout().length() : 10);
                if(face.getIdentity().getAbout().length() > 10)
                    about += "...";
                id = "ID: " + face.getIdentity().getGeneratedId();
            }
            if(Permissions.getNumericPermissions(Permissions.getPermissionsFromFace(face)) < Permissions.getNumericPermissions(permissionLevel))
                permissionLevel = Permissions.getPermissionsFromFace(face);
            g.drawImage(designation, rect.x, rect.y, rect.width, rect.height, (java.awt.Image img, int infoflags, int x, int y, int width, int height) -> false);

            String assetOrSecondary = (str.equals("SECONDARY") ? "SECONDARY IDENTIFIED" : "ASSET IDENTIFIED");
            g.setFont(Assets.MagdaCleanMonoSmall);
            double asHeight = Assets.MagdaCleanMonoSmall.getLineMetrics(assetOrSecondary, g.getFontMetrics().getFontRenderContext()).getHeight(),
                    asWidth = g.getFontMetrics().stringWidth(assetOrSecondary);
            ((Graphics2D) g).setPaint(Color.WHITE);
            ((Graphics2D) g).fill(new Rectangle2D.Double(rect.x + rect.width, rect.y + rect.height / 2 - asHeight - 10, asWidth + 20, asHeight + 10));
            ((Graphics2D) g).setPaint(Color.BLACK);
            g.drawString(assetOrSecondary, rect.x + rect.width + 10, rect.y + rect.height / 2 - 5);

            g.setFont(Assets.MagdaCleanMonoLarge);
            double desigHeight = Assets.MagdaCleanMonoLarge.getLineMetrics(prefix + str, g.getFontMetrics().getFontRenderContext()).getHeight(),
                    desigWidth = g.getFontMetrics().stringWidth(prefix + str);
            ((Graphics2D) g).setPaint(Assets.TransparentBlack);
            ((Graphics2D) g).fill(new Rectangle2D.Double(rect.x + rect.width, rect.y + rect.height / 2, desigWidth + 100, rect.height / 3));
            ((Graphics2D) g).setPaint(color);
            g.drawString(prefix + str, rect.x + rect.width + 50, rect.y + 2 * rect.height / 3 + 10);

            g.setFont(Assets.MagdaCleanMonoMedium);
            ((Graphics2D) g).setPaint(Color.WHITE);
            String tripleColon = ":::";
            double tripleColonHeight = Assets.MagdaCleanMonoMedium.getLineMetrics(tripleColon, g.getFontMetrics().getFontRenderContext()).getHeight(),
                    tripleColonWidth = g.getFontMetrics().stringWidth(tripleColon);
            g.drawString(tripleColon, rect.x + rect.width, rect.y + rect.height / 2 + (int) tripleColonHeight);

            ((Graphics2D) g).setPaint(Assets.TransparentGray);
            ((Graphics2D) g).fill(new Rectangle2D.Double(rect.x + rect.width, rect.y + 5 * rect.height / 6 + 10, desigWidth + 100, 10));
            g.setFont(Assets.MagdaCleanMonoSmall);
            double unameHeight = Assets.MagdaCleanMonoMedium.getLineMetrics(uname, g.getFontMetrics().getFontRenderContext()).getHeight(),
                    ssnHeight = Assets.MagdaCleanMonoMedium.getLineMetrics(ssn, g.getFontMetrics().getFontRenderContext()).getHeight(),
                    aboutHeight = Assets.MagdaCleanMonoMedium.getLineMetrics(about, g.getFontMetrics().getFontRenderContext()).getHeight();
            ((Graphics2D) g).setPaint(Assets.TransparentBlack);
            ((Graphics2D) g).fill(new Rectangle2D.Double(rect.x + rect.width, rect.y + 5 * rect.height / 6 + 20 + 5, desigWidth + 100, 5 + unameHeight + 5 + ssnHeight + 5 + aboutHeight + 5));
            ((Graphics2D) g).setPaint(Color.WHITE);
            g.drawString(uname, rect.x + rect.width + 5, rect.y + 5 * rect.height / 6 + 20 + 5 + 15);
            g.drawString(ssn , rect.x + rect.width + 5, rect.y + 5 * rect.height / 6 + 20 + 5 + 15 + (int) unameHeight + 5);
            g.drawString(about, rect.x + rect.width + 5, rect.y + 5 * rect.height / 6 + 20 + 5 + 15 + (int) unameHeight + 5 + (int) ssnHeight + 5);
            ((Graphics2D) g).setPaint(Assets.TransparentGray);
            ((Graphics2D) g).fill(new Rectangle2D.Double(rect.x + rect.width, rect.y + 5 * rect.height / 6 + 20 + 5 + 5 + unameHeight + 5 + ssnHeight + 5 + aboutHeight + 10, desigWidth + 100, 10));

            ((Graphics2D) g).setPaint(Assets.TransparentBlack);
            g.setFont(Assets.MagdaCleanMonoMini);
            double idHeight = Assets.MagdaCleanMonoMini.getLineMetrics(id, g.getFontMetrics().getFontRenderContext()).getHeight(),
                    idWidth = g.getFontMetrics().stringWidth(id);
            ((Graphics2D) g).fill(new Rectangle2D.Double(rect.x + rect.width / 2 - idWidth / 2 - 5, rect.y + rect.height + 5, idWidth + 10, idHeight + 10));
            ((Graphics2D) g).setPaint(Color.WHITE);
            g.drawString(id, rect.x + rect.width / 2 - (int) idWidth / 2, rect.y + rect.height + 5 + 10);

            if(rm == RecognitionMode.MODE_TRAINING || rm == RecognitionMode.MODE_INITIAL_TRAINING) {
                g.setFont(Assets.MagdaCleanMonoMedium);
                ((Graphics2D) g).setPaint(Color.WHITE);
                String watermark = "TRAINING SAMPLE: " + (count + 1) + "/" + FacialRecognition.TRAINING_DURATION;
                double watermarkHeight = Assets.MagdaCleanMonoMedium.getLineMetrics(watermark, g.getFontMetrics().getFontRenderContext()).getHeight();
                double watermarkWidth = Assets.MagdaCleanMonoMedium.createGlyphVector(g.getFontMetrics().getFontRenderContext(), watermark).getVisualBounds().getWidth();
                g.drawString(watermark, (int) Math.round(bufferedImage.getWidth() - watermarkWidth - 5), (int) Math.round(bufferedImage.getHeight() - watermarkHeight));
            }
        }
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

    public static Mat toMat(Image image) {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        newBufferedImage.setData(bufferedImage.getRaster());
        byte[] data = ((DataBufferByte) newBufferedImage.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(newBufferedImage.getHeight(), newBufferedImage.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, data);
        return mat;
    }

    public Permissions.PermissionLevel getPermissionLevel() {
        return permissionLevel;
    }

    public static Image toImage(Mat mat) {
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".png", mat, matOfByte);
        return new Image(new ByteArrayInputStream(matOfByte.toArray()));
    }
}
