package org.themachineproject.machine;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.File;

/**
 * Created by xnart on 31.05.2016.
 */

public final class UserPlatform {

    public static final String OS_NAME = System.getProperty("os.name");
    public static final String OS_ARCH = System.getProperty("os.arch");

    public static void loadLibrary(){
        File openCvPath;
        File openCvInC = new File("C:" + File.separator + "opencv" + File.separator
                + "build" + File.separator + "java" +File.separator + "x64" + File.separator + "opencv_java310.dll");
        if (UserPlatform.OS_NAME.toLowerCase().contains("Linux".toLowerCase()) && UserPlatform.OS_ARCH.toLowerCase().contains("amd64".toLowerCase()))
            openCvPath = new File(System.getProperty("user.dir") + File.separator + "ASSETS" + File.separator
                    + "lib" + File.separator + "x64" + File.separator + "opencv_java310.so");
        else if(UserPlatform.OS_NAME.toLowerCase().contains("Windows".toLowerCase()) && UserPlatform.OS_ARCH.toLowerCase().contains("amd64".toLowerCase()))
            openCvPath = new File(System.getProperty("user.dir") + File.separator + "ASSETS" + File.separator
                    + "opencv" + File.separator + "build" + File.separator + "java" + File.separator + "x64" + File.separator + "opencv_java310.dll");
        else if(UserPlatform.OS_NAME.toLowerCase().contains("Windows".toLowerCase()) && UserPlatform.OS_ARCH.toLowerCase().contains("x86".toLowerCase()))
            openCvPath = new File(System.getProperty("user.dir") + File.separator + "ASSETS" + File.separator
                    + "opencv" + File.separator + "build" + File.separator + "java" + File.separator + "x86" + File.separator + "opencv_java310.dll");
        else
            openCvPath = null;

        if(openCvPath != null && openCvPath.exists() && openCvPath.canRead())
            System.load(openCvPath.getAbsolutePath());
        else if("Windows".toLowerCase().contains(UserPlatform.OS_NAME.toLowerCase()) && openCvPath == null && openCvInC.exists() && openCvInC.canRead())
            System.load(openCvInC.getAbsolutePath());
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "OpenCV not found. Exiting...", ButtonType.OK);
            alert.showAndWait();
            System.exit(1);
            return;
        }
    }
}
