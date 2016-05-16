using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data;
using System.Linq;
using System.Threading.Tasks;
using System.Windows;
using System.IO;
using machine;
using System.Windows.Media;
using System.Windows.Controls;

namespace machine
{
    public enum UIKind
    {
        UIMachine, UISamaritan, UINone
    };

    public partial class App : Application
    {
        protected override void OnStartup(StartupEventArgs args)
        {
            base.OnStartup(args);
            // Test whether or not to train the Machine by looking for the existence of various files.
            bool train = !Directory.Exists(FileUtilities.DirectoryName) || !File.Exists(FileUtilities.DirectoryName + "\\" + FileUtilities.AssetIndexData) ||
                !File.Exists(FileUtilities.DirectoryName + "\\" + FileUtilities.CoreImageData);
            UIKind uikind = UIKind.UISamaritan;
            if (uikind == UIKind.UIMachine)
            {
                MachineStartPage m = new MachineStartPage(train, uikind);
                m.Show();
            }
            else if (uikind == UIKind.UISamaritan)
            {
                SamaritanStartPage ssp = new SamaritanStartPage(train, uikind);
                ssp.Show();
            }
        }
    }
}

namespace XAMLConstants
{
    public class Constants
    {
        public static readonly Brush MachineBackgroundColor = Brushes.Black;
        public static readonly Brush SamaritanForegroundColor = Brushes.Black;
        public static readonly Brush MachineForegroundColor = Brushes.White;
        public static readonly Uri samaritanRestoreImage = new Uri("pack://application:,,,/restore_samaritan.png");
        public static readonly Uri samaritanMaximizeImage = new Uri("pack://application:,,,/maximize_samaritan.png");
        public static readonly Uri machineRestoreImage = new Uri("pack://application:,,,/restore_machine.png");
        public static readonly Uri machineMaximizeImage = new Uri("pack://application:,,,/maximize_machine.png");
        public static readonly Uri machineFont = new Uri("pack://application:,,,/font.ttf");
    }
}
