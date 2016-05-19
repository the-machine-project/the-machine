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
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using System.Threading;
using System.Windows.Threading;
using System.Drawing.Text;
using System.Drawing;
using System.Runtime.InteropServices;

namespace machine
{
    public enum AccessQualifier
    {
        AccessFull, AccessRestricted, AccessLocked
    }

    public partial class MachineStartPage : Window
    {
        bool maximized = true;
        Button closeButton = null;
        Button maximizeRestoreButton = null;
        System.Windows.Controls.Image maximizeRestoreImage = null;
        Button minimizeButton = null;
        MainWindow mainWindow = null;
        InputSelection inputSelection = null;
        AccessQualifier accessQualifier = AccessQualifier.AccessLocked;
        EventHandler mainEventHandler = null;
        bool firstChanged = false;

        DispatcherTimer dispatchTimer = null;
        bool creditsPart = true;
        bool firstPart = false;
        bool secondPart = false;
        bool thirdPart = false;
        bool fourthPart = false;
        bool trainMachine = false;
        bool seekedAdmin = false;
        bool initialWindowShown = false;
        UIKind uikind = UIKind.UINone;

        int line = 0;
        int pos = 0;

        public MachineStartPage(bool train, UIKind uk)
        {
            InitializeComponent();
            trainMachine = train;
            dispatchTimer = new DispatcherTimer();
            dispatchTimer.Interval = new TimeSpan(0, 0, 0, 0, 10);
            dispatchTimer.Tick += timerTick;
            uikind = uk;
        }

        private Tuple<int, int> printNonCyclicText(String[] list, int line, int pos)
        {
            if (line >= list.Length)
                return new Tuple<int, int>(line, pos);
            else
            {
                String current = list[line];
                if (pos >= current.Length)
                {
                    line++;
                    if (line >= list.Length) ;
                    else
                        machineText.Text += "\n";
                    return new Tuple<int, int>(line, 0);
                }
                else
                {
                    machineText.Text += current[pos];
                    pos++;
                    return new Tuple<int, int>(line, pos);
                }
            }
        }

        private Tuple<int, int> printCyclicText(String[] list, int line, int pos)
        {
            if (line < list.Length - 1)
            {
                String current = list[line];
                if (pos >= current.Length)
                {
                    line++;
                    if (line >= list.Length - 1)
                        dispatchTimer.Interval = new TimeSpan(0, 0, 0, 0, 500);
                    else
                        machineText.Text += "\n";
                    pos = 0;
                    seekedAdmin = true;
                    return new Tuple<int, int>(line, pos);
                }
                else
                {
                    machineText.Text += current[pos];
                    pos++;
                    return new Tuple<int, int>(line, pos);
                }
            }
            else
            {
                String current = list[line];
                if (pos >= current.Length)
                {
                    pos = 0;
                    if (trainMachine && thirdPart)
                    {
                        thirdPart = false;
                        fourthPart = true;
                        machineText.Text = "";
                        line = 0;
                        dispatchTimer.Interval = new TimeSpan(0, 0, 0, 0, 1);
                    }
                    else
                        machineText.Text = machineText.Text.Remove(machineText.Text.Length - 3);
                    return new Tuple<int, int>(line, pos);
                }
                else
                {
                    machineText.Text += current[pos];
                    pos++;
                    return new Tuple<int, int>(line, pos);
                }
            }
        }

        private void timerTick(object sender, EventArgs args)
        {
            if (creditsPart)
            {
                Tuple<int, int> tup = printNonCyclicText(credits, line, pos);
                line = tup.Item1;
                pos = tup.Item2;
                if (line >= credits.Length)
                {
                    creditsPart = false;
                    firstPart = true;
                    line = 0;
                    pos = 0;
                    machineText.Text = "";
                    dispatchTimer.Interval = new TimeSpan(0, 0, 0, 0, 1);
                    Thread.Sleep(500);
                }
            }
            else if (firstPart)
            {
                Tuple<int, int> tup = printNonCyclicText(systemBootPart1, line, pos);
                line = tup.Item1;
                pos = tup.Item2;
                if (line >= systemBootPart1.Length)
                {
                    firstPart = false;
                    secondPart = true;
                    line = 0;
                    pos = 0;
                    machineText.Text = "";
                }
            }
            else if (secondPart)
            {
                Tuple<int, int> tup = printNonCyclicText(systemBootPart2, line, pos);
                line = tup.Item1;
                pos = tup.Item2;
                if (line >= systemBootPart2.Length)
                {
                    secondPart = false;
                    thirdPart = true;
                    line = 0;
                    pos = 0;
                    machineText.Text = "";
                }
            }
            else if (thirdPart)
            {
                Tuple<int, int> tup = printCyclicText(systemBootPart3, line, pos);
                line = tup.Item1;
                pos = tup.Item2;
            }
            else if (fourthPart)
            {
                Tuple<int, int> tup = printCyclicText(systemBootPart4, line, pos);
                line = tup.Item1;
                pos = tup.Item2;
            }
            else
                dispatchTimer.Stop();
            if (seekedAdmin && !initialWindowShown)
            {
                if (trainMachine)
                    inputSelection = new InputSelection(trainMachine, false, "ADMIN", InputSelection.TrainTitle, machine.MainWindow.TrainTitle, FaceIdentity.FaceAdmin, this, uikind);
                else
                    inputSelection = new InputSelection(trainMachine, true, "", InputSelection.ExecutionTitle, machine.MainWindow.ExecutionTitle, FaceIdentity.FaceNone, this, uikind);
                inputSelection.Show();
                mainWindow = inputSelection.GetMainWindow();
                seekedAdmin = false;
                initialWindowShown = true;
            }
        }

        public override void OnApplyTemplate()
        {
            base.OnApplyTemplate();
            closeButton = (Button)GetTemplateChild("customCloseButton");
            closeButton.Click += (object sender, RoutedEventArgs args) =>
            {
                Environment.Exit(1);
            };
            maximizeRestoreButton = (Button)GetTemplateChild("customMaximizeRestoreButton");
            maximizeRestoreButton.Click += (object sender, RoutedEventArgs args) =>
            {
                if (maximized)
                    WindowState = WindowState.Normal;
                else
                    WindowState = WindowState.Maximized;
            };
            maximizeRestoreImage = (System.Windows.Controls.Image)GetTemplateChild("customMaximizeRestoreImage");
            minimizeButton = (Button)GetTemplateChild("customMinimizeButton");
            minimizeButton.Click += (object sender, RoutedEventArgs args) =>
            {
                WindowState = WindowState.Minimized;
            };
        }

        private void Window_SizeChanged(object sender, SizeChangedEventArgs e)
        {
            maximized = WindowState == WindowState.Maximized;
            if (maximized)
                maximizeRestoreImage.Source = new BitmapImage(XAMLConstants.Constants.machineRestoreImage);
            else
                maximizeRestoreImage.Source = new BitmapImage(XAMLConstants.Constants.machineMaximizeImage);
        }

        private void window_Loaded(object sender, EventArgs args)
        {
            dispatchTimer.Start();
        }

        private void window_PreviewKeyDown(object sender, KeyEventArgs args)
        {
            if (creditsPart)
                return;
            if (!thirdPart && !trainMachine)
            {
                firstPart = false;
                secondPart = false;
                thirdPart = true;
            }
            else if (trainMachine)
            {
                firstPart = false;
                secondPart = false;
                thirdPart = false;
                fourthPart = true;
            }
            dispatchTimer.Interval = new TimeSpan(0, 0, 0, 0, 1);
            pos = 0;
            line = 0;
            machineText.Text = "";
            if (accessQualifier == AccessQualifier.AccessFull)
            {
                new CommandLinePrompt(this).ShowDialog();
            }
        }

        private void timerExit(object sender, EventArgs args)
        {
            Tuple<int, int> tup = printNonCyclicText(noInputSource, line, pos);
            line = tup.Item1;
            pos = tup.Item2;
            if (line >= noInputSource.Length)
                MainWindow.PanicAndTerminateProgram();
        }

        public void NoInputSourceSelected()
        {
            dispatchTimer.Stop();
            dispatchTimer.Tick -= timerTick;
            dispatchTimer.Tick -= mainEventHandler;
            machineText.Text = "";
            dispatchTimer.Interval = new TimeSpan(0, 0, 0, 0, 1);
            line = 0;
            pos = 0;
            dispatchTimer.Tick += timerExit;
            dispatchTimer.Start();
        }

        private void timerTerminate(object sender, EventArgs args)
        {
            Tuple<int, int> tup = printNonCyclicText(programExiting, line, pos);
            line = tup.Item1;
            pos = tup.Item2;
            if (line >= noInputSource.Length)
                Environment.Exit(1);
        }

        public void ProgramTerminating()
        {
            dispatchTimer.Stop();
            dispatchTimer.Tick -= timerTick;
            dispatchTimer.Tick -= mainEventHandler;
            machineText.Text = "";
            dispatchTimer.Interval = new TimeSpan(0, 0, 0, 0, 1);
            line = 0;
            pos = 0;
            dispatchTimer.Tick += timerExit;
            dispatchTimer.Start();
        }

        public void ModifyAccess(AccessQualifier aq)
        {
            if (accessQualifier != aq || !firstChanged)
            {
                machineText.Text = "";
                String[] toPrint = aq == AccessQualifier.AccessLocked ? adminNotInView :
                    (aq == AccessQualifier.AccessRestricted ? restrictedInView : onlyAdminInView);
                accessQualifier = aq;
                dispatchTimer.Stop();
                dispatchTimer.Tick -= timerTick;
                dispatchTimer.Tick -= timerExit;
                dispatchTimer.Tick -= mainEventHandler;
                machineText.Text = "";
                dispatchTimer.Interval = new TimeSpan(0, 0, 0, 0, 1);
                line = 0;
                pos = 0;
                mainEventHandler = (object sender, EventArgs args) =>
                {
                    Tuple<int, int> tup = printCyclicText(toPrint, line, pos);
                    line = tup.Item1;

                    pos = tup.Item2;
                };
                dispatchTimer.Tick += mainEventHandler;
                dispatchTimer.Start();
                firstChanged = true;
            }
        }

        public AccessQualifier GetAccessQualifier()
        {
            return accessQualifier;
        }

        public void HandleCommand(String cmd)
        {
            if (cmd == "/website")
                System.Diagnostics.Process.Start("http://www.themachineproject.com/");
            else
            {
                MessageBox.Show("Invalid Command!", "The Machine", MessageBoxButton.OK);
            }
        }

        public UIKind GetUIKind()
        {
            return uikind;
        }

        private void Window_Closed(object sender, EventArgs e)
        {
            Environment.Exit(1);
        }
    }
}
