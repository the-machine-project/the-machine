package org.themachineproject.machine;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.themachineproject.machine.command.Command;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by nathr on 5/23/2016.
 */
public class Terminal {

    private Permissions.PermissionLevel permissionLevel;
    private Label currentPrompt;
    private TextField currentCommandEntry;
    private ScrollPane scrollPane;
    private HBox currentLine;
    private Scene scene;
    private Stage primaryStage;
    private VBox allLines;
    private boolean currentlyOpen;
    private Font machineTerminalFont;
    private Group root;

    public static double INITIAL_WINDOW_HEIGHT = 300;

    private void initialize() {
        currentlyOpen = true;
        permissionLevel = null;
        allLines = new VBox(5);
        allLines.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        scrollPane = new ScrollPane(allLines);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(INITIAL_WINDOW_HEIGHT);
        scrollPane.setPrefWidth(scene.getWidth());
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setTranslateY(scene.getHeight() - INITIAL_WINDOW_HEIGHT);
        scrollPane.setStyle("-fx-background: black;");
        root.getChildren().add(scrollPane);
    }

    private IdentityDataBaseFile identityDataBaseFile;

    public Terminal(IdentityDataBaseFile idbf, Stage s, Scene sc, Group g) {
        identityDataBaseFile = idbf;
        primaryStage = s;
        scene = sc;
        root = g;
        currentlyOpen = false;
        try {
            machineTerminalFont = Font.loadFont(new FileInputStream(new File(Assets.CALL_ONE_REGULAR)), 10f);
        } catch(FileNotFoundException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Font extraction error, exiting the Machine.", ButtonType.OK);
            alert.showAndWait();
            System.exit(1);
        }
    }

    public void clearTerminal() {
        allLines.getChildren().clear();
        initializeNewLine(getPromptString(permissionLevel));
    }

    public void closeTerminal() {
        root.getChildren().remove(scrollPane);
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

    private void handleEnteredCommand(String promptString, String command) {
        if(command.length() == 0) {
            initializeNewLine(promptString);
            return;
        }
        String[] input = command.split(" ");
        if(DispatchTable.COMMAND_DISPATCH_TABLE.containsKey(input[0])) {
            Command c = DispatchTable.COMMAND_DISPATCH_TABLE.get(input[0]);
            if(Permissions.getNumericPermissions(c.getMinimumPermissionLevel()) > Permissions.getNumericPermissions(permissionLevel)) {
                HBox errorLine = new HBox();
                errorLine.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
                Text t = new Text("Error!");
                t.setFont(machineTerminalFont);
                t.setFill(Color.RED);
                t.setTranslateY(2);
                Text t2 = new Text("    Command: '" + input[0] + "' cannot be run at this permission level.");
                t2.setFont(machineTerminalFont);
                t2.setFill(Color.WHITE);
                t2.setTranslateY(2);
                errorLine.getChildren().add(t);
                errorLine.getChildren().add(t2);
                allLines.getChildren().add(errorLine);
                initializeNewLine(promptString);
                return;
            }
            ArrayList<ArrayList<TextFragment>> arr = c.getCommandOutput(input);
            for(ArrayList<TextFragment> line : arr) {
                HBox l = new HBox();
                l.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
                for(TextFragment text : line) {
                    Text t = new Text(text.getText());
                    t.setFont(machineTerminalFont);
                    t.setFill(text.getColor());
                    t.setTranslateY(2);
                    l.getChildren().add(t);
                }
                allLines.getChildren().add(l);
            }
            initializeNewLine(promptString);
            return;
        }
        HBox errorLine = new HBox();
        errorLine.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        Text t = new Text("Error!");
        t.setFont(machineTerminalFont);
        t.setFill(Color.RED);
        t.setTranslateY(2);
        Text t2 = new Text("    Command: '" + input[0] + "' not found.");
        t2.setFont(machineTerminalFont);
        t2.setFill(Color.WHITE);
        t2.setTranslateY(2);
        errorLine.getChildren().add(t);
        errorLine.getChildren().add(t2);
        allLines.getChildren().add(errorLine);
        initializeNewLine(promptString);
    }

    private void initializeNewLine(String promptString) {
        currentLine = new HBox();
        currentLine.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        currentPrompt = new Label(promptString);
        currentPrompt.setFont(machineTerminalFont);
        currentPrompt.setTextFill(Color.WHITE);
        currentPrompt.setTranslateY(2);
        currentPrompt.setFocusTraversable(false);
        currentPrompt.setStyle("-fx-background: black;");
        currentCommandEntry = new TextField();
        currentCommandEntry.setStyle("-fx-background-color: black; -fx-text-inner-color: white;");
        currentCommandEntry.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.NONE, CornerRadii.EMPTY, BorderWidths.EMPTY)));
        currentCommandEntry.setFont(machineTerminalFont);
        currentCommandEntry.setPrefWidth(scrollPane.getWidth() - 10 - currentPrompt.getWidth() - 35);
        currentCommandEntry.requestFocus();
        currentCommandEntry.setTranslateY(-2);
        currentCommandEntry.setOnKeyPressed((event) -> {
            if(event.getCode().equals(KeyCode.ENTER)) {
                currentLine.getChildren().remove(currentCommandEntry);
                Label label = new Label(currentCommandEntry.getText());
                label.setTranslateX(2);
                label.setTranslateY(2);
                label.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
                label.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.NONE, CornerRadii.EMPTY, BorderWidths.EMPTY)));
                label.setTextFill(Color.WHITE);
                label.setFont(machineTerminalFont);
                label.setFocusTraversable(false);
                currentLine.getChildren().add(label);
                handleEnteredCommand(promptString, currentCommandEntry.getText());
            }
        });
        currentLine.setPadding(new Insets(0, 5, 0, 5));
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

    public void showTerminal(Permissions.PermissionLevel pl) {
        permissionLevel = pl;
        if(permissionLevel == Permissions.PermissionLevel.KIND_LOCKED)
            closeTerminal();
        boolean store = currentlyOpen;
        if(!currentlyOpen)
            initialize();
        String promptString = getPromptString(pl);
        createPrompt(!store, promptString);
    }
}
