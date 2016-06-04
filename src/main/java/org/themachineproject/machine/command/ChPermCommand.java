package org.themachineproject.machine.command;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import org.themachineproject.machine.Identity;
import org.themachineproject.machine.IdentityDataBaseFile;
import org.themachineproject.machine.Permissions;
import org.themachineproject.machine.TextFragment;

import java.util.*;

/**
 * Created by nathr on 5/27/2016.
 */
public class ChPermCommand extends Command {

    public static final Map<String, Identity.PermissionsKind> POSSIBLE_PERMISSIONS_MAP = new HashMap<>();
    static {
        POSSIBLE_PERMISSIONS_MAP.put("ADMIN", Identity.PermissionsKind.KIND_ADMIN);
        POSSIBLE_PERMISSIONS_MAP.put("AUX_ADMIN", Identity.PermissionsKind.KIND_AUX_ADMIN);
        POSSIBLE_PERMISSIONS_MAP.put("ASSET", Identity.PermissionsKind.KIND_ASSET);
        POSSIBLE_PERMISSIONS_MAP.put("ANALOG_INTERFACE", Identity.PermissionsKind.KIND_ANALOG_INTERFACE);
        POSSIBLE_PERMISSIONS_MAP.put("THREAT", Identity.PermissionsKind.KIND_THREAT);
    }

    public ChPermCommand(IdentityDataBaseFile idbf) {
        super(idbf, "chperm", Permissions.PermissionLevel.KIND_READ_WRITE_MODIFY);
    }

    @Override
    public ArrayList<ArrayList<TextFragment>> getCommandOutput(String[] input) {
        ArrayList<ArrayList<TextFragment>> output = new ArrayList<>();
        if(input.length != 4) {
            ArrayList<TextFragment> errorLine1 = new ArrayList<>();
            errorLine1.add(new TextFragment("Error", Color.RED));
            errorLine1.add(new TextFragment(": 'chperm' expects exactly 4 command line arguments.", Color.WHITE));
            output.add(errorLine1);
            ArrayList<TextFragment> errorLine2 = new ArrayList<>();
            errorLine2.add(new TextFragment("   chperm <user name> <ssn> [AUX_ADMIN | ASSET | ANALOG_INTERFACE | THREAT]", Color.WHITE));
            output.add(errorLine2);
            return output;
        }
        String uname = input[1], ssn = input[2], perm = input[3];
        if(POSSIBLE_PERMISSIONS_MAP.keySet().contains(perm) && perm.equals("ADMIN")) {
            ArrayList<TextFragment> errorLine = new ArrayList<>();
            errorLine.add(new TextFragment("Error", Color.RED));
            errorLine.add(new TextFragment(": cannot assign any user to an ADMIN; there can only be one ADMIN.", Color.WHITE));
            output.add(errorLine);
            return output;
        }
        else if(!POSSIBLE_PERMISSIONS_MAP.keySet().contains(perm)) {
            ArrayList<TextFragment> errorLine = new ArrayList<>();
            errorLine.add(new TextFragment("Error", Color.RED));
            errorLine.add(new TextFragment(": given permission not recognized.", Color.WHITE));
            output.add(errorLine);
            return output;
        }
        ArrayList<Identity> identityList = getIdentityDataBaseFile().parseIdentityDataBaseFile();
        boolean hit = false;
        int index = -1;
        for(int j = 0; j < identityList.size(); j++) {
            Identity i = identityList.get(j);
            if(i.getUserName().equals(uname)) {
                if(i.getPermissionsKind() == Identity.PermissionsKind.KIND_ADMIN) {
                    ArrayList<TextFragment> errorLine = new ArrayList<>();
                    errorLine.add(new TextFragment("Error", Color.RED));
                    errorLine.add(new TextFragment(": user '" + uname + "' is ADMIN, cannot reassign permission.", Color.WHITE));
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
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you would like to reassign the permission the given identity?", ButtonType.OK);
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get().equals(ButtonType.OK)) {
            Identity toUpdate = identityList.remove(index);
            getIdentityDataBaseFile().clearFile();
            Identity updated = new Identity(toUpdate.getRecognitionMode(), POSSIBLE_PERMISSIONS_MAP.get(perm), toUpdate.getUserName(), toUpdate.getSocialSecurityNumber(), toUpdate.getFacialDataFile(),
                    toUpdate.getAbout(), toUpdate.getGeneratedId());
            identityList.add(updated);
            getIdentityDataBaseFile().writeAllIdentitiesToFile(identityList);
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
        arr.add(new TextFragment("'chperm' -- Changes the permission level of a user. Usage:", Color.WHITE));
        ArrayList<TextFragment> arr2 = new ArrayList<>();
        arr2.add(new TextFragment("     chperm <user name> <ssn> [AUX_ADMIN | ASSET | ANALOG_INTERFACE | THREAT]", Color.WHITE));
        output.add(arr);
        output.add(arr2);
        return output;
    }
}
