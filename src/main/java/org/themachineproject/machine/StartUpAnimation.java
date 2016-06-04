package org.themachineproject.machine;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Created by nathr on 5/15/2016.
 */
public class StartUpAnimation {

    public final String[] startUpText = {
            "[!]:./action.SYSTEM.BOOT",
            "efiboot loaded from device: Acpi(PNP0A03,0)/Pci(1C|4)/Pci(0|0)/SATA0,",
            "SATA1,SATA2,SATA3, SigE01574-16D9-4F98-8D56488F7CCCC8)",
            "boot file path: \\System\\CoreServicesLibrary\\phx1\\tiny\\bootefil.efi",
            "",
            ".........................",
            "..........",
            ".........................................................",
            ".........................................................",
            ".........................................................",
            ".........................................................",
            "...........................",
            ".",
            ".",
            ".",
            ".",
            ".........",
            ".........",
            "....................................",
            ".........................................................",
            ".........................................................",
            "Administrator Supervisory Mode Executive Protection running",
            "Kernel Version 39.7.0: Day 0:",
            "vm_page_bootstrap: 4045336795451 free pages and 116200725 wired pages",
            "extensions submap [0xffffffff7f800a00000f - [0xfffffffb0000000000000]",
            "",
            "zone leak detection		enabled",
            "corruption leak detection	enabled",
            "distributed leak detection	enabled",
            "",
            "\"vm_compresser_mode\" is unlimited",
            "multi scheduler config: deep-drain 0, depth limit=unlimited",
            "standard timeslicing quantum is 10 uSec",
            "standard background quantum is 25 uSec",
            "con-cmd_table_max_displ = 999",
            "Deadline Timer variables supported and enabled",
            "calling mpo_policy_init for Sandbox",
            "Security policy loaded: Safety net for Sandbox",
            "calling mpo_policy_init for CPU_distro_PS3",
            "Security policy loaded: Safety net for CPU_distro_PS3",
            "calling mpo_policy_init for Seatbelt",
            "Security policy loaded: Seatbelt for CPU_distro_PS3",
            "calling mpo_policy_init for Quarantine",
            "Security policy loaded: Quarantine for CPU_distro_PS3",
            "Security Framework successfully initialized",
            "",
            "",
            "",
            "PhxACPICPU ProcessorId=1 LocalApicId=0 Enabled Starting---",
            "PhxACPICPU ProcessorId=2 LocalApicId=2 Enabled Starting---",
            "PhxACPICPU ProcessorId=3 LocalApicId=4 Enabled Starting---",
            "PhxACPICPU ProcessorId=4 LocalApicId=6 Enabled Starting---",
            "PhxACPICPU ProcessorId=5 LocalApicId=1 Enabled Starting---",
            "PhxACPICPU ProcessorId=6 LocalApicId=3 Enabled Starting---",
            "PhxACPICPU ProcessorId=7 LocalApicId=5 Enabled Starting---",
            "PhxACPICPU ProcessorId=8 LocalApicId=7 Enabled Starting---",
            "PhxACPICPU ProcessorId=9 LocalApicId=0 Enabled Starting---",
            "PhxACPICPU ProcessorId=10 LocalApicId=2 Enabled Starting---",
            "PhxACPICPU ProcessorId=11 LocalApicId=4 Enabled Starting---",
            "PhxACPICPU ProcessorId=12 LocalApicId=6 Enabled Starting---",
            "PhxACPICPU ProcessorId=13 LocalApicId=1 Enabled Starting---",
            "PhxACPICPU ProcessorId=14 LocalApicId=3 Enabled Starting---",
            "PhxACPICPU ProcessorId=15 LocalApicId=5 Enabled Starting---",
            "PhxACPICPU ProcessorId=16 LocalApicId=7 Enabled Starting---",
            "PhxACPICPU ProcessorId=17 LocalApicId=0 Enabled Starting---",
            "PhxACPICPU ProcessorId=18 LocalApicId=2 Enabled Starting---",
            "PhxACPICPU ProcessorId=19 LocalApicId=4 Enabled Starting---",
            "PhxACPICPU ProcessorId=20 LocalApicId=6 Enabled Starting---",
            "PhxACPICPU ProcessorId=21 LocalApicId=1 Enabled Starting---",
            "PhxACPICPU ProcessorId=22 LocalApicId=3 Enabled Starting---",
            "PhxACPICPU ProcessorId=23 LocalApicId=5 Enabled Starting---",
            "PhxACPICPU ProcessorId=24 LocalApicId=7 Enabled Starting---",
            "PhxACPICPU ProcessorId=25 LocalApicId=0 Enabled Starting---",
            "PhxACPICPU ProcessorId=26 LocalApicId=2 Enabled Starting---",
            "PhxACPICPU ProcessorId=27 LocalApicId=4 Enabled Starting---",
            "PhxACPICPU ProcessorId=28 LocalApicId=6 Enabled Starting---",
            "PhxACPICPU ProcessorId=29 LocalApicId=1 Enabled Starting---",
            "PhxACPICPU ProcessorId=30 LocalApicId=3 Enabled Starting---",
            "PhxACPICPU ProcessorId=31 LocalApicId=5 Enabled Starting---",
            "PhxACPICPU ProcessorId=32 LocalApicId=7 Enabled Starting---",
            "PhxACPICPU ProcessorId=33 LocalApicId=0 Enabled Starting---",
            "PhxACPICPU ProcessorId=34 LocalApicId=2 Enabled Starting---",
            "PhxACPICPU ProcessorId=35 LocalApicId=4 Enabled Starting---",
            "PhxACPICPU ProcessorId=36 LocalApicId=6 Enabled Starting---",
            "PhxACPICPU ProcessorId=37 LocalApicId=1 Enabled Starting---",
            "PhxACPICPU ProcessorId=38 LocalApicId=3 Enabled Starting---",
            "PhxACPICPU ProcessorId=39 LocalApicId=5 Enabled Starting---",
            "PhxACPICPU ProcessorId=40 LocalApicId=7 Enabled Starting---",
            "PhxACPICPU ProcessorId=41 LocalApicId=0 Enabled Starting---",
            "PhxACPICPU ProcessorId=42 LocalApicId=2 Enabled Starting---",
            "PhxACPICPU ProcessorId=43 LocalApicId=4 Enabled Starting---",
            "PhxACPICPU ProcessorId=44 LocalApicId=6 Enabled Starting---",
            "PhxACPICPU ProcessorId=45 LocalApicId=1 Enabled Starting---",
            "PhxACPICPU ProcessorId=46 LocalApicId=3 Enabled Starting---",
            "PhxACPICPU ProcessorId=47 LocalApicId=5 Enabled Starting---",
            "PhxACPICPU ProcessorId=48 LocalApicId=7 Enabled Starting---",
            "PhxACPICPU ProcessorId=49 LocalApicId=0 Enabled Starting---",
            "PhxACPICPU ProcessorId=50 LocalApicId=2 Enabled Starting---",
            "PhxACPICPU ProcessorId=51 LocalApicId=4 Enabled Starting---",
            "PhxACPICPU ProcessorId=52 LocalApicId=6 Enabled Starting---",
            "PhxACPICPU ProcessorId=53 LocalApicId=1 Enabled Starting---",
            "PhxACPICPU ProcessorId=54 LocalApicId=3 Enabled Starting---",
            "PhxACPICPU ProcessorId=55 LocalApicId=5 Enabled Starting---",
            "PhxACPICPU ProcessorId=56 LocalApicId=7 Enabled Starting---",
            "PhxACPICPU ProcessorId=57 LocalApicId=0 Enabled Starting---",
            "PhxACPICPU ProcessorId=58 LocalApicId=2 Enabled Starting---",
            "PhxACPICPU ProcessorId=59 LocalApicId=4 Enabled Starting---",
            "PhxACPICPU ProcessorId=60 LocalApicId=6 Enabled Starting---",
            "PhxACPICPU ProcessorId=61 LocalApicId=1 Enabled Starting---",
            "PhxACPICPU ProcessorId=62 LocalApicId=3 Enabled Starting---",
            "PhxACPICPU ProcessorId=63 LocalApicId=5 Enabled Starting---",
            "PhxACPICPU ProcessorId=64 LocalApicId=7 Enabled Starting---",
            "PhxACPICPU ProcessorId=65 LocalApicId=0 Enabled Starting---",
            "PhxACPICPU ProcessorId=66 LocalApicId=2 Enabled Starting---",
            "PhxACPICPU ProcessorId=67 LocalApicId=4 Enabled Starting---",
            "PhxACPICPU ProcessorId=68 LocalApicId=6 Enabled Starting---",
            "PhxACPICPU ProcessorId=69 LocalApicId=1 Enabled Starting---",
            "PhxACPICPU ProcessorId=70 LocalApicId=3 Enabled Starting---",
            "PhxACPICPU ProcessorId=71 LocalApicId=5 Enabled Starting---",
            "PhxACPICPU ProcessorId=72 LocalApicId=7 Enabled Starting---",
            "PhxACPICPU ProcessorId=73 LocalApicId=0 Enabled Starting---",
            "PhxACPICPU ProcessorId=74 LocalApicId=2 Enabled Starting---",
            "PhxACPICPU ProcessorId=75 LocalApicId=4 Enabled Starting---",
            "PhxACPICPU ProcessorId=76 LocalApicId=6 Enabled Starting---",
            "PhxACPICPU ProcessorId=77 LocalApicId=1 Enabled Starting---",
            "PhxACPICPU ProcessorId=78 LocalApicId=3 Enabled Starting---",
            "PhxACPICPU ProcessorId=79 LocalApicId=5 Enabled Starting---",
            "PhxACPICPU ProcessorId=80 LocalApicId=7 Enabled Starting---",
            "PhxACPICPU ProcessorId=81 LocalApicId=0 Enabled Starting---",
            "PhxACPICPU ProcessorId=82 LocalApicId=2 Enabled Starting---",
            "PhxACPICPU ProcessorId=83 LocalApicId=4 Enabled Starting---",
            "PhxACPICPU ProcessorId=84 LocalApicId=6 Enabled Starting---",
            "PhxA"
    };

    public final String[] loadingComponents = {
            "INITIALIZING BOOT SEQUENCE",
            "LOADING MACHINE.STARTUPANIMATION",

            "LOADING CORE FUNCTIONALITY",
            "LOADING MACHINE.MAINPAGE",
            "LOADING MACHINE.ASSETS",

            "INITIALIZING LOCAL DATABASE",
            "LOADING MACHINE.IDENTITYDATABASE",
            "LOADING MACHINE.IDENTITYENTRY",
            "LOADING MACHINE.IDENTITY",

            "INITIALIZING WEBCAM",
            "LOADING MACHINE.WEBCAMHANDLER",

            "LOADING PRE-EXISTING FACIAL DATA",
            "LOADING MACHINE.FACE",
            "LOADING MACHINE.RECOGNITIONMODE",

            "LOADING HIGHER-ORDER FUNCTIONS",
            "LOADING MACHINE.FACIALDETECTION",
            "LOADING MACHINE.FACIALRECOGNITION",

            "LOADING UI ELEMENTS",
            "FINALIZING BOOT SEQUENCE",
            "STARTING THE MACHINE"
    };

    private IdentityDataBaseFile identityDataBaseFile;

    public StartUpAnimation(IdentityDataBaseFile idbf) {
        identityDataBaseFile = idbf;
    }

    private String assembleStartUpString() {
        String first = "";
        for(String s : startUpText)
            first += s + "\n";
        first = first.substring(0, first.length() - 1);
        return first;
    }

    public void createStartUpAnimation(Stage primaryStage) {
        Group root = new Group();
        Scene scene = new Scene(root, MainPage.INITIAL_WINDOW_WIDTH, MainPage.INITIAL_WINDOW_HEIGHT, Color.BLACK);
        String start = assembleStartUpString();

        IntegerProperty text = new SimpleIntegerProperty(0);
        double textHeight = new Text("q").getLayoutBounds().getHeight();
        Label label = new Label();
        label.setLayoutX(5);
        label.setLayoutY(5);
        label.setTextFill(Color.WHITE);
        Font font = null;
        try {
            font = Font.loadFont(new FileInputStream(new File(Assets.CALL_ONE_REGULAR)), 8f);
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Font extraction error, exiting the Machine.", ButtonType.OK);
            alert.showAndWait();
            System.exit(1);
        }
        label.setFont(font);
        text.addListener((a, b, c) -> {
            label.setText(start.substring(0, c.intValue()));
            if(label.getLayoutY() + label.getHeight() > MainPage.INITIAL_WINDOW_HEIGHT)
                label.setLayoutY(label.getLayoutY() - textHeight);
        });

        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(text, start.length());
        KeyFrame kf = new KeyFrame(Duration.seconds(5), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();
        timeline.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                root.getChildren().clear();
                hookToGraphConstruction(primaryStage, scene, root);
            }
        });

        root.getChildren().add(label);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.centerOnScreen();
    }

    private double[] generateGoodLines(Rectangle2D.Double exclude) {
        Random random = new Random();
        while(true) {
            double x1 = random.nextInt((int) MainPage.INITIAL_WINDOW_WIDTH), x2 = x1 + random.nextInt(50) * (Math.random() < 0.5 ? 1 : -1);
            double y1 = random.nextInt((int) MainPage.INITIAL_WINDOW_WIDTH), y2 = y1 + random.nextInt(50) * (Math.random() < 0.5 ? 1 : -1);
            if(!exclude.contains(x1, y1) && !exclude.contains(y1, y2) &&
                    !((x1 <= exclude.getX() && x2 >= exclude.getX() + exclude.getWidth()) || (x1 >= exclude.getX() + exclude.getWidth() && x2 <= exclude.getX())) &&
                    !((y1 <= exclude.getY() && y2 >= exclude.getY() + exclude.getHeight()) || (y1 >= exclude.getY() + exclude.getHeight() && y2 <= exclude.getY())))
                return new double[] { x1, y1, x2, y2 };
        }
    }

    private void animateLineWithExclusion(GraphicsContext gc, Rectangle2D.Double exclude) {
        double[] coords = generateGoodLines(exclude);
        double x1 = coords[0], y1 = coords[1], x2 = coords[2], y2 = coords[3];
        gc.fillOval(x1 - 2, y1 - 2, 4, 4);
        gc.fillOval(x2 - 2, y2 - 2, 4, 4);
        animateLine(gc, x1, y1, x2, y2, 100);
    }

    private void hookToGraphConstruction(Stage primaryStage, Scene scene, Group root) {
        Canvas canvas = new Canvas(MainPage.INITIAL_WINDOW_WIDTH, MainPage.INITIAL_WINDOW_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.WHITE);
        Font font = null;
        try {
            font = Font.loadFont(new FileInputStream(new File(Assets.MAGDA_CLEAN_MONO)), 0.5f);
        }
        catch(IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Font extraction error, exiting the Machine.", ButtonType.OK);
            alert.showAndWait();
            System.exit(1);
        }
        root.setScaleX(10);
        root.setScaleY(10);
        gc.setLineWidth(1);
        gc.setFill(Color.WHITE);
        String str = "Initializing...";
        Label label = new Label(str);
        Text temp = new Text(str);
        temp.setFont(font);
        double w = temp.getLayoutBounds().getWidth(), h = temp.getLayoutBounds().getHeight();
        label.setLayoutX(MainPage.INITIAL_WINDOW_WIDTH / 2 - w / 2);
        label.setLayoutY(MainPage.INITIAL_WINDOW_HEIGHT / 2 - h / 2);
        Rectangle2D.Double exclude = new Rectangle2D.Double(MainPage.INITIAL_WINDOW_WIDTH / 2 - w / 2, MainPage.INITIAL_WINDOW_HEIGHT / 2 - h / 2, w, h);
        label.setPadding(new Insets(0, 1, 1, 1));
        label.setFont(font);
        label.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        label.setTextFill(Color.BLACK);
        label.toFront();
        root.getChildren().addAll(label);
        root.getChildren().add(canvas);
        Timeline timeline = new Timeline();
        IntegerProperty amount = new SimpleIntegerProperty(0);
        KeyValue kv = new KeyValue(amount, 100);
        amount.addListener((a, b, c) -> {
            if(c.intValue() < 98)
                animateLineWithExclusion(gc, exclude);
        });
        KeyFrame kf = new KeyFrame(Duration.seconds(5), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();

        timeline.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                gc.setFill(Color.BLACK);
                gc.fillRect(0, 0, MainPage.INITIAL_WINDOW_WIDTH, MainPage.INITIAL_WINDOW_HEIGHT);
                root.getChildren().clear();
                hookToProgressBar(primaryStage, scene, root);
            }
        });
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(4), root);
        scaleTransition.setToX(1);
        scaleTransition.setToY(1);
        scaleTransition.play();
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(4), label);
        fadeTransition.setToValue(0);
        fadeTransition.play();
    }

    private Timeline animateLine(GraphicsContext gc, double x1, double y1, double x2, double y2, double millis) {
        Timeline timeline = new Timeline();
        IntegerProperty amount = new SimpleIntegerProperty(0);
        double xTimeStep = (x2 - x1) / 50,
                yTimeStep = (y2 - y1) / 50;
        KeyValue kv = new KeyValue(amount, 50);
        amount.addListener((a, b, c) -> {
            gc.strokeLine(x1, y1, x1 + c.intValue() * xTimeStep, y1 + c.intValue() * yTimeStep);
        });
        KeyFrame kf = new KeyFrame(Duration.millis(millis), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();
        return timeline;
    }

    private void hookToProgressBar(Stage primaryStage, Scene scene, Group root) {
        Canvas canvas = new Canvas(MainPage.INITIAL_WINDOW_WIDTH, MainPage.INITIAL_WINDOW_HEIGHT);
        root.getChildren().add(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.setStroke(Color.WHITE);
        Timeline t1 = animateLine(gc, MainPage.INITIAL_WINDOW_WIDTH / 2, MainPage.INITIAL_WINDOW_HEIGHT / 2 + 10, MainPage.INITIAL_WINDOW_WIDTH / 2 - 150, MainPage.INITIAL_WINDOW_HEIGHT / 2 + 10, 500);
        Timeline t2 = animateLine(gc, MainPage.INITIAL_WINDOW_WIDTH / 2, MainPage.INITIAL_WINDOW_HEIGHT / 2 + 10, MainPage.INITIAL_WINDOW_WIDTH / 2 + 150, MainPage.INITIAL_WINDOW_HEIGHT / 2 + 10, 500);
        t1.setOnFinished((v) -> {
            Timeline t3 = animateLine(gc, MainPage.INITIAL_WINDOW_WIDTH / 2 - 150, MainPage.INITIAL_WINDOW_HEIGHT / 2 + 10, MainPage.INITIAL_WINDOW_WIDTH / 2 - 150, MainPage.INITIAL_WINDOW_HEIGHT / 2 - 10, 200);
            t3.setOnFinished((_v) -> {
                animateLine(gc, MainPage.INITIAL_WINDOW_WIDTH / 2 - 150, MainPage.INITIAL_WINDOW_HEIGHT / 2 - 10, MainPage.INITIAL_WINDOW_WIDTH / 2, MainPage.INITIAL_WINDOW_HEIGHT / 2 - 10, 100);
            });
        });
        t2.setOnFinished((v) -> {
            Timeline t4 = animateLine(gc, MainPage.INITIAL_WINDOW_WIDTH / 2 + 150, MainPage.INITIAL_WINDOW_HEIGHT / 2 + 10, MainPage.INITIAL_WINDOW_WIDTH / 2 + 150, MainPage.INITIAL_WINDOW_HEIGHT / 2 - 10, 200);
            t4.setOnFinished((_v) -> {
                Timeline t5 = animateLine(gc, MainPage.INITIAL_WINDOW_WIDTH / 2 + 150, MainPage.INITIAL_WINDOW_HEIGHT / 2 - 10, MainPage.INITIAL_WINDOW_WIDTH / 2, MainPage.INITIAL_WINDOW_HEIGHT / 2 - 10, 100);
                t5.setOnFinished((__v) -> {
                    hookToProgressBarStatus(primaryStage, scene, root, gc);
                });
            });
        });
    }

    private void hookToProgressBarStatus(Stage primaryStage, Scene scene, Group root, GraphicsContext gc) {
        Font font = null, subFont = null;
        try {
            font = Font.loadFont(new FileInputStream(new File(Assets.MAGDA_CLEAN_MONO)), 14f);
            subFont = Font.loadFont(new FileInputStream(new File(Assets.MAGDA_CLEAN_MONO)), 12f);
        }
        catch(IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Font extraction error, exiting the Machine.", ButtonType.OK);
            alert.showAndWait();
            System.exit(1);
        }
        Text temp = new Text("q");
        temp.setFont(font);
        double h = temp.getLayoutBounds().getHeight(), w = temp.getLayoutBounds().getWidth();
        Label label = new Label("LOADING_");
        label.setFont(font);
        label.setLayoutX(MainPage.INITIAL_WINDOW_WIDTH / 2 - 150);
        label.setLayoutY(MainPage.INITIAL_WINDOW_HEIGHT / 2 - 12 - 2 * h - 12);
        label.setTextFill(Color.BLACK);
        label.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        label.setPadding(new Insets(2, 5, 2, 5));
        root.getChildren().add(label);
        Label subText = new Label(loadingComponents[0]);
        subText.setFont(font);
        subText.setTextFill(Color.rgb(238, 233, 60));
        subText.setLayoutX(MainPage.INITIAL_WINDOW_WIDTH / 2 - 150);
        subText.setLayoutY(MainPage.INITIAL_WINDOW_HEIGHT / 2 - 12 - h - 5);
        root.getChildren().add(subText);
        hookToFlickeringLoadingText(label);
        double incrementSteps = 300.0 / loadingComponents.length;
        Timeline timeline = new Timeline();
        IntegerProperty amount = new SimpleIntegerProperty(0);
        KeyValue kv = new KeyValue(amount, loadingComponents.length - 1);
        amount.addListener((a, b, c) -> {
            subText.setText(loadingComponents[c.intValue()]);
            gc.fillRect(MainPage.INITIAL_WINDOW_WIDTH / 2 - 150, MainPage.INITIAL_WINDOW_HEIGHT / 2 - 10,  incrementSteps * (c.doubleValue() + 1), 20);
        });
        KeyFrame kf = new KeyFrame(Duration.seconds(3), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();
        timeline.setOnFinished((v) -> {
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, MainPage.INITIAL_WINDOW_WIDTH, MainPage.INITIAL_WINDOW_HEIGHT);
            root.getChildren().clear();
            hookToMainApp(primaryStage, scene, root);
        });
    }

    private void hookToFlickeringLoadingText(Label label) {
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);
        class Temp { boolean b = false; }
        Temp temp = new Temp();
        KeyFrame keyFrame = new KeyFrame(Duration.millis(500), (event) -> {
            if(temp.b)
                label.setText("LOADING_");
            else
                label.setText("LOADING ");
            temp.b = !temp.b;
        });
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    private void hookToMainApp(Stage primaryStage, Scene scene, Group root) {
        Platform.runLater(() -> {
            if (identityDataBaseFile.getRecognitionMode() == RecognitionMode.MODE_INITIAL_TRAINING ||
                    identityDataBaseFile.getRecognitionMode() == RecognitionMode.MODE_TRAINING)
                new IdentityEntry(identityDataBaseFile);
            WebcamHandler webcamHandler = new WebcamHandler(primaryStage, scene, root, identityDataBaseFile);
            webcamHandler.grabWebcamOutput();
            primaryStage.setOnCloseRequest((WindowEvent windowEvent) -> {
                webcamHandler.exit();
                System.exit(1);
            });
        });
    }
}
