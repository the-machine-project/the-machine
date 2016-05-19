/* By devloop0 (Nikhil Athreya) */
/***
    This is the window where the user selects the input source for the Machine.
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
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;
using DirectShowLib;

namespace machine
{
    public partial class InputSelection : Window
    {
        MachineInputSourceKind machineInputSourceKind = MachineInputSourceKind.SourceNone; // The default value, which is an error.
        int cameraNumber = 0; // The camera number to pass to EmguCV Capture in order for further processing.
        int cameraFps = 30; // The camera frame rate, defaults to 30 frames per second.
        bool resDisposal = false; // Whether or not to dispose matrices as they are processed.
        bool advancedSettingsVisible = false; // Whether the advanced settings are visible (initially, they are not).
        String thresholdExplanationText = "This changes the sensitivity of the artificial neural network to detect distances. Higher values mean that it will be less sensitive to high distances, " +
            "lower values mean that it will be more sensitive to higher distances. Change this value depending on the distance you sit from the webcam. " +
            "If the Machine does not accurately recognize you, please by sure to toggle this value (usually lower) in order for accurate recognition. " + 
            "The downside is that it will be less sensitive to other people's faces as well. " + "Be sure to toggle the value to experiment and select a value that suits you. " +
            "This is ignored when training the Machine."; // The threshold explanation text to make sure the user understands what the slider means.
        int distance = 40; // The default threshold distance, set to 40.
        bool trainMachine = false; // Whether the machine is training itself or not.
        FaceIdentity faceIdentity = FaceIdentity.FaceNone; // The classification of the person if the Machine will train itself.
        public const String TrainTitle = "Input Selection for Training the Machine"; // The title for the window if the Machine is training itself.
        public const String ExecutionTitle = "Input Selection for the Machine"; // The title for the window of the Machine during normal Machine execution.
        String trainingName = null; // The name of the person the Machine will train itself to recognize (if it is training).
        String mainWindowTitle = null; // The title of the main window.

        /***
            Function: public InputSelection(bool train, bool disposal, String name, String title, String mwt, FaceIdentity fit)
            Parameter(s): bool train
                Whether or not to train the Machine (this is passed to the MainWindow.xaml.cs file).
                          bool disposal
                Whether or not to enable resouce disposal.
                          String name
                The name of the person that the Machine will train itself to recognize (if training is enabled; should be "" if trainMachine is false).
                          String title
                The title of the input selection window.
                          String mainWindowTitle
                The title of the main window (this is passed to the MainWindow.xaml.cs file).
                          FaceIdentity fit
                The classification of the face to be examined during training (should be FaceIdentity.FaceNone if the Machine is not training; if the Machine is training).
            Return Value: N/A (Constructor)
        ***/
        public InputSelection(bool train, bool disposal, String name, String title, String mwt, FaceIdentity fit)
        {
            InitializeComponent();
            this.Title = title;
            trainMachine = train;
            faceIdentity = fit;
            trainingName = name;
            mainWindowTitle = mwt;
            if(!disposal)
            {
                resDisposal = false;

                // Completely disable the option (this is only applicable for training).
                resourceDisposal.IsEnabled = false;
            }
        }

        /***
            Function: public MachineInputSourceKind GetMachineInputSourceKind()
            Parameter(s):
            Return Value: MachineInputSourceKind
                This method is essentially a getter-function for the machineInputSourceKind variable to access after an input
                has been selected.
        ***/
        public MachineInputSourceKind GetMachineInputSourceKind()
        {
            return machineInputSourceKind;
        }

        /***
            Function: public int GetCameraNumber()
            Parameter(s):
            Return Value: int
                Essentially acts as a getter-function in order to access the cameraNumber variable.
                This is used to access the cameraNumber variable after an input has been selected.
        ***/
        public int GetCameraNumber()
        {
            return cameraNumber;
        }

        /***
            Function: public int GetCameraFrameRate()
            Parameter(s):
            Return Value: int
                Essentially acts as a getter-function in order to access the cameraFrameRate variable.
                This is used to access the cameraFrameRate variable after an input has been selected.
        ***/
        public int GetCameraFrameRate()
        {
            return cameraFps;
        }

        /***
            Function: public bool GetResourceDisposal()
            Parameter(s):
            Return Value: bool
                Essentially acts as a getter-function in order to access the resDisposal variable.
                This is used to access the resourceDisposal variable after an input has been selected and only applies for camera input.
        ***/
        public bool GetResourceDisposal()
        {
            return resDisposal;
        }

        private void submit_OnClick(object sender, RoutedEventArgs e)
        {
            bool? fb = fileButton.IsChecked;
            bool? cb = cameraButton.IsChecked;
            bool fileChosen = false;
            bool cameraChosen = false;
            if (fb.HasValue)
                fileChosen = (bool) fb;
            if (cb.HasValue)
                cameraChosen = (bool)cb;
            if (fileChosen)
                machineInputSourceKind = MachineInputSourceKind.SourceFile;
            else if (cameraChosen)
            {
                machineInputSourceKind = MachineInputSourceKind.SourceWebcam;
                String text2 = cameraFrameRate.Text;
                String text = cameraNumberManual.Text;

                // The default value is zero, but could crash if there is no detectable camera.
                if (cameraList.IsEnabled)
                    cameraNumber = cameraList.SelectedIndex;
                if (advancedSettingsVisible && text.Length > 0)
                    cameraNumber = Int32.Parse(text);
                if (text2 == "") ;
                else
                    cameraFps = Int32.Parse(text2);
            }
            bool? rd = resourceDisposal.IsChecked;
            if (rd.HasValue)
                resDisposal = (bool)rd;
            distance = (int) distanceThresholdSlider.Value;

            // Start the main window once all of the necessary parameters have been received.
            MainWindow mainWindow = new MainWindow(trainMachine, faceIdentity, trainingName, GetCameraNumber(), GetCameraFrameRate(),
                GetResourceDisposal(), GetDistanceThresholdValue(), GetMachineInputSourceKind(), GetMainWindowTitle());
            mainWindow.Show();

            // Close the input selection window.
            Close();
        }

        /***
            Function: private void HideAdvancedSettings()
            Parameter(s):
            Return Value: void
                Hides the advanced settings of the application (the text box with the Emgu CV camera number), and its label.
        ***/
        private void HideAdvancedSettings()
        {
            cameraNumberManual.Visibility = Visibility.Hidden;
            cameraNumberManual.IsEnabled = false;
            cameraNumberManualLabel.Visibility = Visibility.Hidden;
            advancedSettingsVisible = false;
        }

        /***
            Function: private void ShowAdvancedSettings()
            Parameter(s):
            Return Value: void
                Shows the advanced settings of the application (the text box with the Emgu CV camera number), and its label.
        ***/
        private void ShowAdvancedSettings()
        {
            cameraNumberManual.Visibility = Visibility.Visible;
            cameraNumberManual.IsEnabled = true;
            cameraNumberManualLabel.Visibility = Visibility.Visible;
            advancedSettingsVisible = true;
        }

        /***
            Function: public int GetDistanceThresholdValue()
            Parameter(s):
            Return Value: int
                Essentially acts as a getter-function for the 'distance' variable; this should only be called once the entire window
                has run and the user has selected values.
        ***/
        public int GetDistanceThresholdValue()
        {
            return distance;
        }

        private void distanceThresholdSlider_ValueChanged(object sender, RoutedPropertyChangedEventArgs<double> e)
        {
            // Update the label as the slider value changes.
            if (distanceThresholdSlider == null || currentValue == null) ;
            else
                currentValue.Content = ((int) (distanceThresholdSlider.Value)).ToString();
        }

        private void window_Loaded(object sender, RoutedEventArgs e)
        {
            // Load the list of possible video capture devices.
            DsDevice[] deviceList = DsDevice.GetDevicesOfCat(FilterCategory.VideoInputDevice);
            List<String> deviceNameList = new List<String>();
            foreach (DsDevice dsDevice in deviceList)
                deviceNameList.Add(dsDevice.Name);

            cameraList.ItemsSource = null;

            // Hide advanced settings initially
            HideAdvancedSettings();

            if (deviceNameList.Count() > 0)
            {
                cameraList.ItemsSource = deviceNameList;
                cameraList.SelectedIndex = 0;
            }
            else // If there are no video devices, disable the ComboBox completely.
                cameraList.IsEnabled = false;
        }

        private void explanation_Click(object sender, RoutedEventArgs args)
        {
            // Show the explanation regarding the distance threshold values.
            MessageBox.Show(thresholdExplanationText, this.Title, MessageBoxButton.OK);
        }

        private void advancedSettings_Click(object sender, RoutedEventArgs args)
        {
            // Toggle the advancedSettings variable and the visibility of the Emgu CV capture variable textbox and label.
            if (advancedSettingsVisible)
                HideAdvancedSettings();
            else
                ShowAdvancedSettings();
        }

        /***
            Function: public String GetMainWindowTitle()
            Parameter(s):
            Return Value: String
                Essentially acts as a getter-function for the 'mainWindowTitle' variable; this should only be called once the
                actual window has been shown and the user's preferences have been gathered.
        ***/
        public String GetMainWindowTitle()
        {
            return mainWindowTitle;
        }
    }
}
