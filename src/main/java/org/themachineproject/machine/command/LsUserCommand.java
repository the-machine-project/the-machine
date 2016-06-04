package org.themachineproject.machine.command;

import javafx.scene.paint.Color;
import org.themachineproject.machine.Identity;
import org.themachineproject.machine.IdentityDataBaseFile;
import org.themachineproject.machine.Permissions;
import org.themachineproject.machine.TextFragment;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by nathr on 5/28/2016.
 */
public class LsUserCommand extends Command {

    public LsUserCommand(IdentityDataBaseFile idbf) {
        super(idbf, "lsuser", Permissions.PermissionLevel.KIND_READ);
    }

    @Override
    public ArrayList<ArrayList<TextFragment>> getCommandOutput(String[] input) {
        ArrayList<ArrayList<TextFragment>> output = new ArrayList<>();
        if(input.length != 1) {
            ArrayList<TextFragment> errorLine1 = new ArrayList<>();
            errorLine1.add(new TextFragment("Error", Color.RED));
            errorLine1.add(new TextFragment(": 'lsuser' does not expect any arguments.", Color.WHITE));
            output.add(errorLine1);
            return output;
        }
        ArrayList<Identity> identityList = getIdentityDataBaseFile().parseIdentityDataBaseFile();
        Collections.sort(identityList, (a, b) -> {
            return Permissions.getNumericPermissions(Permissions.getPermissionsFromIdentity(a)) - Permissions.getNumericPermissions(Permissions.getPermissionsFromIdentity(b));
        });
        for(Identity id : identityList) {
            ArrayList<TextFragment> line = new ArrayList<>();
            if(id.getPermissionsKind() == Identity.PermissionsKind.KIND_ADMIN || id.getPermissionsKind() == Identity.PermissionsKind.KIND_ASSET
                    || id.getPermissionsKind() == Identity.PermissionsKind.KIND_AUX_ADMIN || id.getPermissionsKind() == Identity.PermissionsKind.KIND_ANALOG_INTERFACE)
                line.add(new TextFragment(id.getPermissionsString(), Color.YELLOW));
            else if(id.getPermissionsKind() == Identity.PermissionsKind.KIND_THREAT)
                line.add(new TextFragment(id.getPermissionsString(), Color.RED));
            line.add(new TextFragment("\t\t\t\t\t" + id.getUserName().substring(0, id.getUserName().length() < 10 ? id.getUserName().length() : 10)
                    + (id.getUserName().length() > 10 ? "..." : ""), Color.WHITE));
            line.add(new TextFragment("\t\t\t\t" + id.getSocialSecurityNumber(), Color.WHITE));
            line.add(new TextFragment("\t\t" + id.getAbout().substring(0, id.getAbout().length() < 10 ? id.getAbout().length() : 10)
                    + (id.getAbout().length() > 10 ? "..." : ""), Color.WHITE));
            output.add(line);
        }
        return output;
    }

    @Override
    public ArrayList<ArrayList<TextFragment>> getHelpOutput() {
        ArrayList<ArrayList<TextFragment>> output = new ArrayList<>();
        ArrayList<TextFragment> arr = new ArrayList<>();
        arr.add(new TextFragment("'lsuser' -- Lists all of the users in the system. Usage:", Color.WHITE));
        ArrayList<TextFragment> arr2 = new ArrayList<>();
        arr2.add(new TextFragment("     lsuser", Color.WHITE));
        output.add(arr);
        output.add(arr2);
        return output;
    }
}
