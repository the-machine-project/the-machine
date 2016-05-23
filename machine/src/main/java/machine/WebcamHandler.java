package main.java.machine;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import java.io.File;
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

    public WebcamHandler(Stage st, Scene s, Group g, ArrayList<Identity> il, RecognitionMode rm) {
        stage = st;
        scene = s;
        root = g;
        imageView = new ImageView();
        videoCapture = new VideoCapture(SelectWebcam.selectWebcam());
        if(!videoCapture.isOpened()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Webcam initialization failure (perhaps you don't have a webcam or it is" +
                " not supported by OpenCV). Exiting...", ButtonType.OK);
            alert.showAndWait();
            System.exit(1);
        }
        root.getChildren().add(imageView);
        identityList = il;
        recognitionMode = rm;
        count = 0;
        if(recognitionMode == RecognitionMode.MODE_INITIAL_TRAINING || recognitionMode == RecognitionMode.MODE_TRAINING) {
            File dir = new File(Assets.TRAINING_DIRECTORY);
            dir.mkdir();
            identityWithFaceRecognizerList = new ArrayList<>();
        }
        else
            identityWithFaceRecognizerList = FacialRecognition.initializeFacialRecognitionSystem(il);
        faceList = new ArrayList<>();
        faceDetector = Assets.faceDetector;
    }

    public void grabWebcamOutput() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        Runnable frameGrabber = () -> {
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
                }
            }
            else if(count % UPDATE_RATE == 0) {
                FacialDetection facialDetection = new FacialDetection(frame);
                Rect[] facialRects = facialDetection.detectFaces(faceDetector);
                FacialRecognition facialRecognition = new FacialRecognition(frame, facialRects, recognitionMode, identityWithFaceRecognizerList);
                faceList = facialRecognition.recognizeFaces();
                newImage = facialDetection.draw(recognitionMode, faceList, count);
            }
            else {
                FacialDetection facialDetection = new FacialDetection(frame);
                newImage = facialDetection.draw(recognitionMode, faceList, count);
            }
            final Image processed = newImage;
            frame.release();
            if(update) {
                Platform.runLater(() -> {

                    stage.setHeight(processed.getHeight() + scene.getY());
                    stage.setWidth(processed.getWidth());
                    imageView.setImage(processed);
                });
            }
            count++;
        };
        executorService.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
    }

    public void exit() {
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
