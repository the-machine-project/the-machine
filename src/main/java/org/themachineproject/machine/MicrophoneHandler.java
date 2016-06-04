package org.themachineproject.machine;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by nathr on 6/1/2016.
 */
public class MicrophoneHandler {

    private boolean speechRecognitionOnline;
    private Text textToSet;
    private LiveSpeechRecognizer liveSpeechRecognizer;
    private Font speechRecognitionFont;

    public MicrophoneHandler(Text tts) {
        textToSet = tts;
        speechRecognitionOnline = true;
        try {
            speechRecognitionFont = Font.loadFont(new FileInputStream(new File(Assets.MAGDA_CLEAN_MONO)), 48f);
            Platform.runLater(() -> {
                textToSet.setFont(speechRecognitionFont);
                textToSet.setTranslateY(textToSet.getTranslateY() + textToSet.getLayoutBounds().getHeight());
            });
        }
        catch(IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Font extraction error, exiting the Machine.", ButtonType.OK);
            alert.showAndWait();
            System.exit(1);
        }
        try {
            Configuration configuration = new Configuration();
            configuration.setLanguageModelPath(Assets.SPEECH_LANGUAGE_MODEL_PATH);
            configuration.setDictionaryPath(Assets.SPEECH_DICTIONARY_PATH);
            configuration.setAcousticModelPath(Assets.SPEECH_ACOUSTIC_MODEL_PATH);

            liveSpeechRecognizer = new LiveSpeechRecognizer(configuration);
            liveSpeechRecognizer.startRecognition(true);
        }
        catch (IOException ex) {
            speechRecognitionOnline = false;
            Platform.runLater(() -> {
                textToSet.setText("Offline".toUpperCase());
                textToSet.setFill(Color.rgb(Assets.ThreatTextColor.getRed(), Assets.ThreatTextColor.getGreen(), Assets.ThreatTextColor.getGreen()));
            });
        }
    }

    public void startSpeechRecognition() {
        while(speechRecognitionOnline) {
            SpeechResult speechResult = liveSpeechRecognizer.getResult();
            if(speechResult != null)
                Platform.runLater(() -> {
                    textToSet.setText(speechResult.getHypothesis().toUpperCase());
                });
        }
    }

    public void stopSpeechRecognition() {
        speechRecognitionOnline = false;
    }
}
