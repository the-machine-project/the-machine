package org.machineproject.machine;

import java.io.File;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * Created by nathr on 3/12/2016.
 */
public class MainPage extends Application {

    public final static double INITIAL_WINDOW_WIDTH = 500, INITIAL_WINDOW_HEIGHT = 500;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(Assets.TheMachine);
        primaryStage.setResizable(false);

        File openCvInCurrentPath;
        if ("Linux".equalsIgnoreCase(UserPlatform.OS_NAME) && "amd64".equalsIgnoreCase
                (UserPlatform.OS_ARCH)){
            openCvInCurrentPath = new File(System.getProperty("user.dir") + File
                    .separator + "ASSETS" + File.separator
                    + "lib" + File.separator + "x64" + File
                    .separator + "opencv_java310.so");
        }else if ("Windows".equalsIgnoreCase(UserPlatform.OS_NAME) && "amd64"
                .equalsIgnoreCase(UserPlatform.OS_ARCH)){
            openCvInCurrentPath = new File(System.getProperty("user.dir") + File
                    .separator + "opencv" + File.separator
                    + "build" + File.separator + "java" + File.separator + "x64" + File
                    .separator + "opencv_java310.dll");

        }else{
            openCvInCurrentPath = null;
        }


        File fileToLoadFrom;
        if (openCvInCurrentPath.exists() && openCvInCurrentPath.canRead()) {
            //System.out.println("Could find file");
            fileToLoadFrom = openCvInCurrentPath;
        } else {
            fileToLoadFrom = null;
        }

        if(fileToLoadFrom == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fatal Error !");
            alert.setContentText("OpenCV couldn't loaded, exiting");
            alert.showAndWait();
        }else{
            System.load(fileToLoadFrom.getAbsolutePath());

            // DO NOT MOVE THIS BEFORE THE SYSTEM LOAD!!!
            // IT CRASHES THE PROGRAM!!
            primaryStage.getIcons().add(Assets.Icon);
            StartUpAnimation.createStartUpAnimation(primaryStage);
        }

    }
}
