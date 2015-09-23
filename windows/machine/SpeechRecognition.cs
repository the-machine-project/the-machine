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
using System.Globalization;

namespace machine
{
    public partial class MainWindow : Window
    {
        String userSpeech = "Speech Recognition Offline!";
        bool speechRecognitionOnline = true;
        SpeechRecognitionEngine speechRecognitionEngine = new SpeechRecognitionEngine(new CultureInfo("en-US"));
    }
}
