package machine;

import edu.cmu.sphinx.api.SpeechResult;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import machine.command.*;
import org.opencv.videoio.VideoCapture;

import java.io.File;
import java.io.IOException;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;

/**
 * Created by nathr on 3/12/2016.
 */
public class MainPage extends Application {

    public final static double INITIAL_WINDOW_WIDTH = 500, INITIAL_WINDOW_HEIGHT = 500;

    @Override
    public void start(Stage primaryStage) {
        File openCvInCurrentPath = new File(System.getProperty("user.dir") + File.separator + "opencv" + File.separator
                + "build" + File.separator + "java" +File.separator + "x64" + File.separator + "opencv_java310.dll");

        File openCvInC = new File("C:" + File.separator + "opencv" + File.separator
                + "build" + File.separator + "java" +File.separator + "x64" + File.separator + "opencv_java310.dll");
        File fileToLoadFrom;
        if(openCvInCurrentPath.exists() && openCvInCurrentPath.canRead()){
            fileToLoadFrom = openCvInCurrentPath;
        } else if (openCvInC.exists() && openCvInC.canRead()) {
            fileToLoadFrom = openCvInC;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "OpenCV not found. Exiting...", ButtonType.OK);
            alert.showAndWait();
            System.exit(1);
            return;
        }

        System.load(fileToLoadFrom.getAbsolutePath());

        IdentityDataBaseFile identityDataBaseFile = new IdentityDataBaseFile();
        DispatchTable.COMMAND_DISPATCH_TABLE.put("adduser", new AddUserCommand(identityDataBaseFile));
        DispatchTable.COMMAND_DISPATCH_TABLE.put("retrain", new RetrainCommand(identityDataBaseFile));
        DispatchTable.COMMAND_DISPATCH_TABLE.put("rmuser", new RmUserCommand(identityDataBaseFile));
        DispatchTable.COMMAND_DISPATCH_TABLE.put("chperm", new ChPermCommand(identityDataBaseFile));
        DispatchTable.COMMAND_DISPATCH_TABLE.put("killall", new KillAllCommand(identityDataBaseFile));
        DispatchTable.COMMAND_DISPATCH_TABLE.put("exit", new ExitCommand(identityDataBaseFile));
        DispatchTable.COMMAND_DISPATCH_TABLE.put("lsuser", new LsUserCommand(identityDataBaseFile));
        DispatchTable.COMMAND_DISPATCH_TABLE.put("clear", new ClearCommand(identityDataBaseFile));
        DispatchTable.COMMAND_DISPATCH_TABLE.put("help", new HelpCommand(identityDataBaseFile));
        primaryStage.setTitle(Assets.TheMachine);
        primaryStage.setResizable(false);
        primaryStage.setTitle(Assets.TheMachine);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(Assets.Icon);
        StartUpAnimation sa = new StartUpAnimation(identityDataBaseFile);
        sa.createStartUpAnimation(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
