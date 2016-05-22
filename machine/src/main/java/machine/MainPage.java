package main.java.machine;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.io.File;

/**
 * Created by nathr on 3/12/2016.
 */
public class MainPage extends Application {

    public final static double INITIAL_WINDOW_WIDTH = 500, INITIAL_WINDOW_HEIGHT = 500;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle(Assets.TheMachine);

        System.load(System.getProperty("user.dir") + File.separator + "opencv" + File.separator + "build" + File.separator + "java" +
                File.separator + "x64" + File.separator + "opencv_java300.dll");

        // DO NOT MOVE THIS BEFORE THE SYSTEM LOAD!!!
        // IT CRASHES THE PROGRAM!!
        primaryStage.getIcons().add(Assets.Icon);
        StartUpAnimation.createStartUpAnimation(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
