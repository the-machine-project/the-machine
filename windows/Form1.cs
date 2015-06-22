/* By devloop0 (Nikhil Athreya) */
/***
    This file is a Windows Form for the main video rendering and processing.
    This software is licensed under the MIT License.
    The purpose of this software is to be a simulation of "the Machine" from the *Person of Interest" show.

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
using System.Windows.Forms;
using Emgu.CV;
using Emgu.Util;
using Emgu.CV.Structure;
using System.Threading;
using System.Diagnostics;
using System.Drawing.Text;
using NAudio;
using NAudio.Wave;

namespace machine
{
    // MachineInputSourceKind is an Enum that contains the various possible input sources that the Machine can process
    public enum MachineInputSourceKind
    {
        SourceWebcam, SourceFile, SourceNone
    }

    public partial class Form1 : Form
    {
        Capture capture; // The general capture object used to get frames from a camera or a video file

        double frame_count; // The number of frames in a video
        double frame_rate; // The frame rate in a video or a camera input
        int theoretical_delay; // The theoretical delay between frames
        int number_of_delayable_frames; // The number of frames to delay updating the facial detection
        bool cameraResourceDisposal; // Whether or not to dispose of Mat's as they are processed.

        CascadeClassifier faceClassifier; // Holds the cascade classifier, in this case, the HaarCascade
        Bitmap admin_focus; // The image of the Machine's admin box
        PrivateFontCollection privateFontCollection; // Stores the Machine's Font Collection
        Font font = null; // The font used by the Machine to display text (Call-One Regular)
        const int scale_factor = 4; // Reduce the size of the image before the ANN detects a face by this much
        const int aesthetics_factor = 10; // Increase the size of the box surrounding a face by this many pixels for a more aesthetically pleasing look
        const double box_magnification = 1.2; // Increases the height of the box by this factor
        int counter = 0; // Counts the number of frames for determining facial processing delays
        Rectangle[] cameraFaceList = null; // Stores the faces in a Bitmap for 1/2 second update for facial detection

        public Form1()
        {
            faceClassifier = new CascadeClassifier("assets\\haarcascade_frontalface_alt.xml");
            admin_focus = new Bitmap("assets\\admin_focus.jpg");
            InitializeComponent();
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
            privateFontCollection.AddFontFile("assets\\font.ttf");
            FontFamily[] fontFamilyArray = privateFontCollection.Families;
            font = new Font(fontFamilyArray[0], 10, FontStyle.Regular);
            if (font == null)
                throw new ArgumentNullException();
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

            // Currently accepted file names are *.mp3 and *.avi
            // More filenames are probably suppored, but have not been tested yet.
            openFileDialog.Filter = "AVI Files (*.avi)|*.avi";
            openFileDialog.InitialDirectory = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments);
            DialogResult dialogResult = openFileDialog.ShowDialog();
            if (dialogResult == DialogResult.OK)
                return new Capture(openFileDialog.FileName);
            return null;
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
        private Point ConstructScaledPoint(Rectangle original)
        {
            // The scaled rectangle complicates the math needed to make sure the iamge is correctly position
            int tempX = (original.X * scale_factor - aesthetics_factor > 0 ? original.X * scale_factor - aesthetics_factor : 0) + ((int)(original.Width * scale_factor * box_magnification));
            int X = tempX + aesthetics_factor;
            int tempHeight = ((int)(original.Height * scale_factor * box_magnification));
            int tempY2 = (original.Y * scale_factor - aesthetics_factor > 0 ? original.Y * scale_factor - aesthetics_factor : 0);
            int Y = tempY2 + tempHeight / 6;
            return new Point(X, Y);
        }

        /***
            Function: private Rectangle[] ProcessVideoFrame(Mat mat)
            Parameter(s): Mat mat
                Takes a EmguCV Mat which contains the Bitmap of the frame. 
                This function presumes that video frame passed should be facial processed.
            Return Value: Rectangle []
                Returns a Rectangle[] which contains the location of all of the faces.
                The Rectangle's returned are unscaled and need further processing.
        ***/
        private Rectangle[] ProcessVideoFrame(Mat mat)
        {
            Bitmap bitmap = mat.Bitmap;
            Graphics graphics = Graphics.FromImage(bitmap);

            // Resizes the image and converts it to greyscale in order to increase performance
            Image<Gray, byte> image = mat.ToImage<Gray, byte>().Resize(1 / (double) scale_factor, Emgu.CV.CvEnum.Inter.Linear);

            // Performs the actual face detection
            Rectangle[] faceList = faceClassifier.DetectMultiScale(image);
            foreach (Rectangle face in faceList)
            {
                Rectangle scaled_face = ConstructScaledRectangle(face);
                Bitmap scaled_admin_focus = new Bitmap(admin_focus, scaled_face.Size);
                graphics.DrawImage(scaled_admin_focus, scaled_face.Location);
                graphics.DrawString("ADMIN", font, new SolidBrush(Color.White), ConstructScaledPoint(face));
            }
            imagePanel.Image = bitmap;
            return faceList;
        }

        /***
            Function: private void ProcessDelayableFrame(Rectangle[] faceList, Mat mat)
            Parameters: Rectangle[] faceList, Mat mat
                Rectangle[] faceList: An unscaled array of Rectangle's that contain the location and dimensions
                of a Rectangle with a person's face.
                Mat mat: The current frame which to process. Note, this function presumes that the frame will not
                be facially processed.
            Return Value: void
        ***/
        private void ProcessDelayableFrame(Rectangle[] faceList, Mat mat)
        {
            Bitmap bitmap = mat.Bitmap;
            Graphics graphics = Graphics.FromImage(bitmap);

            /***
                The faceList parameter is not checked for a null-value,
                as the faceList in the private void ThreadFunction() function will be set
                to a non-null value in the first frame processed.
            ***/
            foreach (Rectangle face in faceList)
            {
                Rectangle scaled_face = ConstructScaledRectangle(face);
                Bitmap scaled_admin_focus = new Bitmap(admin_focus, scaled_face.Size);
                graphics.DrawImage(scaled_admin_focus, scaled_face.Location);
                graphics.DrawString("ADMIN", font, new SolidBrush(Color.White), ConstructScaledPoint(face));
            }
            imagePanel.Image = bitmap;
        }

        /***
            Function: private void ThreadFunction()
            Parameter(s):
            Return Value:
                This function is the function that combines the ProcessVideoFrame and ProcessDelayableFrame
                and is run inside the other thread of execution. 
                This function only applies to processing videos, and not camera frames.
        ***/
        private void ThreadFunction()
        {
            Mat mat = capture.QueryFrame();
            Rectangle[] faceList = null;

            // The Stopwatch is used to make sure all the frame timings are as accurate as possible.
            Stopwatch stopwatch = new Stopwatch();
            while (mat != null)
            {
                if (counter % number_of_delayable_frames == 0)
                {
                    stopwatch.Start();

                    // Makes sure that the faceList value passed to the ProcessDelayableFrame function is non-null.
                    faceList = ProcessVideoFrame(mat);
                    stopwatch.Stop();
                    if (stopwatch.ElapsedMilliseconds < theoretical_delay)
                        Thread.Sleep(theoretical_delay - (int)stopwatch.ElapsedMilliseconds);
                }
                else
                {
                    stopwatch.Start();
                    ProcessDelayableFrame(faceList, mat);
                    stopwatch.Stop();
                    if (stopwatch.ElapsedMilliseconds < theoretical_delay)
                        Thread.Sleep(theoretical_delay - (int)stopwatch.ElapsedMilliseconds);
                }
                mat = capture.QueryFrame();
                counter++;
                stopwatch.Reset();
            }
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            InitializeMachineFont();

            // Determines whether the user wants input from a video or a camera
            InputSelection inputSelection = new InputSelection();
            inputSelection.ShowDialog();
            MachineInputSourceKind machineInputSourceKind = inputSelection.GetMachineInputSourceKind();
            int cameraNumber = inputSelection.GetCameraNumber();
            int cameraFrameRate = inputSelection.GetCameraFrameRate();
            cameraResourceDisposal = inputSelection.GetResourceDisposal();
            if (machineInputSourceKind == MachineInputSourceKind.SourceNone)
                throw new NullReferenceException();
            if (machineInputSourceKind == MachineInputSourceKind.SourceFile)
            {
                capture = InitCapture();
                frame_count = capture.GetCaptureProperty(Emgu.CV.CvEnum.CapProp.FrameCount);
                frame_rate = capture.GetCaptureProperty(Emgu.CV.CvEnum.CapProp.Fps);
                theoretical_delay = (int)(1000 / frame_rate);
                number_of_delayable_frames = (int)(frame_rate / 2);
                Thread thread = new Thread(new ThreadStart(this.ThreadFunction));
                thread.IsBackground = true;
                thread.Start();
            }
            else if (machineInputSourceKind == MachineInputSourceKind.SourceWebcam)
            {
                try
                {
                    /***
                        Some computers have their default, working camera on a different internal number.
                        Note, zero is usually the correct number, and is actually the default in the InputSelection.cs file.
                    ***/
                    capture = new Capture(cameraNumber);

                    // Locks the camera frame rate to a constant value.
                    frame_rate = cameraFrameRate;
                    capture.SetCaptureProperty(Emgu.CV.CvEnum.CapProp.Fps, frame_rate);
                    theoretical_delay = (int) frame_rate;
                    number_of_delayable_frames = (int)(frame_rate / 2);

                    // When the application is idling, the application processes video frames.
                    Application.Idle += ProcessCameraFrame;
                }
                catch (NullReferenceException nullReferenceException)
                {
                    MessageBox.Show(nullReferenceException.Message);
                }
            }
        }

        /***
            Function: private void ProcessCameraFrame(object sender, EventArgs args)
            Parameters: object sender, EventArgs args
            Return Value: void
                Processes a frame; note, this function only applies for camera frames and not video frames.
        ***/
        private void ProcessCameraFrame(object sender, EventArgs args)
        {
            Mat mat = capture.QueryFrame();
            if (counter % number_of_delayable_frames == 0)
                cameraFaceList = ProcessVideoFrame(mat);
            else
                ProcessDelayableFrame(cameraFaceList, mat);
            counter++;
            if (cameraResourceDisposal)
                mat.Dispose();
        }
    }

    /***
        class: internal class ImagePanel: Panel
        This exists so that images for video frames can be centered.
        Note, the default needed to be modified a little bit in order for camera frames
        to be correctly sized and not repeated.
    ***/
    internal class ImagePanel : Panel
    {
        public ImagePanel()
        {
            this.DoubleBuffered = false;
            this.AutoScroll = true;
            this.BackgroundImageLayout = ImageLayout.Center;
        }
        public override Image BackgroundImage
        {
            get
            {
                return base.BackgroundImage;
            }

            set
            {
                base.BackgroundImage = value;
                if (value != null) this.AutoScrollMinSize = value.Size;
            }
        }
    }
}