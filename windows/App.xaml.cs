using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data;
using System.Linq;
using System.Threading.Tasks;
using System.Windows;
using System.IO;
using machine;

namespace machine
{
    public partial class App : Application
    {
        protected override void OnStartup(StartupEventArgs args)
        {
            base.OnStartup(args);
            // Test whether or not to train the Machine by looking for the existence of various files.
            bool train = !Directory.Exists(FileUtilities.DirectoryName) || !File.Exists(FileUtilities.DirectoryName + "\\" + FileUtilities.AssetIndexData) ||
                !File.Exists(FileUtilities.DirectoryName + "\\" + FileUtilities.CoreImageData);
            Console.WriteLine(train);
            if (train)
            {
                InputSelection i = new InputSelection(train, false, "ADMIN", InputSelection.TrainTitle, machine.MainWindow.TrainTitle, FaceIdentity.FaceAdmin);
                i.Show();
            }
            else
            {
                InputSelection i = new InputSelection(train, true, "", InputSelection.ExecutionTitle, machine.MainWindow.ExecutionTitle, FaceIdentity.FaceNone);
                i.Show();
            }
        }
    }
}
