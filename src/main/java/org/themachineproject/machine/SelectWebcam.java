package org.themachineproject.machine;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.VideoInputFrameGrabber;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by nathr on 5/22/2016.
 */
public class SelectWebcam {

    public static double INITIAL_WINDOW_WIDTH = 260, INITIAL_WINDOW_HEIGHT = 60;

    public static int selectWebcam() {
        class Temp { int ret = 0; };
        Temp temp = new Temp();
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Select Webcam");
        Group root = new Group();
        Scene scene = new Scene(root, INITIAL_WINDOW_WIDTH, INITIAL_WINDOW_HEIGHT, Color.WHITE);
        VBox vBox = new VBox(12);
        vBox.setPadding(new Insets(5, 5, 5, 5));
        root.getChildren().add(vBox);

        HBox optionsHBox = new HBox(10);
        Label label = new Label("Webcam:");
        label.setTranslateY(1);
        ArrayList<String> webCamOptions = new ArrayList<>();
        try {
            String[] deviceList = VideoInputFrameGrabber.getDeviceDescriptions();
            webCamOptions = new ArrayList<String>(Arrays.asList(deviceList));
            if(webCamOptions.size() == 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "No webcams found. Exiting...", ButtonType.OK);
                alert.showAndWait();
                System.exit(1);
            }
        } catch(FrameGrabber.Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not list webcams. Exiting...", ButtonType.OK);
            alert.showAndWait();
            System.exit(1);
        }
        ComboBox<String> webcamList = new ComboBox<String>(FXCollections.observableArrayList(webCamOptions));
        webcamList.setPrefWidth(200);
        webcamList.getSelectionModel().select(0);
        optionsHBox.getChildren().add(label);
        optionsHBox.getChildren().add(webcamList);
        optionsHBox.setAlignment(Pos.CENTER);
        vBox.getChildren().add(optionsHBox);

        HBox buttonHBox = new HBox();
        Button button = new Button("Select");
        button.setAlignment(Pos.CENTER);
        buttonHBox.getChildren().add(button);
        buttonHBox.setAlignment(Pos.CENTER);
        vBox.getChildren().add(buttonHBox);

        button.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                temp.ret = webcamList.getSelectionModel().getSelectedIndex();
                stage.close();
            }
        });

        stage.setScene(scene);
        stage.showAndWait();
        stage.centerOnScreen();
        return temp.ret;
    }
}
