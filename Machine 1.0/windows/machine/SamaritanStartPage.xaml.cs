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
using System.Windows.Threading;
using System.Windows.Media.Animation;
using System.Threading;

namespace machine
{
    /// <summary>
    /// Interaction logic for SamaritanStartPage.xaml
    /// </summary>
    public partial class SamaritanStartPage : Window
    {

        String[] list = new String[]
        {
            //"Willing", "to", "trade", "lives", "for", "location", "60", "59", "58", "57", "56", "55", "54", "53", "52", "51", "50", "49", "48", "47", "46", "45", "44", "43", "42", "41", "40", "39", "38", "37", "36", "35", "34", "33", "32", "31", "30", "29", "28", "27", "26", "25", "24", "23", "22", "21", "20", "19", "18", "17", "16", "15", "14", "13", "12", "11", "10", "9", "8", "7", "6", "5", "4", "3", "2", "1", "The", "machine", "has", "been", "located", "deploying", "all", "assets"
            "Can", "you", "hear", "me", "?"
        };
        DispatcherTimer dispatchTimer = null;
        int count = 0;
        Line line2 = new Line();
        Line line = new Line();
        bool maximized = true;
        Button closeButton = null;
        Button maximizeRestoreButton = null;
        System.Windows.Controls.Image maximizeRestoreImage = null;
        Button minimizeButton = null;
        UIKind uikind = UIKind.UINone;
        bool trainMachine = false;
        InputSelection inputSelection = null;
        bool inputSelectionShown = false;

        public SamaritanStartPage(bool train, UIKind uk)
        {
            InitializeComponent();
            uikind = uk;
            trainMachine = train;
            dispatchTimer = new DispatcherTimer(new TimeSpan(0, 0, 1), DispatcherPriority.ApplicationIdle, timerTick, Application.Current.Dispatcher);
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

        private void timerTick(object sender, EventArgs args)
        {
            if (count < list.Length)
                textField.Text = list[count].ToUpper();
            else if (!inputSelectionShown)
            {
                if (trainMachine)
                    inputSelection = new InputSelection(trainMachine, false, "ADMIN", InputSelection.TrainTitle, machine.MainWindow.TrainTitle, FaceIdentity.FaceAdmin, new MachineStartPage(trainMachine, uikind), uikind);
                else
                    inputSelection = new InputSelection(trainMachine, true, "", InputSelection.ExecutionTitle, machine.MainWindow.ExecutionTitle, FaceIdentity.FaceNone, new MachineStartPage(trainMachine, uikind), uikind);
                inputSelection.Show();
                inputSelectionShown = true;
            }
            Dispatcher.Invoke(delegate
            {
                if (count == 0)
                {
                    canvas.Children.Add(line);
                    canvas.Children.Add(line2);
                    line.Stroke = Brushes.Black;
                    line.StrokeThickness = 2;
                    line.X1 = 0;
                    line.Y1 = 0;
                    line2.Stroke = Brushes.Black;
                    line2.StrokeThickness = 2;
                    line2.X1 = 0;
                    line2.Y1 = 0;
                }
                Storyboard storyBoard = new Storyboard();
                DoubleAnimation da = new DoubleAnimation(line.X2, textField.ActualWidth / 2 + 10, new Duration(new TimeSpan(0, 0, 0, 0, 250)));
                Storyboard.SetTargetProperty(da, new PropertyPath("(Line.X2)"));
                storyBoard.Children.Add(da);
                storyBoard.Completed += (s, e) =>
                {
                    if (count + 1 < list.Length && list[count].Length != list[count + 1].Length)
                        canvas.InvalidateVisual();
                };
                line.BeginStoryboard(storyBoard);
                Storyboard storyBoard2 = new Storyboard();
                DoubleAnimation da2 = new DoubleAnimation(line2.X2, -textField.ActualWidth / 2 - 10, new Duration(new TimeSpan(0, 0, 0, 0, 250)));
                Storyboard.SetTargetProperty(da2, new PropertyPath("(Line.X2)"));
                storyBoard2.Children.Add(da2);
                storyBoard2.Completed += (s, e) =>
                {
                    if (count + 1 < list.Length && list[count].Length != list[count + 1].Length)
                        canvas.InvalidateVisual();
                };
                line2.BeginStoryboard(storyBoard2);
            }, DispatcherPriority.ApplicationIdle);
            count++;
        }

        private void Window_SizeChanged(object sender, SizeChangedEventArgs e)
        {
            maximized = WindowState == WindowState.Maximized;
            if (maximized)
                maximizeRestoreImage.Source = new BitmapImage(XAMLConstants.Constants.samaritanRestoreImage);
            else
                maximizeRestoreImage.Source = new BitmapImage(XAMLConstants.Constants.samaritanMaximizeImage);
        }

        private void Window_Loaded(object sender, RoutedEventArgs e)
        {
            dispatchTimer.Start();
        }

        public UIKind GetUIKind()
        {
            return uikind;
        }

        public bool GetTrainMachine() {
            return trainMachine;
        }

        private void Window_Closed(object sender, EventArgs e)
        {
            Environment.Exit(1);
        }
    }
}