package org.themachineproject.machine;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * Created by nathr on 3/19/2016.
 */
public class IdentityDataBaseFile {

    public static final String INNER_IDENTITY_DELIMITER = "/\\", OUTER_IDENTITY_DELIMITER = "\n/\\\n";
    private File identityDataBaseFile, facialDataDirectory;
    private RecognitionMode recognitionMode;
    private boolean update;
    private boolean closeApplication, closeTerminal, clearTerminal;

    public IdentityDataBaseFile() {
        update = false;
        identityDataBaseFile = new File(Assets.DATABASE_FILE);
        facialDataDirectory = new File(Assets.FACIAL_DATA_DIRECTORY);
        if(!identityDataBaseFile.exists() || !facialDataDirectory.exists()) {
            recognitionMode = RecognitionMode.MODE_INITIAL_TRAINING;
            try {
                identityDataBaseFile.createNewFile();
                facialDataDirectory.mkdir();
            } catch(IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Cannot create a database file or facial data directory, please make sure that the Machine has the"
                        + " correct file permissions.", ButtonType.OK);
                alert.showAndWait();
                System.exit(1);
            }
        }
        else
            recognitionMode = RecognitionMode.MODE_NORMAL;
        closeApplication = false;
        closeTerminal = false;
        closeTerminal = false;
    }

    public boolean getClearTerminal() {
        return clearTerminal;
    }

    public void setClearTerminal(boolean ct) {
        clearTerminal = ct;
    }

    public boolean getCloseTerminal() {
        return closeTerminal;
    }

    public void setCloseTerminal(boolean ct) {
        closeTerminal = ct;
    }

    public boolean getCloseApplication() {
        return closeApplication;
    }

    public void setCloseApplication(boolean ca) {
        closeApplication = ca;
    }

    public boolean getUpdate() {
        return update;
    }

    public void setUpdate(boolean u) {
        update = u;
    }

    public void setRecognitionMode(RecognitionMode rm) {
        recognitionMode = rm;
    }

    public RecognitionMode getRecognitionMode() {
        return recognitionMode;
    }

    public ArrayList<Identity> parseIdentityDataBaseFile() {
        ArrayList<Identity> ret = new ArrayList<>();
        // File format: < 'ADMIN' | 'AUX_ADMIN' | 'ASSET' | 'THREAT' | 'ANALOG_INTERFACE' > '/\\' <user-name> '/\\' <about> '/\\' <file-name> '/\\' <ssn> '\n/\\\n'
        // this is a single entry
        try {
            StringBuilder stringBuilder = new StringBuilder();
            Files.lines(identityDataBaseFile.toPath()).forEach((String s) -> stringBuilder.append(s + "\n"));
            String rawText = stringBuilder.toString();
            String[] identityList = rawText.split("\\n/\\\\\\n");
            for(String identity : identityList) {
                if(identity.length() == 0); // just skip
                else {
                    String[] decomposed = identity.split("/\\\\");
                    if(decomposed.length != 6) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Malformed identity database file; if you tried to edit it, please revert any changes. If you didn't, "
                                + "then something has gone wrong; just delete the file and restart the application.", ButtonType.OK);
                        alert.showAndWait();
                        System.exit(1);
                    }
                    Identity.PermissionsKind permKind = Identity.permissionStringToPermissionsKind(decomposed[0]);
                    if(permKind == null) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Malformed identity database file or non-existent facial data file; if you tried to edit stuff, please revert any changes. If you didn't, "
                                + "then something has gone wrong; just delete the file and restart the application.", ButtonType.OK);
                        alert.showAndWait();
                        System.exit(1);
                    }
                    ret.add(new Identity(recognitionMode, permKind, decomposed[1], decomposed[4], decomposed[3], decomposed[2], decomposed[5]));
                }
            }
        } catch(IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Cannot create a database file, please make sure that the Machine has the"
                    + " correct file permissions.", ButtonType.OK);
            alert.showAndWait();
            System.exit(1);
        }
        return ret;
    }

    public void clearFile() {
        try {
            PrintWriter printWriter = new PrintWriter(identityDataBaseFile);
            printWriter.write("");
            printWriter.close();
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Cannot access the database file, please make sure that the Machine has the"
                    + " correct file permissions.", ButtonType.OK);
            alert.showAndWait();
            System.exit(1);
        }
    }

    public void writeAllIdentitiesToFile(ArrayList<Identity> il) {
        for(Identity i : il)
            writeIdentityToFile(i);
    }

    public void writeIdentityToFile(Identity identity) {
        try {
            FileWriter fileWriter = new FileWriter(identityDataBaseFile, true);
            fileWriter.write(identity.getPermissionsString() + INNER_IDENTITY_DELIMITER + identity.getUserName() + INNER_IDENTITY_DELIMITER + identity.getAbout() +
                INNER_IDENTITY_DELIMITER + identity.getFacialDataFile() + INNER_IDENTITY_DELIMITER + identity.getSocialSecurityNumber() + INNER_IDENTITY_DELIMITER
                    + identity.getGeneratedId() + OUTER_IDENTITY_DELIMITER);
            fileWriter.close();
        } catch(IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Cannot access the database file, please make sure that the Machine has the"
                    + " correct file permissions.", ButtonType.OK);
            alert.showAndWait();
            System.exit(1);
        }
    }
}
