using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Emgu.CV;
using Emgu.Util;
using Emgu.CV.Structure;
using System.Threading;
using System.Diagnostics;
using System.IO;
using System.Windows;
using System.Drawing.Text;
using Emgu.CV.Face;
using System.Windows.Media;
using System.Runtime.InteropServices;
using System.Windows.Media.Imaging;
using System.Speech.Recognition;

namespace machine
{
    public partial class MainWindow : Window
    {
        String userSpeech = "";
        bool speechRecognitionOnline = true;
        SpeechRecognitionEngine speechRecognitionEngine = new SpeechRecognitionEngine();

        private void InitializeSpeechRecognition()
        {
            if(speechRecognitionOnline)
            {
                speechRecognitionEngine.LoadGrammar(new Grammar(new GrammarBuilder("exit")));
                speechRecognitionEngine.LoadGrammar(new DictationGrammar());
                speechRecognitionEngine.SpeechRecognized += SpeechRecognized;
                speechRecognitionEngine.SetInputToDefaultAudioDevice();
                speechRecognitionEngine.RecognizeAsync(RecognizeMode.Multiple);
            }
        }

        private void SpeechRecognized(object sender, SpeechRecognizedEventArgs args) {
            userSpeech = args.Result.Text;
        }
    }
}
