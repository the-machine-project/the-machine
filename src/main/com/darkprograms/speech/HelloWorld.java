package com.darkprograms.speech;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;


import com.darkprograms.speech.microphone.Microphone;
import com.darkprograms.speech.recognizer.*;
import com.darkprograms.speech.synthesiser.Synthesiser;
import javaFlacEncoder.FLACFileWriter;
import org.bytedeco.javacpp.opencv_face;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/**
 * Jarvis Speech API Tutorial
 * @author Aaron Gokaslan (Skylion)
 *
 */
public class HelloWorld {

   


    
    public static void talk(String text){
        //String language = "auto";//Uses language autodetection
        //** While the API can detect language by itself, this is reliant on the Google Translate API which is prone to breaking. For maximum stability, please specify the language.**//
        String language = "en-us";//English (US) language code   //If you want to specify a language use the ISO code for your country. Ex: en-us
    /*If you are unsure of this code, use the Translator class to automatically detect based off of
    * Either text from your language or your system settings.
    */
        Synthesiser synth = new Synthesiser(language);

        try {
            InputStream is = synth.getMP3Data(text);
            AudioStream as = new AudioStream(is);
            AudioPlayer.player.start(as);

            //TODO Use any Java MP3 Implementation to play back the AudioFile from the InputStream.
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("Error");
            e.printStackTrace();

            return;
        }
    }
}
