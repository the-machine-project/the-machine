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

namespace machine
{
    public partial class CommandLinePrompt : Window
    {
        String command = "";
        MachineStartPage machineStartPage;
        UIKind uikind = UIKind.UINone;

        public CommandLinePrompt(MachineStartPage msp)
        {
            InitializeComponent();
            textBox.Focus();
            machineStartPage = msp;
            uikind = msp.GetUIKind();
        }

        private void textBox_KeyDown(object sender, KeyEventArgs e)
        {
            if(e.Key == Key.Enter)
            {
                command = textBox.Text;
                machineStartPage.HandleCommand(command);
                Close();
            }
        }

        public String GetCommand()
        {
            return command;
        }

        private void Window_Loaded(object sender, RoutedEventArgs e)
        {
            FontFamily fontFamily = null;
            if (machineStartPage.GetUIKind() == UIKind.UIMachine)
                fontFamily = new FontFamily(new Uri("pack://application:,,,/machine_font.ttf"), "./#CallOne-Regular");
            else if(machineStartPage.GetUIKind() == UIKind.UISamaritan)
                fontFamily = new FontFamily(new Uri("pack://application:,,,/samaritan_font.otf"), "./#MagdaCleanMono");
            textBox.FontFamily = fontFamily;
            label.FontFamily = fontFamily;
        }
    }
}
