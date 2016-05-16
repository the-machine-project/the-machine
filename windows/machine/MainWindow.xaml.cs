/* By devloop0 (Nikhil Athreya) */
/***
    This file is the "main window" of the application, which brings together everything from the MachineHeuristics.cs file.
    This software is licensed under the MIT License.
    The purpose of this software is to be a simulation of "the Machine" from the *Person of Interest* show.

    MIT License:
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.
***/
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
using Microsoft.Win32;
using System.Windows.Threading;

namespace machine
{
    // MachineInputSourceKind is an Enum that contains the various possible input sources that the Machine can process
    public enum MachineInputSourceKind
    {
        SourceWebcam, SourceFile, SourceNone
    }

    // FaceIdentity is an Enum that contains the possible classifications for a person in the eyes of the Machine.
    public enum FaceIdentity
    {
        FaceAdmin, FaceAuxAdmin, FaceAsset, FaceSecondary, FaceContingency, FaceThreat, FaceNone
    }

    public partial class MainWindow : Window
    {
        Capture capture; // The general capture object used to get frames from a camera or a video file.

        double frame_count; // The number of frames in a video.
        double frame_rate; // The frame rate in a video or a camera input.
        int theoretical_delay; // The theoretical delay between frames.
        int number_of_delayable_frames; // The number of frames to delay updating the facial detection.
        bool cameraResourceDisposal; // Whether or not to dispose of Mat's as they are processed.

        CascadeClassifier faceClassifier = null; // Holds the cascade classifier, in this case, the HaarCascade.
        Bitmap admin_focus = null; // The image of the Machine's admin box.
        Bitmap secondary_focus = null; // The image of the Machine's secondary box.
        Bitmap threat_focus = null;
        PrivateFontCollection privateFontCollection = null; // Stores the Machine's Font Collection.
        Font font = null; // The font used by the Machine to display text (Call-One Regular).
        Font speechFont = null;
        const int scale_factor = 4; // Reduce the size of the image before the ANN detects a face by this much.
        const int aesthetics_factor = 10; // Increase the size of the box surrounding a face by this many pixels for a more aesthetically pleasing look.
        const double box_magnification = 1.2; // Increases the height of the box by this factor.
        int counter = 0; // Counts the number of frames for determining facial processing delays.
        FaceIdentity[] focusKindDatabase = null; // The list of classifications of faces from the ASSET_INDEX.dat file.
        String[] nameDatabase = null; // The list of names from the ASSET_INDEX.dat file.
        int facialRecognitionHeight = 0; // The image height to be used for facial recognition (not during training) from the CORE_IMAGE_DATA.dat file.
        int facialRecognitionWidth = 0; // The image width to be used for the facial recognition (not during training) from the CORE_IMAGE_DATA.dat file.
        FaceIdentity faceIdentityTraining = FaceIdentity.FaceNone; // The identity of the face during training.
        String nameTraining = null; // The name associated with a person's face during training.
        LBPHFaceRecognizer[] lbphFaceRecognizerList = null; // The list of ANN's used for facial recognition.
        FaceIdentity[] focusKindList = null; // The list of classifications of faces used during the ProcessVideoFrame function.
        String[] nameList = null; // The list names used during the the ProcessVideoFrame function.
        Rectangle[] faceList = null; // The list of detected faces within the bounds of these rectangles used during the ProcessVideoFrame function.
        int cameraNumber = 0; // The camera number used by the 'capture' variable.
        int cameraFrameRate = 0; // The fps of the camera (not used when processing a video file).
        MachineInputSourceKind machineInputSourceKind = MachineInputSourceKind.SourceNone; // The input kind which the Machine has to process.
        public const String TrainTitle = "The Machine - Training Mode"; // The title of the window when the Machine is training itself to recognize a face.
        public const String ExecutionTitle = "The Machine"; // The title of the window when the Machine is normally running.
        MachineStartPage machineStartPage = null;
        List<FaceIdentity> faceIdentityCommunication = new List<FaceIdentity>();
        UIKind uikind = UIKind.UINone;
        String fileName = null;

        /***
            Function: public MainWindow(bool train, FaceIdentity fit, String nt, int cn, int fps, bool crd, int mdt, MachineInputSourceKind misk)
            Parameter(s): bool train
                Whether or not the machine is supposed to be training itself.
                          FaceIdentity fit
                The classification of the person that the Machine is training itself to recognize (supposed to be equal to FaceIdentity.FaceNone during
                normal execution).
                          String nt
                The name of the person that the Machine is training itself to recognize (supposed to be equal to "" during normal execution).
                          int cn
                The camera number to used by the 'capture' variable.
                          int fps
                The frame rate of the camera (not used when processing video files).
                          bool crd
                Whether or not the Machine should dispose of resources (could potentially be unstable).
                          int mdt
                The maxDistanceThreshold to use when classifying faces.
                           MachineInputSourceKind misk
                The input source from which the Machine will gather its input.
                           String t
                The title of this window.
                            MachineStartPage msp
                A reference to the main machine window to control.
            Return Value: N/A (Constructor)
        ***/
        public MainWindow(bool train, FaceIdentity fit, String nt, int cn, int fps, bool crd, int mdt,
            MachineInputSourceKind misk, String t, MachineStartPage msp, UIKind uk)
        {
            machineStartPage = msp;
            cameraResourceDisposal = crd;
            maxDistanceThreshold = mdt;
            cameraNumber = cn;
            cameraFrameRate = fps;
            machineInputSourceKind = misk;
            if (misk == MachineInputSourceKind.SourceFile || misk == MachineInputSourceKind.SourceNone || train)
                speechRecognitionOnline = true;
            else
                speechRecognitionOnline = true;
            userSpeech = "";
            FileUtilities.DirectoryCreation();
            FileUtilities.TrainingDirectoryCreation();
            faceClassifier = new CascadeClassifier("assets\\haarcascade_frontalface_alt.xml");
            uikind = uk;
            if (uikind == UIKind.UIMachine) {
                admin_focus = new Bitmap("assets\\machine\\admin_focus.jpg");
                secondary_focus = new Bitmap("assets\\machine\\secondary_focus.jpg");
                threat_focus = new Bitmap("assets\\machine\\threat_focus.jpg");
            }
            else
            {
                admin_focus = new Bitmap("assets\\samaritan\\deviant_focus.jpg");
                secondary_focus = new Bitmap("assets\\samaritan\\irrelevant_focus.jpg");
                threat_focus = new Bitmap("assets\\samaritan\\threat_focus.jpg");
            }
            machineIsTraining = train;
            //Check if the CORE_IMAGE_DATA.dat file exists and read the predefined width and height.
            if (File.Exists(FileUtilities.DirectoryName + "\\" + FileUtilities.CoreImageData))
            {
                String[] lines = File.ReadAllLines(FileUtilities.DirectoryName + "\\" + FileUtilities.CoreImageData);
                if (lines.Length != 0)
                {
                    String[] heightWidth = lines[0].Split('|');
                    facialRecognitionHeight = Int32.Parse(heightWidth[0]);
                    facialRecognitionWidth = Int32.Parse(heightWidth[1]);
                }
            }
            FileUtilities.CoreImageDataCreation();
            // Check if the Machine is being trained or not.
            if (!train && fit == FaceIdentity.FaceNone && nt == "")
            {
                String[] lines2 = File.ReadAllLines(FileUtilities.DirectoryName + "\\" + FileUtilities.AssetIndexData);
                List<FaceIdentity> faceIdentityList = new List<FaceIdentity>();
                List<String> nList = new List<String>();
                List<LBPHFaceRecognizer> lbphList = new List<LBPHFaceRecognizer>();
                // Load the trained neural networks, list of names, and classifications.
                foreach (String line in lines2)
                {
                    String[] innerSplit = line.Split('^');
                    String name = innerSplit[0];
                    String identifier = innerSplit[1];
                    String file = innerSplit[2];
                    if (identifier == adminIdentifier)
                        faceIdentityList.Add(FaceIdentity.FaceAdmin);
                    else if (identifier == auxAdminIdentifier)
                        faceIdentityList.Add(FaceIdentity.FaceAuxAdmin);
                    else if (identifier == assetIdentifier)
                        faceIdentityList.Add(FaceIdentity.FaceAsset);
                    else
                        PanicAndTerminateProgram();
                    nList.Add(name);
                    LBPHFaceRecognizer lbph = new LBPHFaceRecognizer();
                    lbph.Load(file);
                    lbphList.Add(lbph);
                }
                focusKindDatabase = faceIdentityList.ToArray();
                nameDatabase = nList.ToArray();
                lbphFaceRecognizerList = lbphList.ToArray();
                // Check to make sure the ANN, name, and classification database lengths are all equal.
                if (focusKindDatabase.Length == nameDatabase.Length && nameDatabase.Length == lbphFaceRecognizerList.Length) ;
                else
                    PanicAndTerminateProgram();
            }
            faceIdentityTraining = fit;
            nameTraining = nt;
            InitializeComponent();
            this.Title = t;

            Closed += (object sender, EventArgs args) =>
            {
                Environment.Exit(1);
            };
        }

        /***
            Function: private Capture InitCapture()
            Parameter(s): None
            Return Value: Capture
                Constructs an EmguCV Capture from a path that was selected in a file dialog.
                This presumes that the user wants to perform facial recognition on a video.
        ***/
        private Capture InitCapture()
        {
            OpenFileDialog openFileDialog = new OpenFileDialog();

            // Currently accepted file names are *.avi
            // More filenames are probably suppored, but have not been tested yet.
            openFileDialog.Filter = "AVI Files (*.avi)|*.avi";
            openFileDialog.InitialDirectory = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments);

            // Kill the dialog box if no file was selected.
            bool? res = openFileDialog.ShowDialog();
            if (!res.HasValue || !((bool)res))
                PanicAndTerminateProgram();

            fileName = openFileDialog.FileName;
            return new Capture(fileName);
        }

        /***
            Function: private Rectangle ConstructScaledRectangle(Rectangle original)
            Parameter(s): Rectangle original
                Rectangle original: The original Rectangle from which the face was detected. This is multipled by the scale_factor
                and performs math with the aesthetics_factor and box_magnification variable.
            Return Value: Rectangle
                Returns the scaled Rectangle which is bigger than the original Rectangle.
        ***/
        private Rectangle ConstructScaledRectangle(Rectangle original)
        {
            // Make sure that the subtraction of the position is still positive
            if (uikind == UIKind.UISamaritan)
            {
                int width = (int)(original.Width * scale_factor * box_magnification) * (scale_factor / 2);
                int height = (int)(original.Height * scale_factor * box_magnification) * (scale_factor / 2);
                return new Rectangle(original.X * scale_factor - width / scale_factor > 0 ? original.X * scale_factor - width / scale_factor : 0,
                    original.Y * 2 - height * 2 > 0 ? original.Y * 2 - height * 2 : 0,
                    width, height);
            }
            else
                return new Rectangle(original.X * scale_factor - aesthetics_factor > 0 ? original.X * scale_factor - aesthetics_factor : 0, original.Y * scale_factor - aesthetics_factor > 0 ? original.Y * scale_factor - aesthetics_factor : 0,
                    (int)(original.Width * scale_factor * box_magnification), (int)(original.Height * scale_factor * box_magnification));
        }

        /***
            Function: private Point ConstructScaledPoint(Rectangle original)
            Parameter(s): Rectangle original
                Rectangle original: THe original Rectangle from which the face was detected. This constructs the X and Y values only
                for a Point.
            Return Value: Point
                Returns the scaled Point from the original Rectangle.
        ***/
        private System.Drawing.Point ConstructScaledPoint(Rectangle original)
        {
            // The scaled rectangle complicates the math needed to make sure the iamge is correctly position
            if(uikind == UIKind.UIMachine)
            {
                int tempX = (original.X * scale_factor - aesthetics_factor > 0 ? original.X * scale_factor - aesthetics_factor : 0) + ((int)(original.Width * scale_factor * box_magnification));
                int X = tempX + aesthetics_factor;
                int tempHeight = ((int)(original.Height * scale_factor * box_magnification));
                int tempY2 = (original.Y * scale_factor - aesthetics_factor > 0 ? original.Y * scale_factor - aesthetics_factor : 0);
                int Y = tempY2 + tempHeight / 6;
                return new System.Drawing.Point(X, Y);
            }
            else if(uikind == UIKind.UISamaritan)
            {
                int tempWidth = (int)(original.Width * scale_factor * box_magnification) * (scale_factor / 2);
                int tempX = original.X * scale_factor - tempWidth / scale_factor > 0 ? original.X * scale_factor - tempWidth / scale_factor : 0;
                int X = tempX + 9 * tempWidth / 10;
                int tempHeight = (int)(original.Height * scale_factor * box_magnification) * (scale_factor / 2);
                int tempY2 = original.Y * 2 - tempHeight * 2 > 0 ? original.Y * 2 - tempHeight * 2 : 0;
                int Y = tempY2 + tempHeight / 6;
                return new System.Drawing.Point(X, Y);
            }
            return new System.Drawing.Point(-1, -1);
        }

        private System.Drawing.Point SpeechScaledPoint(System.Drawing.Point scaledPoint)
        {
            return new System.Drawing.Point(scaledPoint.X, scaledPoint.Y + (int) (aesthetics_factor * 2.5));
        }
        /***
            Function: private void InitializeMachineFont()
            Parameter(s):
            Return Value: void
                Makes sure that the font for displaying text in the Machine is accessible to the application.
        ***/
        private void InitializeMachineFont()
        {
            privateFontCollection = new PrivateFontCollection();
            if (uikind == UIKind.UIMachine)
                privateFontCollection.AddFontFile("assets\\machine_font.ttf");
            else if (uikind == UIKind.UISamaritan)
                privateFontCollection.AddFontFile("assets\\samaritan_font.otf");
            FontFamily[] fontFamilyArray = privateFontCollection.Families;
            font = new Font(fontFamilyArray[0], 14, System.Drawing.FontStyle.Regular);
            speechFont = new Font(fontFamilyArray[0], 20, System.Drawing.FontStyle.Regular);
            if (font == null)
                PanicAndTerminateProgram();
        }
        
        /***
            Function: private void MachineExecution()
            Parameter(s):
            Return Value: void
                This is where the main execution of the program takes place. Most of the code called from this function
                resides in the MachineHeuristics.cs file with the facial detection and recognition processing.
        ***/
        private void MachineExecution()
        {
            if (machineInputSourceKind == MachineInputSourceKind.SourceNone)
                PanicAndTerminateProgram();
            if (machineInputSourceKind == MachineInputSourceKind.SourceFile)
            {
                capture = InitCapture();
                frame_count = capture.GetCaptureProperty(Emgu.CV.CvEnum.CapProp.FrameCount);
                frame_rate = capture.GetCaptureProperty(Emgu.CV.CvEnum.CapProp.Fps);
                theoretical_delay = (int)(1000 / frame_rate);
                number_of_delayable_frames = (int)(frame_rate / 2);
                dispatcherTimer = new DispatcherTimer(
                        TimeSpan.FromMilliseconds(theoretical_delay),
                        DispatcherPriority.ApplicationIdle,
                        ProcessAnyFrame,
                        Application.Current.Dispatcher
                    );
                dispatcherTimer.Start();
            }
            else if (machineInputSourceKind == MachineInputSourceKind.SourceWebcam)
            {
                /***
                    Some computers have their default, working camera on a different internal number.
                    Note, zero is usually the correct number, and is actually the default in the InputSelection.xaml.cs file.
                ***/
                capture = new Capture(cameraNumber);

                // Locks the camera frame rate to a constant value.
                frame_rate = cameraFrameRate;
                capture.SetCaptureProperty(Emgu.CV.CvEnum.CapProp.Fps, frame_rate);
                theoretical_delay = (int)frame_rate;
                number_of_delayable_frames = (int)(frame_rate / 2);
                dispatcherTimer = new DispatcherTimer(
                        TimeSpan.FromMilliseconds(1000 / frame_rate),
                        DispatcherPriority.ApplicationIdle,
                        ProcessAnyFrame,
                        Application.Current.Dispatcher
                    );

                // Keep running the ProcessAnyFrame function every time a frame is received (theoretically), should work with no hitches.
                dispatcherTimer.Start();
            }
        }

        private void window_Loaded(object sender, RoutedEventArgs e)
        {
            // Make sure the font for the Machine is loaded.
            InitializeMachineFont();
            InitializeSpeechRecognition();

            // Courtesy of Pink Floyd:
            // Welcome my son;
            // Welcome, to the Machine!!!!
            // Where have you been?
            // It's alright we know where you've been!!!!
            MachineExecution();
        }

        public UIKind GetUIKind()
        {
            return uikind;
        }
    }
}