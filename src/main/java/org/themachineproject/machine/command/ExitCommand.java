package org.themachineproject.machine.command;

import javafx.scene.paint.Color;
import org.themachineproject.machine.IdentityDataBaseFile;
import org.themachineproject.machine.Permissions;
import org.themachineproject.machine.TextFragment;

import java.util.ArrayList;

/**
 * Created by nathr on 5/28/2016.
 */
public class ExitCommand extends Command {

    public ExitCommand(IdentityDataBaseFile idbf) {
        super(idbf, "exit", Permissions.PermissionLevel.KIND_REGULAR);
    }

    @Override
    public ArrayList<ArrayList<TextFragment>> getCommandOutput(String[] input) {
        ArrayList<ArrayList<TextFragment>> output = new ArrayList<>();
        if(input.length != 1) {
            ArrayList<TextFragment> errorLine = new ArrayList<>();
            errorLine.add(new TextFragment("Warning", Color.PURPLE));
            errorLine.add(new TextFragment(": 'exit' did not expect any command line arguments, ignoring them.", Color.WHITE));
            output.add(errorLine);
        }
        ArrayList<TextFragment> successLine = new ArrayList<>();
        successLine.add(new TextFragment("Success!", Color.GREEN));
        output.add(successLine);
        getIdentityDataBaseFile().setCloseTerminal(true);
        return output;
    }

    @Override
    public ArrayList<ArrayList<TextFragment>> getHelpOutput() {
        ArrayList<ArrayList<TextFragment>> output = new ArrayList<>();
        ArrayList<TextFragment> arr = new ArrayList<>();
        arr.add(new TextFragment("'exit' -- Exits the terminal. Usage:", Color.WHITE));
        ArrayList<TextFragment> arr2 = new ArrayList<>();
        arr2.add(new TextFragment("     exit", Color.WHITE));
        output.add(arr);
        output.add(arr2);
        return output;
    }
}