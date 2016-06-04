package org.themachineproject.machine;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Created by nathr on 3/19/2016.
 */
public class Identity {

    public enum PermissionsKind {
        KIND_ADMIN, KIND_AUX_ADMIN, KIND_ASSET, KIND_THREAT, KIND_ANALOG_INTERFACE
    }

    private PermissionsKind permissionsKind;
    private String userName, permissionsString, about, socialSecurityNumber, facialDataFile;
    private RecognitionMode recognitionMode;
    private String generatedId;

    public Identity(RecognitionMode rm, PermissionsKind pk, String un, String ssn, String f, String gi) {
        permissionsKind = pk;
        recognitionMode = rm;
        facialDataFile = f;
        userName = un;
        switch(pk) {
            case KIND_ADMIN:
                permissionsString = "ADMIN";
                break;
            case KIND_AUX_ADMIN:
                permissionsString = "AUX_ADMIN";
                break;
            case KIND_ASSET:
                permissionsString = "ASSET";
                break;
            case KIND_THREAT:
                permissionsString = "THREAT";
                break;
            case KIND_ANALOG_INTERFACE:
                permissionsString = "ANALOG_INTERFACE";
                break;
            default:
                Alert alert = new Alert(Alert.AlertType.ERROR, "Malformed identity entry", ButtonType.OK);
                alert.showAndWait();
                System.exit(1);
                break;
        }

        socialSecurityNumber = ssn;
        String[] splitSSN = socialSecurityNumber.split("-");
        if(splitSSN.length != 3) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Malformed identity entry", ButtonType.OK);
            alert.showAndWait();
            System.exit(1);
        }
        if(splitSSN[0].length() != 3 || splitSSN[1].length() != 2 || splitSSN[2].length() != 4) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Malformed identity entry", ButtonType.OK);
            alert.showAndWait();
            System.exit(1);
        }

        about = "";
        generatedId = gi;
    }

    public Identity(RecognitionMode rm, PermissionsKind pk, String un, String ssn, String f, String a, String gi) {
        this(rm, pk, un, ssn, f, gi);
        about = a;
    }

    public PermissionsKind getPermissionsKind() {
        return permissionsKind;
    }

    public RecognitionMode getRecognitionMode() {
        return recognitionMode;
    }

    public String getUserName() {
        return userName;
    }

    public String getPermissionsString() {
        return permissionsString;
    }

    public String getAbout() {
        return about;
    }

    public String getGeneratedId() {
        return generatedId;
    }

    public static PermissionsKind permissionStringToPermissionsKind(String str) {
        if(str.equals("ADMIN")) return PermissionsKind.KIND_ADMIN;
        else if(str.equals("AUX_ADMIN")) return PermissionsKind.KIND_AUX_ADMIN;
        else if(str.equals("ASSET")) return PermissionsKind.KIND_ASSET;
        else if(str.equals("THREAT")) return PermissionsKind.KIND_THREAT;
        else if(str.equals("ANALOG_INTERFACE")) return PermissionsKind.KIND_ANALOG_INTERFACE;
        return null;
    }

    public String getFacialDataFile() {
        return facialDataFile;
    }

    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }
}
