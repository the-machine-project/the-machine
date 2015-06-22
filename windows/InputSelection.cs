/* By devloop0 (Nikhil Athreya) */
/***
    This file is a Windows Form for allowing the user to choose the input source they want to get videos from.
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

namespace machine
{
    public partial class InputSelection : Form
    {
        MachineInputSourceKind machineInputSourceKind = MachineInputSourceKind.SourceNone; // The default value, which is an error.
        int cameraNumber = 0; // The camera number to pass to EmguCV Capture in order for further processing.
        int cameraFrameRate = 15; // The camera frame rate, defaults to 15 frames per second.
        bool resourceDisposal = false; // Whether or not to dispose matrices as they are processed.

        public InputSelection()
        {
            InitializeComponent();
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

        private void selectSource_Click(object sender, EventArgs e)
        {
            bool fileSelectionChecked = fileSelection.Checked;
            bool webcamSelectionChecked = webcamSelection.Checked;
            if (fileSelectionChecked)
                machineInputSourceKind = MachineInputSourceKind.SourceFile;
            else if (webcamSelectionChecked)
            {
                machineInputSourceKind = MachineInputSourceKind.SourceWebcam;
                String text = cameraNumberInput.Text;
                String text2 = cameraFrameRateInput.Text;

                // The default value is zero, so make sure an empty string is not processed.
                if (text == "")
                    cameraNumber = 0;
                else
                    cameraNumber = Int32.Parse(text);
                if (text2 == "")
                    cameraFrameRate = 15;
                else
                    cameraFrameRate = Int32.Parse(text2);
            }
            resourceDisposal = resourceDisposalInput.Checked;
            Close();
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
            return cameraFrameRate;
        }

        /***
            Function: public bool GetResourceDisposal()
            Parameter(s):
            Return Value: bool
                Essentially acts as a getter-function in order to access the resourceDisposal variable.
                This is used to access the resourceDisposal variable after an input has been selected and only applies for camera input.
        ***/
        public bool GetResourceDisposal()
        {
            return resourceDisposal;
        }
    }
}
