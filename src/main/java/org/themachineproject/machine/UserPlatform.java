package org.themachineproject.machine;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.shape.Arc;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xnart on 31.05.2016.
 */

public final class UserPlatform {

    public static final String OS_NAME = System.getProperty("os.name");
    public static final String OS_ARCH = System.getProperty("os.arch");

    public enum ArchitectureKind {
        ARCH_32, ARCH_64
    }

    public enum OSKind {
        OS_WINDOWS, OS_MAC, OS_LINUX
    }

    public static final Map<String, ArchitectureKind> ARCH_MAP = new HashMap<>();
    static {
        ARCH_MAP.put("x86", ArchitectureKind.ARCH_32);
        ARCH_MAP.put("i386", ArchitectureKind.ARCH_32);
        ARCH_MAP.put("i486", ArchitectureKind.ARCH_32);
        ARCH_MAP.put("i586", ArchitectureKind.ARCH_32);
        ARCH_MAP.put("i686", ArchitectureKind.ARCH_32);
        ARCH_MAP.put("x86_64", ArchitectureKind.ARCH_64);
        ARCH_MAP.put("amd64", ArchitectureKind.ARCH_64);
    }

    private static OSKind deduceOSKind(String s) {
        if(s.toLowerCase().contains("Windows".toLowerCase()))
            return OSKind.OS_WINDOWS;
        else if(s.toLowerCase().contains("Linux".toLowerCase()))
            return OSKind.OS_LINUX;
        else if(s.toLowerCase().contains("Mac".toLowerCase()))
            return OSKind.OS_MAC;
        return null;
    }

    public static void loadLibrary(){
        File openCvPath = null;
        ArchitectureKind architectureKind = ARCH_MAP.get(OS_ARCH);
        if(architectureKind == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unrecognized architecture. Exiting...", ButtonType.OK);
            alert.showAndWait();
            System.exit(1);
            return;
        }
        OSKind osKind = deduceOSKind(OS_NAME);
        if(osKind != OSKind.OS_WINDOWS) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unrecognized or unsupported operating system. Exiting...", ButtonType.OK);
            alert.showAndWait();
            System.exit(1);
            return;
        }
        File openCvInCx64 = new File("C:" + File.separator + "opencv" + File.separator
                + "build" + File.separator + "java" +File.separator + "x64" + File.separator + "opencv_java310.dll");
        File openCvInCx86 = new File("C:" + File.separator + "opencv" + File.separator
                + "build" + File.separator + "java" +File.separator + "x86" + File.separator + "opencv_java310.dll");
        if(osKind == OSKind.OS_WINDOWS) {
            if(architectureKind == ArchitectureKind.ARCH_32)
                openCvPath = new File(System.getProperty("user.dir") + File.separator + "ASSETS" + File.separator
                        + "opencv" + File.separator + "build" + File.separator + "java" + File.separator + "x86" + File.separator + "opencv_java310.dll");
            else if(architectureKind == ArchitectureKind.ARCH_64)
                openCvPath = new File(System.getProperty("user.dir") + File.separator + "ASSETS" + File.separator
                        + "opencv" + File.separator + "build" + File.separator + "java" + File.separator + "x64" + File.separator + "opencv_java310.dll");
        }

        if(openCvPath != null && openCvPath.exists() && openCvPath.canRead()) {
            System.load(openCvPath.getAbsolutePath());
            return;
        }
        else if(osKind == OSKind.OS_WINDOWS) {
            if(architectureKind == ArchitectureKind.ARCH_32) {
                System.load(openCvInCx86.getAbsolutePath());
                return;
            }
            else if(architectureKind == ArchitectureKind.ARCH_64) {
                System.load(openCvInCx64.getAbsolutePath());
                return;
            }
        }
        Alert alert = new Alert(Alert.AlertType.ERROR, "OpenCV not found. Exiting...", ButtonType.OK);
        alert.showAndWait();
        System.exit(1);
    }
}
