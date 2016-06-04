package org.themachineproject.machine;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.bytedeco.javacpp.*;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by nathr on 3/20/2016.
 */
public class FacialRecognition {

    public static final double THRESHOLD_DISTANCE = 40.0;
    public static final int TRAINING_DURATION = 200;

    public static class IdentityWithFaceRecognizer {

        private Identity identity;
        private opencv_face.LBPHFaceRecognizer lbphFaceRecognizer;
        private double similarity;

        public IdentityWithFaceRecognizer(Identity i, opencv_face.LBPHFaceRecognizer lbph) {
            identity = i;
            lbphFaceRecognizer = lbph;
            similarity = 0;
        }

        public Identity getIdentity() {
            return identity;
        }

        public opencv_face.LBPHFaceRecognizer getLbphFaceRecognizer() {
            return lbphFaceRecognizer;
        }

        public void setSimilarity(double s) {
            similarity = s;
        }

        public double getSimilarity() {
            return similarity;
        }
    }

    private ArrayList<opencv_core.Mat> faceList;
    private RecognitionMode recognitionMode;
    private Rect[] rects;
    private ArrayList<IdentityWithFaceRecognizer> identityWithFaceRecognizerList;
    Mat imageMat;

    public static opencv_core.Mat openCVMatToJavaCVMat(Mat m) {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(FacialDetection.toImage(m), null);
        BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        newBufferedImage.setData(bufferedImage.getRaster());
        opencv_core.Mat ret = new opencv_core.Mat(newBufferedImage.getHeight(), newBufferedImage.getWidth(), opencv_core.CV_8UC3, new BytePointer(((DataBufferByte) newBufferedImage.getRaster().getDataBuffer()).getData()));
        opencv_core.Mat fnal = new opencv_core.Mat();
        org.bytedeco.javacpp.opencv_imgproc.cvtColor(ret, fnal, opencv_imgproc.CV_BGR2GRAY);
        return fnal;
    }

    public static ArrayList<IdentityWithFaceRecognizer> initializeFacialRecognitionSystem(ArrayList<Identity> il) {
        ArrayList<IdentityWithFaceRecognizer> ret = new ArrayList<>();
        for(Identity identity : il) {
            opencv_face.LBPHFaceRecognizer lbphFaceRecognizer = opencv_face.createLBPHFaceRecognizer();
            lbphFaceRecognizer.load(new opencv_core.FileStorage(identity.getFacialDataFile(), opencv_core.FileStorage.READ));
            ret.add(new IdentityWithFaceRecognizer(identity, lbphFaceRecognizer));
        }
        return ret;
    }

    public FacialRecognition(Mat mat, Rect[] rs, RecognitionMode rm, ArrayList<IdentityWithFaceRecognizer> iwfrl) {
        rects = rs;
        imageMat = mat;
        faceList = new ArrayList<>();
        for(Rect rect : rects)
            faceList.add(openCVMatToJavaCVMat(new Mat(imageMat, rect)));
        recognitionMode = rm;
        identityWithFaceRecognizerList = iwfrl;
    }

    public void release() {
        for(opencv_core.Mat mat : faceList)
            mat.release();
    }

    public ArrayList<Face> recognizeFaces() {
        ArrayList<Face> ret = new ArrayList<>();
        for(int i = 0; i < faceList.size(); i++) {
            opencv_core.Mat mat = faceList.get(i);
            ArrayList<Double> confidenceList = new ArrayList<>();
            for (IdentityWithFaceRecognizer identityWithFaceRecognizer : identityWithFaceRecognizerList) {
                double[] confidence = new double[]{ 0.5 };
                identityWithFaceRecognizer.getLbphFaceRecognizer().predict(mat, new int[] { 1 }, confidence);
                confidenceList.add(confidence[0]);
            }
            double minDistance = Collections.min(confidenceList);
            int minIndex = confidenceList.indexOf(minDistance);
            if(minDistance <= THRESHOLD_DISTANCE)
                ret.add(new Face(rects[i], identityWithFaceRecognizerList.get(minIndex).getIdentity()));
            else
                ret.add(new Face(rects[i], null));
        }
        return ret;
    }

    public static void trainOnFace(Identity identity) {
        File file = new File(identity.getFacialDataFile());
        try {
            file.createNewFile();
        }
        catch (IOException ex) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Cannot create a facial data file, please make sure that the Machine has the"
                        + " correct file permissions.", ButtonType.OK);
                alert.showAndWait();
                System.exit(1);
            });
        }
        opencv_core.MatVector images = new opencv_core.MatVector(TRAINING_DURATION);
        opencv_core.Mat labels = new opencv_core.Mat(TRAINING_DURATION, 1, opencv_core.CV_32SC1);
        IntBuffer labelsBuffer = labels.getIntBuffer();
        for(int i = 0; i < TRAINING_DURATION; i++) {
            opencv_core.Mat m = opencv_imgcodecs.imread(Assets.TRAINING_DIRECTORY + File.separator + i + ".png", opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
            images.put(i, m);
            labelsBuffer.put(i, 0);
        }
        opencv_face.LBPHFaceRecognizer lbphFaceRecognizer = opencv_face.createLBPHFaceRecognizer();
        lbphFaceRecognizer.train(images, labels);
        lbphFaceRecognizer.save(new opencv_core.FileStorage(identity.getFacialDataFile(), opencv_core.FileStorage.WRITE));
    }
}
