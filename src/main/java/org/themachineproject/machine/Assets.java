package org.themachineproject.machine;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import org.opencv.objdetect.CascadeClassifier;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by nathr on 3/19/2016.
 */
public class Assets {

    public static final String ASSETS = "ASSETS";
    public static final String DESIGNATIONS = ASSETS + File.separator + "DESIGNATIONS";
    public static final String FONTS = ASSETS + File.separator + "FONTS";
    public static final String FACIAL_DATA_DIRECTORY = "FACIAL_DATA";
    public static final String TRAINING_DIRECTORY = "TRAINING";

    public static final String DATABASE_FILE = "IDENTITY_DATABASE.db";

    public static final String HAARCASCADE_FRONTALFACE_ALT_XML = ASSETS + File.separator + "haarcascade_frontalface_alt.xml";

    public static final String ADMIN_AUX_ADMIN_ASSET_DESIGNATION = DESIGNATIONS + File.separator + "ADMIN_AUX_ADMIN_ASSET.png";
    public static final String SECONDARY_DESIGNATION = DESIGNATIONS + File.separator + "SECONDARY.png";
    public static final String THREAT_DESIGNATION = DESIGNATIONS + File.separator + "THREAT.png";
    public static final String ANALOG_INTERFACE_DESIGNATION = DESIGNATIONS + File.separator + "ANALOG_INTERFACE.png";
    public static final String ICON = ASSETS + File.separator + "ICON.jpg";
    public static final String CLOSE_ICON = ASSETS + File.separator + "CLOSE.png";
    public static final String MINIMIZE_ICON = ASSETS + File.separator + "MINIMIZE.png";

    public static final Image AdminAuxAdminAssetDesignation = loadDesignation(DesignationKind.KIND_ADMIN_AUX_ADMIN_ASSET_DESIGNATION);
    public static final Image SecondaryDesignation = loadDesignation(DesignationKind.KIND_SECONDARY_DESIGNATION);
    public static final Image ThreatDesignation = loadDesignation(DesignationKind.KIND_THREAT_DESIGNATION);
    public static final Image AnalogInterfaceDesignation = loadDesignation(DesignationKind.KIND_ANALOG_INTERFACE);
    public static final Image Icon = loadImage(ICON);
    public static final Image CloseIcon = loadImage(CLOSE_ICON);
    public static final Image MinimizeIcon = loadImage(MINIMIZE_ICON);

    public static final CascadeClassifier faceDetector = loadCascadeClassifier();

    public static final String CALL_ONE_REGULAR = FONTS + File.separator + "CallOne-Regular.ttf"; // this is the command-line font
    public static final String MAGDA_CLEAN_MONO = FONTS + File.separator + "MagdaCleanMono.ttf";

    public static final Font CallOneRegular = loadFont(FontKind.KIND_CALL_ONE_REGULAR);
    public static final Font MagdaCleanMonoSmall = loadFont(FontKind.KIND_MAGDA_CLEAN_MONO_SMALL);
    public static final Font MagdaCleanMonoMedium = loadFont(FontKind.KIND_MAGDA_CLEAN_MONO_MEDIUM);
    public static final Font MagdaCleanMonoLarge = loadFont(FontKind.KIND_MAGDA_CLEAN_MONO_LARGE);
    public static final Font MagdaCleanMonoMini = loadFont(FontKind.KIND_MAGDA_CLEAN_MONO_MINI);

    public static final Color TransparentBlack = new Color(0, 0, 0, 200);
    public static final Color AdminTextColor = new Color(238, 233, 60);
    public static final Color SecondaryTextColor = new Color(255, 255, 255);
    public static final Color ThreatTextColor = new Color(235, 28, 36);
    public static final Color TransparentGray = new Color(207, 207, 207, 230);

    public static final String MachineV2 = "Machine v2.0";
    public static final String TheMachine = "The Machine";

    public static final String SPEECH = ASSETS + File.separator + "SPEECH";
    public static final String SPEECH_ACOUSTIC_MODEL_PATH = SPEECH + File.separator + "en-us";
    public static final String SPEECH_DICTIONARY_PATH = SPEECH + File.separator + "cmudict-en-us.dict";
    public static final String SPEECH_LANGUAGE_MODEL_PATH = SPEECH + File.separator + "en-us.lm.bin";

    private enum DesignationKind {
        KIND_ADMIN_AUX_ADMIN_ASSET_DESIGNATION, KIND_SECONDARY_DESIGNATION, KIND_THREAT_DESIGNATION, KIND_ANALOG_INTERFACE
    }

    public static CascadeClassifier loadCascadeClassifier() {
        return new CascadeClassifier(HAARCASCADE_FRONTALFACE_ALT_XML);
    }

    private static Image loadImage(String path) {
        try {
            return SwingFXUtils.toFXImage(ImageIO.read(new File(path)), null);
        }
        catch (IOException ex) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Icon not found, exiting the Machine.", ButtonType.OK);
                alert.showAndWait();
                System.exit(1);
            });
        }
        return null;
    }

    private static Image loadDesignation(DesignationKind designationKind) {
        try {
            switch (designationKind) {
                case KIND_ADMIN_AUX_ADMIN_ASSET_DESIGNATION:
                    return SwingFXUtils.toFXImage(ImageIO.read(new File(ADMIN_AUX_ADMIN_ASSET_DESIGNATION)), null);
                case KIND_SECONDARY_DESIGNATION:
                    return SwingFXUtils.toFXImage(ImageIO.read(new File(SECONDARY_DESIGNATION)), null);
                case KIND_THREAT_DESIGNATION:
                    return SwingFXUtils.toFXImage(ImageIO.read(new File(THREAT_DESIGNATION)), null);
                case KIND_ANALOG_INTERFACE:
                    return SwingFXUtils.toFXImage(ImageIO.read(new File(ANALOG_INTERFACE_DESIGNATION)), null);
                default:
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Unknown designation, exiting the Machine.", ButtonType.OK);
                        alert.showAndWait();
                        System.exit(1);
                    });
                    break;
            }
        }
        catch(IOException ex) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Designation not found, exiting the Machine.", ButtonType.OK);
                alert.showAndWait();
                System.exit(1);
            });
        }
        return null;
    }

    private static enum FontKind {
        KIND_CALL_ONE_REGULAR, KIND_MAGDA_CLEAN_MONO_SMALL, KIND_MAGDA_CLEAN_MONO_MEDIUM, KIND_MAGDA_CLEAN_MONO_LARGE,
        KIND_MAGDA_CLEAN_MONO_MINI
    }

    private static Font loadFont(FontKind fontKind) {
        try {
            switch (fontKind) {
                case KIND_CALL_ONE_REGULAR:
                    return Font.createFont(Font.TRUETYPE_FONT, new File(CALL_ONE_REGULAR)).deriveFont(14f);
                case KIND_MAGDA_CLEAN_MONO_SMALL:
                    return Font.createFont(Font.TRUETYPE_FONT, new File(MAGDA_CLEAN_MONO)).deriveFont(12f);
                case KIND_MAGDA_CLEAN_MONO_MEDIUM:
                    return Font.createFont(Font.TRUETYPE_FONT, new File(MAGDA_CLEAN_MONO)).deriveFont(24f);
                case KIND_MAGDA_CLEAN_MONO_LARGE:
                    return Font.createFont(Font.TRUETYPE_FONT, new File(MAGDA_CLEAN_MONO)).deriveFont(48f);
                case KIND_MAGDA_CLEAN_MONO_MINI:
                    return Font.createFont(Font.TRUETYPE_FONT, new File(MAGDA_CLEAN_MONO)).deriveFont(8f);
                default:
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Unknown font, exiting the Machine.", ButtonType.OK);
                        alert.showAndWait();
                        System.exit(1);
                    });
                    break;
            }
        }
        catch (IOException|FontFormatException ex) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Font extraction error, exiting the Machine.", ButtonType.OK);
                alert.showAndWait();
                System.exit(1);
            });
        }
        return null;
    }
}
