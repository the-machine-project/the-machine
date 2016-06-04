package org.themachineproject.machine;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by nathr on 3/19/2016.
 */
public class IdentityEntry {

    public static final double INITIAL_WINDOW_HEIGHT = 224, INITIAL_WINDOW_WIDTH = 300;
    private Identity constructed;
    RecognitionMode recognitionMode;
    ArrayList<Identity> identityList;
    IdentityDataBaseFile identityDataBaseFile;

    public IdentityEntry(IdentityDataBaseFile idbf) {
        recognitionMode = idbf.getRecognitionMode();
        identityList = idbf.parseIdentityDataBaseFile();
        identityDataBaseFile = idbf;
        show();
    }

    private void show() {
        Stage primaryStage = new Stage();
        primaryStage.setResizable(false);
        primaryStage.setTitle("Create a new Identity");
        Group root = new Group();
        Scene scene = new Scene(root, INITIAL_WINDOW_WIDTH, INITIAL_WINDOW_HEIGHT, Color.WHITE);
        VBox parentVBox = new VBox(12);
        parentVBox.setTranslateX(5);
        parentVBox.setTranslateY(12);
        root.getChildren().add(parentVBox);

        HBox permissionsHBox = new HBox();
        Label permissionsLabel = new Label("Permission Kind: "); ComboBox<String> permissionsOptions;
        if(recognitionMode == RecognitionMode.MODE_INITIAL_TRAINING) {
            permissionsOptions = new ComboBox<String>(
                    FXCollections.observableArrayList(new ArrayList<String>(Arrays.asList(new String[]{"ADMIN"}))));
            permissionsOptions.setDisable(true);
        }
        else
            permissionsOptions = new ComboBox<String>(
                    FXCollections.observableArrayList(new ArrayList<String>(Arrays.asList(new String[]{"AUX_ADMIN", "ASSET", "THREAT", "ANALOG_INTERFACE"}))));
        permissionsOptions.getSelectionModel().select(0);
        permissionsOptions.setStyle("-fx-outer-border: lightblue;");
        permissionsLabel.setTranslateY(4);
        permissionsHBox.getChildren().addAll(permissionsLabel, permissionsOptions);
        parentVBox.getChildren().add(permissionsHBox);

        HBox userNameHBox = new HBox();
        Label userNameLabel = new Label("Name: ");
        TextField userNameTextField = new TextField();
        userNameTextField.setPromptText("Please enter a user name...");
        userNameTextField.setStyle("-fx-text-box-border: lightblue;");
        userNameLabel.setTranslateY(4);
        userNameHBox.getChildren().addAll(userNameLabel, userNameTextField);
        parentVBox.getChildren().add(userNameHBox);

        HBox aboutHBox = new HBox();
        Label aboutLabel = new Label("About: ");
        TextArea aboutTextArea = new TextArea();
        aboutTextArea.setStyle("-fx-text-box-border: lightblue;");
        aboutTextArea.setWrapText(true);
        aboutTextArea.setMaxWidth(INITIAL_WINDOW_WIDTH - (5 + aboutLabel.getLayoutBounds().getWidth() + aboutHBox.getSpacing() + 5) - 32);
        aboutTextArea.setMaxHeight(100);
        aboutTextArea.setPromptText("Type something about yourself (if you don't want anything, just type your real name here).");
        aboutHBox.getChildren().addAll(aboutLabel, aboutTextArea);
        parentVBox.getChildren().add(aboutHBox);

        HBox createHBox = new HBox();
        Button createButton = new Button("Create");
        createButton.setStyle("-fx-outer-border: lightblue;");
        createHBox.getChildren().add(createButton);
        createHBox.setAlignment(Pos.CENTER);
        parentVBox.getChildren().add(createHBox);

        createButton.setOnMouseClicked((MouseEvent mouseEvent) -> {
            String chosenPermission = permissionsOptions.getSelectionModel().getSelectedItem();
            String userName = "", aboutText = "";
            Identity.PermissionsKind permissionsKind = Identity.permissionStringToPermissionsKind(chosenPermission);
            if(validUserInput(userNameTextField.getText()) && userNameTextField.getText().length() > 0 && !userNameTextField.getText().contains(" "))
                userName = userNameTextField.getText();
            else {
                userNameTextField.setStyle("-fx-text-box-border: red;");
                return;
            }
            userNameTextField.setStyle("-fx-text-box-border: lightblue;");
            if(validUserInput(aboutTextArea.getText()) && aboutTextArea.getText().length() > 0)
                aboutText = aboutTextArea.getText();
            else {
                aboutTextArea.setStyle("-fx-text-box-border: red;");
                return;
            }
            aboutTextArea.setStyle("-fx-text-box-border: lightblue;");
            for(Identity identity : identityList)
                if(identity.getUserName().equals(userName)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "This user name has already been chosen, please choose another one.", ButtonType.OK);
                    alert.showAndWait();
                    userNameTextField.setStyle("-fx-text-box-border: red;");
                    return;
                }
            userNameTextField.setStyle("-fx-text-box-border: lightblue;");
            String ssn = generateSocialSecurityNumber();
            String id = generateRandomId();
            boolean test = true;
            while(test) {
                for (Identity identity : identityList)
                    if (identity.getSocialSecurityNumber().equals(ssn)) {
                        test = false;
                        break;
                    }
                test = !test;
                if(test)
                    ssn = generateSocialSecurityNumber();
            }
            test = true;
            while(test) {
                for(Identity identity : identityList) {
                    if(identity.getGeneratedId().equals(id)) {
                        test = false;
                        break;
                    }
                }
                test = !test;
                if(test)
                    id = generateRandomId();
            }
            String[] ssnSplit = ssn.split("-");
            constructed = new Identity(recognitionMode, permissionsKind, userName, ssn, Assets.FACIAL_DATA_DIRECTORY + File.separator +
                    userName + ssnSplit[0] + "-XX-XXXX.dat", aboutText, id);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "A new identity has been constructed. Here are your login credentials: \n" +
                    "User Name: " + userName + "\n" +
                    "Social Security Number: " + ssn + "\n\n" +
                    "Please remember these details.", ButtonType.OK);
            alert.showAndWait();
            identityDataBaseFile.writeIdentityToFile(constructed);
            primaryStage.close();
        });

        primaryStage.setOnCloseRequest((v) -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Identity entry cancelled, exiting application.", ButtonType.OK);
            alert.showAndWait();
            identityDataBaseFile.setCloseTerminal(true);
            identityDataBaseFile.setCloseApplication(true);
            identityDataBaseFile.setUpdate(true);
            System.exit(1);
        });

        primaryStage.setScene(scene);
        primaryStage.showAndWait();
        primaryStage.centerOnScreen();
    }

    private boolean validUserInput(String str) {
        if(str.contains(IdentityDataBaseFile.INNER_IDENTITY_DELIMITER) || str.contains(IdentityDataBaseFile.OUTER_IDENTITY_DELIMITER))
            return false;
        return true;
    }

    private String generateSocialSecurityNumber() {
        int part1 = (int) (Math.random() * 900) + 100;
        int part2 = (int) (Math.random() * 90) + 10;
        int part3 = (int) (Math.random() * 9000) + 1000;
        return part1 + "-" + part2 + "-" + part3;
    }

    private String generateRandomId() {
        return UUID.randomUUID().toString();
    }

    public Identity getConstructedIdentity() {
        return constructed;
    }
}
