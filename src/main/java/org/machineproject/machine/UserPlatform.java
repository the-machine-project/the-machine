package org.machineproject.machine;

import java.io.File;

/**
 * Created by xnart on 31.05.2016.
 */

public final class UserPlatform {

    public static final String OS_NAME = System.getProperty("os.name");
    public static final String OS_ARCH = System.getProperty("os.arch");

    public static File loadLibrary(){
        File openCvPath;
        if ("Linux".equalsIgnoreCase(UserPlatform.OS_NAME) && "amd64".equalsIgnoreCase
                (UserPlatform.OS_ARCH)){
            openCvPath = new File(System.getProperty("user.dir") + File
                    .separator + "ASSETS" + File.separator
                    + "lib" + File.separator + "x64" + File
                    .separator + "opencv_java310.so");
        }else if ("Windows".equalsIgnoreCase(UserPlatform.OS_NAME) && "amd64"
                .equalsIgnoreCase(UserPlatform.OS_ARCH)){
            openCvPath = new File(System.getProperty("user.dir") + File
                    .separator + "ASSETS" + File.separator
                    + "lib" + File.separator + "x64" + File
                    .separator + "opencv_java310.dll");

        }else{
            openCvPath = null;
        }

        return openCvPath;
    }

}
