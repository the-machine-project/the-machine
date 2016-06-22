package org.themachineproject.machine.command.qui;


import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.*;
import org.bytedeco.javacv.CanvasFrame;
import org.themachineproject.machine.*;
import org.themachineproject.machine.command.Command;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

import java.awt.ScrollPane;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Rocco on 05/06/2016.
 */
public class QUIShell {

    private Label currentPrompt;

    private TextField currentCommandEntry;
    private javafx.scene.control.ScrollPane scrollPane;
    private HBox currentLine;
    private Stage primaryStage;
    private Scene scene;
    private VBox allLines;
    private boolean currentlyOpen;
    private javafx.scene.text.Font machineTerminalFont;

    public static double INITIAL_WINDOW_WIDTH = 800, INITIAL_WINDOW_HEIGHT = 500;

    private void initialize() {
        currentlyOpen = true;

        primaryStage = new Stage();
        primaryStage.setTitle(Assets.TheMachineTerminal);
        allLines = new VBox(5);
        allLines.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.BLACK, CornerRadii.EMPTY, javafx.geometry.Insets.EMPTY)));
        allLines.setBorder(new Border(new BorderStroke(javafx.scene.paint.Color.TRANSPARENT, BorderStrokeStyle.NONE, CornerRadii.EMPTY, BorderWidths.EMPTY)));
        scrollPane = new javafx.scene.control.ScrollPane(allLines);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.ALWAYS);
        scene = new Scene(scrollPane, INITIAL_WINDOW_WIDTH, INITIAL_WINDOW_HEIGHT, javafx.scene.paint.Color.WHITE);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest((v) -> {
            currentlyOpen = false;
            allLines.getChildren().clear();
        });
    }

    private IdentityDataBaseFile identityDataBaseFile;

    public QUIShell(IdentityDataBaseFile id) {

        currentlyOpen = false;
        try {
            machineTerminalFont = javafx.scene.text.Font.loadFont(new FileInputStream(new File(Assets.CALL_ONE_REGULAR)), 10f);
        } catch(FileNotFoundException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Font extraction error, exiting the Machine.", ButtonType.OK);
            alert.showAndWait();
            System.exit(1);
        }
    }

    public void clearTerminal() {
        allLines.getChildren().clear();
        initializeNewLine("#>");
    }

    public void closeTerminal() {
        primaryStage.close();
        currentlyOpen = false;
        allLines.getChildren().clear();
    }

    private String getPromptString(Permissions.PermissionLevel pl) {
        if(pl == Permissions.PermissionLevel.KIND_LOCKED)
            return "";
        else if(pl == Permissions.PermissionLevel.KIND_REGULAR)
            return "! >";
        else if(pl == Permissions.PermissionLevel.KIND_READ)
            return "^ >";
        else if(pl == Permissions.PermissionLevel.KIND_READ_WRITE)
            return "* >";
        else if(pl == Permissions.PermissionLevel.KIND_READ_WRITE_MODIFY)
            return "# >";
        return "";
    }

    private void handleEnteredCommand(String command) {
        if(command.length() == 0) {
            initializeNewLine("#>");
            return;
        }
        String[] keywords = command.split(" ");
        java.util.List<String> keys = new ArrayList<>(Arrays.asList(keywords));
        HBox box = new HBox();
        Text text = new Text();
        text.setFont(machineTerminalFont);
        text.setTranslateY(2);

        text.setFill(javafx.scene.paint.Color.WHITE);
        if(contains(keys, "HI")){
            text.setText("Hi, Admin!");
        }
        else if(contains(keys, "TIME")){
            text.setText(new Date().toString());
        }
        else if(contains(keys, "THREAT") && contains(keys, "WHO")){

            ArrayList<Identity> ids = StartUpAnimation.wh.getIdentityList();

            for(Identity id : ids){
                if(id.getPermissionsKind() == Identity.PermissionsKind.KIND_THREAT){
                    text.setText(" ======= THREAT ======\n\n" +
                            "Name: " + id.getUserName() + "\n\nSSN: " + id.getSocialSecurityNumber() +
                    "\n\nAbout: " + id.getAbout());
                }
            }



        }
        else if(contains(keys, "EXIT")){
            closeTerminal();
            this.currentlyOpen = false;
        }
        else if(contains(keys, "SHUTDOWN")){
            closeTerminal();
            StartUpAnimation.wh.terminal.handleEnteredCommand("#>", "killall");
        }
        else if(contains(keys, "SSN")){
            if(keys.contains("di")){
                String nome = keys.get(keys.indexOf("of") + 1);
                ArrayList<Identity> ids = StartUpAnimation.wh.getIdentityList();
                for(Identity id : ids){
                    if(id.getUserName().equalsIgnoreCase(nome)){
                        text.setText("\n\nSSN: " + id.getSocialSecurityNumber());
                    }
                }

            }
        }
        else if(contains(keys, "PROFILE")){
            if(keys.contains("of")) {
                String nome = keys.get(keys.indexOf("of") + 1);
                ArrayList<Identity> ids = StartUpAnimation.wh.getIdentityList();
                for(Identity id : ids){
                    if(id.getUserName().equalsIgnoreCase(nome)){
                        
                        
                        text.setText("\n\n===== PROFILE =====\n\n" +
                                "Name: " + id.getUserName() + "\n\nSSN: " + id.getSocialSecurityNumber() +
                        "\n\nAbout: " + id.getAbout() + "\n\nRole: " + id.getPermissionsString() + "" +
                                "\n\nUUID: " + id.getGeneratedId());

                        

                        
                    }
                    else if(id.getSocialSecurityNumber().equalsIgnoreCase(nome)){
                        text.setText("\n\n===== PROFILE =====\n\n" +
                                "Name: " + id.getUserName() + "\n\nSSN: " + id.getSocialSecurityNumber() +
                                "\n\nAbout: " + id.getAbout() + "\n\nRole: " + id.getPermissionsString() + "\n\nUUID: " + id.getGeneratedId());
                        
                    }
                }
            }
        }
        else if(contains(keys, "CLEAR")){
            clearTerminal();
        }
       



        box.getChildren().add(text);
        allLines.getChildren().add(box);
        initializeNewLine("#>");


        }



    private void initializeNewLine(String promptString) {
        currentLine = new HBox();
        currentLine.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.BLACK, CornerRadii.EMPTY, javafx.geometry.Insets.EMPTY)));
        currentPrompt = new javafx.scene.control.Label(promptString);
        currentPrompt.setFont(machineTerminalFont);
        currentPrompt.setTextFill(javafx.scene.paint.Color.WHITE);
        currentPrompt.setTranslateY(2);
        currentPrompt.setFocusTraversable(false);
        currentCommandEntry = new javafx.scene.control.TextField();
        currentCommandEntry.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.BLACK, CornerRadii.EMPTY, javafx.geometry.Insets.EMPTY)));
        currentCommandEntry.setBorder(new Border(new BorderStroke(javafx.scene.paint.Color.TRANSPARENT, BorderStrokeStyle.NONE, CornerRadii.EMPTY, BorderWidths.EMPTY)));
        currentCommandEntry.setStyle("-fx-text-inner-color: white;");
        currentCommandEntry.setFont(machineTerminalFont);
        currentCommandEntry.setPrefWidth(scrollPane.getWidth() - 10 - currentPrompt.getWidth() - 35);
        currentCommandEntry.requestFocus();
        currentCommandEntry.setTranslateY(-2);
        currentCommandEntry.setOnKeyPressed((event) -> {
            if(event.getCode().equals(KeyCode.ENTER)) {
                currentLine.getChildren().remove(currentCommandEntry);
                javafx.scene.control.Label label = new javafx.scene.control.Label(currentCommandEntry.getText());
                label.setTranslateX(2);
                label.setTranslateY(2);
                label.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.BLACK, CornerRadii.EMPTY, javafx.geometry.Insets.EMPTY)));
                label.setBorder(new Border(new BorderStroke(javafx.scene.paint.Color.TRANSPARENT, BorderStrokeStyle.NONE, CornerRadii.EMPTY, BorderWidths.EMPTY)));
                label.setTextFill(javafx.scene.paint.Color.WHITE);
                label.setFont(machineTerminalFont);
                label.setFocusTraversable(false);
                currentLine.getChildren().add(label);
                handleEnteredCommand(currentCommandEntry.getText());
            }
        });
        currentLine.setPadding(new javafx.geometry.Insets(0, 5, 0, 5));
        currentLine.getChildren().add(currentPrompt);
        currentLine.getChildren().add(currentCommandEntry);
        allLines.getChildren().add(currentLine);
        primaryStage.show();
    }

    private void createPrompt(boolean create, String promptString) {
        if(create)
            initializeNewLine(promptString);
        else {
            currentPrompt.setText(promptString);
            currentCommandEntry.setPrefWidth(scrollPane.getWidth() - 10 - currentPrompt.getWidth() - 35);
        }
    }

    public boolean getCurrentlyOpen() {
        return currentlyOpen;
    }

    public void showShell() {

        boolean store = currentlyOpen;
        if(!currentlyOpen)
            initialize();

        createPrompt(!store, "#>");
    }

    private boolean contains(java.util.List<String> list, String contains){

       java.util.List<String> uppers = new ArrayList<>();
        for(String s : list){
            uppers.add(s.toUpperCase());
        }

        return uppers.contains(contains);

    }
}

