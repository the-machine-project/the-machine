package org.themachineproject.machine.command;

import javafx.scene.paint.Color;
import org.themachineproject.machine.*;

import java.util.ArrayList;

/**
 * Created by nathr on 5/25/2016.
 */
public class AddUserCommand extends Command {

    public AddUserCommand(IdentityDataBaseFile idbf) {
        super(idbf, "adduser", Permissions.PermissionLevel.KIND_READ_WRITE);
    }

    @Override
    public ArrayList<ArrayList<TextFragment>> getCommandOutput(String[] input) {
        ArrayList<ArrayList<TextFragment>> output = new ArrayList<>();
        if(input.length != 1) {
            ArrayList<TextFragment> errorLine = new ArrayList<>();
            errorLine.add(new TextFragment("Error", Color.RED));
            errorLine.add(new TextFragment(": 'adduser' did not expect any command line arguments.", Color.WHITE));
            output.add(errorLine);
            return output;
        }
        ArrayList<TextFragment> successLine = new ArrayList<>();
        IdentityEntry identityEntry = new IdentityEntry(getIdentityDataBaseFile());
        getIdentityDataBaseFile().setRecognitionMode(RecognitionMode.MODE_TRAINING);
        getIdentityDataBaseFile().setUpdate(true);
        successLine.add(new TextFragment("Success!", Color.GREEN));
        output.add(successLine);
        return output;
    }

    @Override
    public ArrayList<ArrayList<TextFragment>> getHelpOutput() {
        ArrayList<ArrayList<TextFragment>> output = new ArrayList<>();
        ArrayList<TextFragment> arr = new ArrayList<>();
        arr.add(new TextFragment("'adduser' -- Add user to the system. Usage:", Color.WHITE));
        ArrayList<TextFragment> arr2 = new ArrayList<>();
        arr2.add(new TextFragment("     adduser", Color.WHITE));
        output.add(arr);
        output.add(arr2);
        return output;
    }
}
