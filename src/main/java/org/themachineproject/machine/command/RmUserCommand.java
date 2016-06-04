package org.themachineproject.machine.command;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import org.themachineproject.machine.Identity;
import org.themachineproject.machine.IdentityDataBaseFile;
import org.themachineproject.machine.Permissions;
import org.themachineproject.machine.TextFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by nathr on 5/27/2016.
 */
public class RmUserCommand extends Command {

    public RmUserCommand(IdentityDataBaseFile idbf) {
        super(idbf, "rmuser", Permissions.PermissionLevel.KIND_READ_WRITE);
    }

    @Override
    public ArrayList<ArrayList<TextFragment>> getCommandOutput(String[] input) {
        ArrayList<ArrayList<TextFragment>> output = new ArrayList<>();
        if(input.length != 3) {
            ArrayList<TextFragment> errorLine1 = new ArrayList<>();
            errorLine1.add(new TextFragment("Error", Color.RED));
            errorLine1.add(new TextFragment(": 'rmuser' expects exactly 3 command line arguments.", Color.WHITE));
            output.add(errorLine1);
            ArrayList<TextFragment> errorLine2 = new ArrayList<>();
            errorLine2.add(new TextFragment("   rmuser <user name> <ssn>", Color.WHITE));
            output.add(errorLine2);
            return output;
        }
        String uname = input[1], ssn = input[2];
        ArrayList<Identity> identityList = getIdentityDataBaseFile().parseIdentityDataBaseFile();
        boolean hit = false;
        int index = -1;
        for(int j = 0; j < identityList.size(); j++) {
            Identity i = identityList.get(j);
            if(i.getUserName().equals(uname)) {
                if(i.getPermissionsKind() == Identity.PermissionsKind.KIND_ADMIN) {
                    ArrayList<TextFragment> errorLine = new ArrayList<>();
                    errorLine.add(new TextFragment("Error", Color.RED));
                    errorLine.add(new TextFragment(": user '" + uname + "' is ADMIN, cannot delete.", Color.WHITE));
                    output.add(errorLine);
                    return output;
                }
                if(ssn.equals(i.getSocialSecurityNumber())) {
                    hit = true;
                    index = j;
                    break;
                }
                else {
                    ArrayList<TextFragment> errorLine = new ArrayList<>();
                    errorLine.add(new TextFragment("Error", Color.RED));
                    errorLine.add(new TextFragment(": user name '" + uname + "' not associated with the given SSN.", Color.WHITE));
                    output.add(errorLine);
                    return output;
                }
            }
        }
        if(!hit) {
            ArrayList<TextFragment> errorLine = new ArrayList<>();
            errorLine.add(new TextFragment("Error", Color.RED));
            errorLine.add(new TextFragment(": user name '" + uname + "' not found.", Color.WHITE));
            output.add(errorLine);
            return output;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you would like to remove the given identity?", ButtonType.OK);
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get().equals(ButtonType.OK)) {
            Identity toDelete = identityList.remove(index);
            getIdentityDataBaseFile().clearFile();
            getIdentityDataBaseFile().writeAllIdentitiesToFile(identityList);
            File fileToDelete = new File(toDelete.getFacialDataFile());
            fileToDelete.delete();
            getIdentityDataBaseFile().setUpdate(true);
        }
        ArrayList<TextFragment> successLine = new ArrayList<>();
        successLine.add(new TextFragment("Success!", Color.GREEN));
        output.add(successLine);
        return output;
    }

    @Override
    public ArrayList<ArrayList<TextFragment>> getHelpOutput() {
        ArrayList<ArrayList<TextFragment>> output = new ArrayList<>();
        ArrayList<TextFragment> arr = new ArrayList<>();
        arr.add(new TextFragment("'rmuser' -- Removes a user from the system. Usage:", Color.WHITE));
        ArrayList<TextFragment> arr2 = new ArrayList<>();
        arr2.add(new TextFragment("     rmuser <user name> <ssn>", Color.WHITE));
        output.add(arr);
        output.add(arr2);
        return output;
    }
}
