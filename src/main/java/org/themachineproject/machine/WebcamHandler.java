package org.themachineproject.machine;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by nathr on 3/12/2016.
 */
public class WebcamHandler {

    private Group root;
    private VideoCapture videoCapture;
    private Scene scene;
    private Stage stage;
    private ImageView imageView;
    private ArrayList<Identity> identityList;
    private ArrayList<Face> faceList;
    private RecognitionMode recognitionMode;
    private int count;
    public static final int UPDATE_RATE = 33 / 2;
    private CascadeClassifier faceDetector;
    private ArrayList<FacialRecognition.IdentityWithFaceRecognizer> identityWithFaceRecognizerList;
    private Terminal terminal;
    private IdentityDataBaseFile identityDataBaseFile;
    private Permissions.PermissionLevel permissionLevel;
    private MicrophoneHandler microphoneHandler;
    private Thread speechRecognitionThread;

    public static double SPEECH_DISPLAY_HEIGHT = 80.0;
    public static final KeyCombination TERMINAL_KEYBOARD_SHORTCUT = new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN);

    public WebcamHandler(Stage st, Scene s, Group g, IdentityDataBaseFile idbf) {
        permissionLevel = null;
        identityDataBaseFile = idbf;
        stage = st;
        scene = s;
        root = g;
        imageView = new ImageView();
        imageView.setTranslateY(SPEECH_DISPLAY_HEIGHT);
        videoCapture = new VideoCapture(SelectWebcam.selectWebcam());
        if(!videoCapture.isOpened()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Webcam initialization failure (perhaps you don't have a webcam or it is" +
                " not supported by OpenCV). Exiting...", ButtonType.OK);
            alert.showAndWait();
            System.exit(1);
        }
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCodeCombination.NO_MATCH);
        stage.setFullScreen(true);
        root.getChildren().add(imageView);
        identityList = identityDataBaseFile.parseIdentityDataBaseFile();
        recognitionMode = identityDataBaseFile.getRecognitionMode();
        count = 0;
        if(recognitionMode == RecognitionMode.MODE_INITIAL_TRAINING || recognitionMode == RecognitionMode.MODE_TRAINING) {
            File dir = new File(Assets.TRAINING_DIRECTORY);
            dir.mkdir();
            identityWithFaceRecognizerList = new ArrayList<>();
        }
        else
            identityWithFaceRecognizerList = FacialRecognition.initializeFacialRecognitionSystem(identityList);
        faceList = new ArrayList<>();
        faceDetector = Assets.faceDetector;
        terminal = new Terminal(idbf, stage, scene, root);
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                Platform.runLater(() -> {
                    if(TERMINAL_KEYBOARD_SHORTCUT.match(event) && permissionLevel != null)
                        terminal.showTerminal(permissionLevel);
                });
            }
        });
        scene.setFill(Color.BLACK);

        Font font = null;
        try {
            font = Font.loadFont(new FileInputStream(new File(Assets.MAGDA_CLEAN_MONO)), 12f);
        }
        catch(IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Font extraction error, exiting the Machine.", ButtonType.OK);
            alert.showAndWait();
            System.exit(1);
        }
        Text text = new Text("Speech Recognition");
        text.setFont(font);
        text.setFill(Color.WHITE);
        text.setTranslateY(text.getLayoutBounds().getHeight());
        text.setTranslateX(5);
        root.getChildren().add(text);
        Text text2 = new Text();
        text2.setTranslateY(text.getLayoutBounds().getHeight() + 5);
        text2.setFill(Color.WHITE);
        text2.setTranslateX(5);
        root.getChildren().add(text2);
        ImageView close = new ImageView(Assets.CloseIcon);
        close.setFitWidth(32);
        close.setFitHeight(32);
        close.setTranslateY(10);
        close.setTranslateX(scene.getWidth() - close.getFitWidth() - 10);
        close.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Platform.runLater(() -> {
                            exit();
                            stage.close();
                            System.exit(1);
                        });
                event.consume();
            }
        });
        ImageView minimize = new ImageView(Assets.MinimizeIcon);
        minimize.setFitWidth(32);
        minimize.setFitHeight(32);
        minimize.setTranslateY(10);
        minimize.setTranslateX(scene.getWidth() - close.getFitWidth() - minimize.getFitWidth() - 10);
        minimize.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Platform.runLater(() -> {
                    stage.setIconified(true);
                });
                event.consume();
            }
        });
        root.getChildren().addAll(close, minimize);

        speechRecognitionThread = new Thread(() -> {
            microphoneHandler = new MicrophoneHandler(text2);
            microphoneHandler.startSpeechRecognition();
        });
        speechRecognitionThread.start();
    }

    public void grabWebcamOutput() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        Runnable frameGrabber = () -> {
            if(identityDataBaseFile.getClearTerminal()) {
                identityDataBaseFile.setClearTerminal(false);
                Platform.runLater(() -> {
                    terminal.clearTerminal();
                });
            }
            if(identityDataBaseFile.getCloseTerminal()) {
                identityDataBaseFile.setCloseTerminal(false);
                Platform.runLater(() -> {
                    terminal.closeTerminal();
                });
            }
            if(identityDataBaseFile.getCloseApplication()) {
                identityDataBaseFile.setCloseApplication(false);
                Platform.runLater(() -> {
                    exit();
                    stage.close();
                    System.exit(1);
                });
            }
            if(identityDataBaseFile.getUpdate()) {
                identityDataBaseFile.setUpdate(false);
                identityList = identityDataBaseFile.parseIdentityDataBaseFile();
                recognitionMode = identityDataBaseFile.getRecognitionMode();
                if(recognitionMode == RecognitionMode.MODE_INITIAL_TRAINING || recognitionMode == RecognitionMode.MODE_TRAINING) {
                    File dir = new File(Assets.TRAINING_DIRECTORY);
                    dir.mkdir();
                    identityWithFaceRecognizerList = new ArrayList<>();
                }
                else
                    identityWithFaceRecognizerList = FacialRecognition.initializeFacialRecognitionSystem(identityList);
                count = 0;
            }
            Mat frame = new Mat();
            videoCapture.read(frame);
            Image newImage = null;
            boolean update = true;
            if(recognitionMode == RecognitionMode.MODE_INITIAL_TRAINING || recognitionMode == RecognitionMode.MODE_TRAINING) {
                if(count >= FacialRecognition.TRAINING_DURATION) {
                    if(count == FacialRecognition.TRAINING_DURATION)
                        trainingHook();
                    update = false;
                }
                else {
                    FacialDetection facialDetection = new FacialDetection(frame);
                    Rect[] facialRects = facialDetection.detectFaces(faceDetector);
                    if (facialRects.length != 1) {
                        newImage = FacialDetection.toImage(frame);
                        count--;
                    }
                    else {
                        faceList = new ArrayList<>();
                        faceList.add(new Face(facialRects[0], identityList.get(identityList.size() - 1)));
                        newImage = facialDetection.draw(recognitionMode, faceList, count);
                        Imgcodecs.imwrite(Assets.TRAINING_DIRECTORY + File.separator + count + ".png", new Mat(frame, facialRects[0]));
                    }
                    permissionLevel = facialDetection.getPermissionLevel();
                }
            }
            else if(count % UPDATE_RATE == 0) {
                FacialDetection facialDetection = new FacialDetection(frame);
                Rect[] facialRects = facialDetection.detectFaces(faceDetector);
                FacialRecognition facialRecognition = new FacialRecognition(frame, facialRects, recognitionMode, identityWithFaceRecognizerList);
                faceList = facialRecognition.recognizeFaces();
                newImage = facialDetection.draw(recognitionMode, faceList, count);
                permissionLevel = facialDetection.getPermissionLevel();
            }
            else {
                FacialDetection facialDetection = new FacialDetection(frame);
                newImage = facialDetection.draw(recognitionMode, faceList, count);
                permissionLevel = facialDetection.getPermissionLevel();
            }
            if(terminal.getCurrentlyOpen()) {
                Platform.runLater(() -> {
                    terminal.showTerminal(permissionLevel);
                });
            }
            final Image processed = newImage;
            frame.release();
            if(update) {
                Platform.runLater(() -> {
                    imageView.setImage(processed);
                    double xCenter = scene.getWidth() / 2, yCenter = scene.getHeight() / 2;
                    imageView.setTranslateX(xCenter - processed.getWidth() / 2);
                    imageView.setTranslateY(yCenter - processed.getHeight() / 2);
                });
            }
            count++;
        };
        executorService.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
    }

    public void exit() {
        microphoneHandler.stopSpeechRecognition();
        speechRecognitionThread.interrupt();
        videoCapture.release();
    }

    public RecognitionMode getRecognitionMode() {
        return recognitionMode;
    }

    public ArrayList<Identity> getIdentityList() {
        return identityList;
    }

    private void trainingHook() {
        FacialRecognition.trainOnFace(identityList.get(identityList.size() - 1));
        try {
            Files.walkFileTree(Paths.get(Assets.TRAINING_DIRECTORY), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (exc == null) {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    } else {
                        throw exc;
                    }
                }
            });
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Post-training clean-up failed, the Machine is exiting.", ButtonType.OK);
            alert.showAndWait();
        }
        exit();
        System.exit(1);
    }
}
