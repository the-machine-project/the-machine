/* By devloop0 (Nikhil Athreya) */
/***
    This file contains the heuristic parts of the machine like the facial detection and recognition algorithms.
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
using System.Windows.Media;
using System.Runtime.InteropServices;
using System.Windows.Media.Imaging;
using System.Windows.Threading;
using System.Windows.Controls;

namespace machine
{
    public partial class MainWindow : Window
    {
        Dictionary<int, String> categories = new Dictionary<int, string>()
        {
            { 0, "ADMIN" },
            { 1, "AUX_ADMIN" },
            { 2, "ASSET" },
            { 3, "SECONDARY" },
            { -1, "TRAINING_ADMIN" },
            { -2, "TRAINING_AUX_ADMIN" },
            { -3, "TRAINING_ASSET" }
        }; // Define the set of possible categories that a user can be in.

        const int predefinedSampleImagesCount = 200; // The number of training samples gathered by the Machine, fixed to 200 images.
        bool machineIsTraining = false; // True if the user is training the Machine, false if the user is not training the Machine.
        int imageSamplesCounter = 0; // A counter for the number of samples gathered for training.
        int maxDistanceThreshold = 40; // The default distance threshold from the camera.
        const String adminIdentifier = "ADMIN"; // Identifier for an Admin.
        const String assetIdentifier = "ASSET"; // Identifier for an Asset.
        const String auxAdminIdentifier = "AUX_ADMIN"; // Identifier for an auxiliary admin.
        const String contingencyIdentifier = "CONTINGENCY"; // Identifier for a contigency.
        const String threatIdentifier = "THREAT"; // Identifier for a threat.
        const String secondaryIdentifier = "SECONDARY"; // Identifier for a secondary.
        DispatcherTimer dispatcherTimer = null; // Timer for the machine gathering frames from a video source, a video file or a camera.
        String trainingBeginning = "Data has been collected. Now the Machine will train itself to recognize this face. This may take a few minutes."; // The message displayed to the user before training begins.
        String trainingEnded = "The Machine has finished training itself to recognize this face. Please be sure to read about 'Distance Threshold' in the input selection window."; // The message displayed to the user after training.
        const String errorPanic = "The Machine encountered an error.";
        const String errorMachine = "The Machine";

        /***
            Function: public static void PanicAndTerminateProgram()
            Parameter(s):
            Return Value: void
                Displays a generic error message and terminates the program.
        ***/
        public static void PanicAndTerminateProgram()
        {
            MessageBox.Show(errorPanic, errorMachine, MessageBoxButton.OK);
            Environment.Exit(1);
        }

        // Internal function needed to convert a Bitmap to a BitmapSource (WPF)
        [DllImport("gdi32")]
        private static extern int DeleteObject(IntPtr o);

        /***
            Function: private BitmapSource toBitmapSource(Bitmap bitmap)
            Parameter(s): Bitmap bitmap
                The input Bitmap object to convert into a BitmapSource object.
            Return Value:
                Returns a converted Bitmap that is now a BitmapSource.
        ***/
        private BitmapSource toBitmapSource(Bitmap bitmap)
        {
            IntPtr ptr = bitmap.GetHbitmap();
            BitmapSource ret = System.Windows.Interop.Imaging.CreateBitmapSourceFromHBitmap(ptr, IntPtr.Zero, Int32Rect.Empty,
                BitmapSizeOptions.FromEmptyOptions());

            // Make sure to delete the lose pointer.
            DeleteObject(ptr);
            return ret;
        }

        /***
            Function: private void TrainMachine(FaceIdentity faceIdentity, String name)
            Parameter(s): FaceIdentity faceIdentity
                Privilege of the face that is being trained to store in the ASSET_INDEX.dat file.
                          String name
                The name of the individual that is being trained; currently, it is not used, but it exists so that in the next version,
                the machine can be more customized.
            Return Value: void
        ***/
        private void TrainMachine(FaceIdentity faceIdentity, String name)
        {
            // Notify the user that training has begun.
            MessageBox.Show(trainingBeginning, this.Title, MessageBoxButton.OK);

            String[] fileList = Directory.GetFiles(FileUtilities.TrainingDirectoryName);
            List<Mat> matList = new List<Mat>();
            foreach (String file in fileList)
                matList.Add(new Mat(file, Emgu.CV.CvEnum.LoadImageType.Unchanged));
            List<Image<Gray, Byte>> list = new List<Image<Gray, Byte>>();

            // Detect each face in each image.
            foreach (Mat mat in matList)
            {
                Image<Gray, Byte> image = mat.ToImage<Gray, Byte>().Resize(1 / (double)scale_factor, Emgu.CV.CvEnum.Inter.Cubic);
                Rectangle[] faceList = faceClassifier.DetectMultiScale(image);
                foreach (Rectangle rect in faceList)
                    list.Add(image.Copy(rect).Convert<Gray, Byte>());
            }

            // Make sure that there is at least one face to train.
            if (list.Count() == 0)
                PanicAndTerminateProgram();

            // If a height exists in the CORE_IMAGE_DATA.dat file, resize to that, useful for future training.
            int height = facialRecognitionHeight == 0 ? list[0].Height * scale_factor : facialRecognitionHeight;
            int width = facialRecognitionWidth == 0 ? list[0].Width * scale_factor : facialRecognitionWidth;
            if (facialRecognitionHeight == 0 || facialRecognitionWidth == 0)
            {
                List<String> lines = new List<String>();
                lines.Add(height + "|" + width);
                File.WriteAllLines(FileUtilities.DirectoryName + "\\" + FileUtilities.CoreImageData, lines.ToArray());
            }
            List<Image<Gray, Byte>> listFinal = new List<Image<Gray, Byte>>();
            foreach (Image<Gray, Byte> image in list)
                listFinal.Add(image.Resize(width, height, Emgu.CV.CvEnum.Inter.Cubic));
            List<int> labelList = new List<int>();
            int integer = 0;
            String prefix = "";
            String ident = "";
            if (faceIdentity == FaceIdentity.FaceAdmin)
            {
                integer = 0;
                prefix = FileUtilities.AdminTrainedPrefix;
                ident = adminIdentifier;
            }
            else if (faceIdentity == FaceIdentity.FaceAsset)
            {
                integer = 2;
                prefix = FileUtilities.AssetTrainedPrefix;
                ident = auxAdminIdentifier;
            }
            else if (faceIdentity == FaceIdentity.FaceAuxAdmin)
            {
                integer = 1;
                prefix = FileUtilities.AuxAdminTrainedPrefix;
                ident = assetIdentifier;
            }
            else
                PanicAndTerminateProgram();
            for (int i = 0; i < list.Count(); i++)
                labelList.Add(integer);

            // Train the machine and write its trained state to a file.
            LBPHFaceRecognizer lbphFaceRecognizer = new LBPHFaceRecognizer();
            lbphFaceRecognizer.Train<Gray, Byte>(listFinal.ToArray(), labelList.ToArray());
            Directory.Delete(FileUtilities.TrainingDirectoryName, true);
            String temp = categories[integer];
            String fname = FileUtilities.DirectoryName + "\\" + prefix + temp.ToUpper().Replace(' ', '_') + FileUtilities.FileExtension;
            lbphFaceRecognizer.Save(fname);

            // Write everything to the ASSET_INDEX.dat file.
            FileUtilities.TrainingDirectoryDeletion();
            List<String> aboutTraining = new List<String>();
            aboutTraining.Add(name + "^" + ident + "^" + fname);
            File.AppendAllLines(FileUtilities.DirectoryName + "\\" + FileUtilities.AssetIndexData, aboutTraining.ToArray());

            // Notify the used that training has ended.
            MessageBox.Show(trainingEnded, this.Title, MessageBoxButton.OK);
        }

        /***
            Function: private Tuple<String[], FaceIdentity[]> GetCategoryForFaces(Image<Gray, Byte> image, Rectangle[] faceList)
            Parameter(s): Image<Gray, Byte> image
                A grayscaled image; this is greyscaled once, as the depth and size of the image to test the ANN has to match the training set's properties.
                          Rectangle[] faceList
                Isolate the list of faces inside of the grayscaled image. This is used for identification from the trained ANN.
            Return Value: Tuple<String[], FaceIdentity[]>
                Return the asset name and categorization from each face.
        ***/
        private Tuple<String[], FaceIdentity[]> GetCategoryForFaces(Image<Gray, Byte> image, Rectangle[] faceList)
        {
            List<String> ret = new List<String>();
            List<FaceIdentity> ret2 = new List<FaceIdentity>();
            foreach (Rectangle face in faceList)
            {
                Image<Gray, Byte> im = image.Copy(face).Resize(facialRecognitionWidth, facialRecognitionHeight, Emgu.CV.CvEnum.Inter.Cubic);
                int index = -1;
                for (int i = 0; i < lbphFaceRecognizerList.Length; i++)
                {
                    // Perform the actual prediction with the LBPHFaceRecognizer.
                    FaceRecognizer.PredictionResult predictionResult = lbphFaceRecognizerList[i].Predict(im);

                    /***
                        Check if the calculated distance from the camera from the LBPHFaceRecognizer is within the minimum
                        distance; too high of a distance disqualifies the image from this classification.
                    ***/
                    if (Math.Floor(predictionResult.Distance) < maxDistanceThreshold)
                    {
                        index = i;
                        break;
                    }
                }
                if (index == -1)
                {
                    ret.Add("");
                    ret2.Add(FaceIdentity.FaceSecondary);
                }
                else
                {
                    ret2.Add(focusKindDatabase[index]);
                    ret.Add(nameDatabase[index]);
                }
            }
            return new Tuple<string[], FaceIdentity[]>(ret.ToArray(), ret2.ToArray());
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
            if (counter == 0)
            {
                imagePanel.Height = mat.Height;
                imagePanel.Width = mat.Width;
                Height = mat.Height;
                Width = mat.Width;
            }
            Graphics graphics = Graphics.FromImage(bitmap);

            // Resizes the image and converts it to greyscale in order to increase performance
            Image<Gray, byte> image = mat.ToImage<Gray, byte>().Resize(1 / (double)scale_factor, Emgu.CV.CvEnum.Inter.Cubic);

            // Performs the actual face detection
            Rectangle[] faceList = faceClassifier.DetectMultiScale(image);

            //Performs the actual facial recognition
            if (!machineIsTraining)
            {
                Tuple<String[], FaceIdentity[]> tup = GetCategoryForFaces(image, faceList);
                nameList = tup.Item1;
                focusKindList = tup.Item2;
                if (focusKindList.Length == 0 || Array.IndexOf(focusKindList, FaceIdentity.FaceSecondary) > -1)
                    machineStartPage.ModifyAccess(AccessQualifier.AccessLocked);
                else if (Array.IndexOf(focusKindList, FaceIdentity.FaceAdmin) > -1)
                    machineStartPage.ModifyAccess(AccessQualifier.AccessFull);
                else
                    machineStartPage.ModifyAccess(AccessQualifier.AccessRestricted);
            }
            for (int i = 0; i < faceList.Length; i++)
            {
                Rectangle face = faceList[i];
                Rectangle scaled_face = ConstructScaledRectangle(face);
                Bitmap scaled_admin_focus = new Bitmap(admin_focus, scaled_face.Size);
                Bitmap scaled_secondary_focus = new Bitmap(secondary_focus, scaled_face.Size);
                String identifier = "";

                // Change the face categorization depending on whether the Machine is training itself or not.
                if (!machineIsTraining)
                {
                    if (focusKindList[i] == FaceIdentity.FaceAdmin)
                        identifier = categories[0];
                    else if (focusKindList[i] == FaceIdentity.FaceAuxAdmin)
                        identifier = categories[1];
                    else if (focusKindList[i] == FaceIdentity.FaceAsset)
                        identifier = categories[2];
                    else if (focusKindList[i] == FaceIdentity.FaceSecondary)
                        identifier = categories[3];
                    else
                        PanicAndTerminateProgram();
                    graphics.DrawImage(focusKindList[i] == FaceIdentity.FaceSecondary ? scaled_secondary_focus : scaled_admin_focus, scaled_face.Location);
                    graphics.DrawString(identifier, font, new SolidBrush(System.Drawing.Color.White), ConstructScaledPoint(face));
                    graphics.DrawString(userSpeech, speechFont, new SolidBrush(System.Drawing.Color.White), SpeechScaledPoint(ConstructScaledPoint(face)));
                }
                else
                {
                    if (faceIdentityTraining == FaceIdentity.FaceAdmin)
                        identifier = categories[-1];
                    else if (faceIdentityTraining == FaceIdentity.FaceAuxAdmin)
                        identifier = categories[-2];
                    else if (faceIdentityTraining == FaceIdentity.FaceAsset)
                        identifier = categories[-3];
                    else
                        PanicAndTerminateProgram();
                    graphics.DrawImage(faceIdentityTraining == FaceIdentity.FaceSecondary ? scaled_secondary_focus : scaled_admin_focus, scaled_face.Location);
                    graphics.DrawString(identifier, font, new SolidBrush(System.Drawing.Color.White), ConstructScaledPoint(face));
                    graphics.DrawString(userSpeech, speechFont, new SolidBrush(System.Drawing.Color.White), SpeechScaledPoint(ConstructScaledPoint(face)));
                }
            }
            if (machineIsTraining)
            {
                // Notify the user of how many frames have passed of the training period.
                String status = "Training: " + counter.ToString() + "/" + predefinedSampleImagesCount.ToString();
                System.Drawing.SizeF bottomCorner = graphics.MeasureString(status, font);
                graphics.DrawString(status, font, System.Drawing.Brushes.White, new PointF(bitmap.Width - bottomCorner.Width, bitmap.Height - bottomCorner.Height));
            }
            BitmapSource bitmapSource = toBitmapSource(bitmap);
            if (counter == 0)
            {
                // Resize the window to fit the size of the image.
                this.Height = bitmapSource.Height;
                this.Width = bitmapSource.Width;
            }
            imagePanel.Source = bitmapSource;
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
            for (int i = 0; i < faceList.Length; i++)
            {
                Rectangle face = faceList[i];
                Rectangle scaled_face = ConstructScaledRectangle(face);
                Bitmap scaled_admin_focus = new Bitmap(admin_focus, scaled_face.Size);
                Bitmap scaled_secondary_focus = new Bitmap(secondary_focus, scaled_face.Size);
                if (!machineIsTraining)
                {
                    String identifier = "";
                    if (focusKindList[i] == FaceIdentity.FaceAdmin)
                        identifier = categories[0];
                    else if (focusKindList[i] == FaceIdentity.FaceAuxAdmin)
                        identifier = categories[1];
                    else if (focusKindList[i] == FaceIdentity.FaceAsset)
                        identifier = categories[2];
                    else if (focusKindList[i] == FaceIdentity.FaceSecondary)
                        identifier = categories[3];
                    else
                        PanicAndTerminateProgram();
                    graphics.DrawImage(focusKindList[i] == FaceIdentity.FaceSecondary ? scaled_secondary_focus : scaled_admin_focus, scaled_face.Location);
                    graphics.DrawString(identifier, font, new SolidBrush(System.Drawing.Color.White), ConstructScaledPoint(face));
                    graphics.DrawString(userSpeech, speechFont, new SolidBrush(System.Drawing.Color.White), SpeechScaledPoint(ConstructScaledPoint(face)));
                }
                else
                {
                    String identifier = "";
                    if (faceIdentityTraining == FaceIdentity.FaceAdmin)
                        identifier = categories[-1];
                    else if (faceIdentityTraining == FaceIdentity.FaceAuxAdmin)
                        identifier = categories[-2];
                    else if (faceIdentityTraining == FaceIdentity.FaceAsset)
                        identifier = categories[-3];
                    else
                        PanicAndTerminateProgram();
                    graphics.DrawImage(faceIdentityTraining == FaceIdentity.FaceSecondary ? scaled_secondary_focus : scaled_admin_focus, scaled_face.Location);
                    graphics.DrawString(identifier, font, new SolidBrush(System.Drawing.Color.White), ConstructScaledPoint(face));
                    graphics.DrawString(userSpeech, speechFont, new SolidBrush(System.Drawing.Color.White), SpeechScaledPoint(ConstructScaledPoint(face)));
                }
            }
            if (machineIsTraining)
            {
                String status = "Training: " + counter.ToString() + "/" + predefinedSampleImagesCount.ToString();
                System.Drawing.SizeF bottomCorner = graphics.MeasureString(status, font);
                graphics.DrawString(status, font, System.Drawing.Brushes.White, new PointF(bitmap.Width - bottomCorner.Width, bitmap.Height - bottomCorner.Height));
            }
            imagePanel.Source = toBitmapSource(bitmap);
        }

        /***
            Function: private void ProcessAnyFrame(object sender, EventArgs args)
            Parameters: object sender, EventArgs args
            Return Value: void
                Processes a frame; note, this function applies to both video and camera frames (unlike the old Winforms version 
                of this application.
        ***/
        private void ProcessAnyFrame(object sender, EventArgs args)
        {
            Mat mat = capture.QueryFrame();
            if (machineIsTraining && (counter == predefinedSampleImagesCount || mat == null))
            {
                // Stop the timer from ticking.
                dispatcherTimer.Stop();
                counter = 0;

                // Train the Machine to recognize faces.
                TrainMachine(faceIdentityTraining, nameTraining);
                capture.Dispose();
                Environment.Exit(1);
            }
            if (mat == null || mat.IsEmpty)
                return;
            if (machineIsTraining && counter > 1)
            {
                // Write sample images to a training directory so the Machine can train itself to recognize difference faces.
                mat.Bitmap.Save(FileUtilities.TrainingDirectoryName + "\\" + FileUtilities.SampleImagePrefix + imageSamplesCounter.ToString() + ".bmp");
                imageSamplesCounter++;
            }
            if (counter % number_of_delayable_frames == 0)
                faceList = ProcessVideoFrame(mat);
            else
                ProcessDelayableFrame(faceList, mat);
            counter++;
            if (cameraResourceDisposal)
                mat.Dispose();
        }
    }
}
