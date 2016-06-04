package org.themachineproject.machine.command;

import javafx.scene.paint.Color;
import org.themachineproject.machine.DispatchTable;
import org.themachineproject.machine.IdentityDataBaseFile;
import org.themachineproject.machine.Permissions;
import org.themachineproject.machine.TextFragment;

import java.util.ArrayList;

/**
 * Created by nathr on 5/28/2016.
 */
public class HelpCommand extends Command {

    public HelpCommand(IdentityDataBaseFile idbf) {
        super(idbf, "help", Permissions.PermissionLevel.KIND_REGULAR);
    }

    @Override
    public ArrayList<ArrayList<TextFragment>> getCommandOutput(String[] input) {
        ArrayList<ArrayList<TextFragment>> output = new ArrayList<>();
        if(input.length != 1) {
            ArrayList<TextFragment> errorLine = new ArrayList<>();
            errorLine.add(new TextFragment("Warning", Color.PURPLE));
            errorLine.add(new TextFragment(": 'killall' did not expect any command line arguments, ignoring them.", Color.WHITE));
            output.add(errorLine);
        }
        for(Command c : DispatchTable.COMMAND_DISPATCH_TABLE.values()) {
            for (ArrayList<TextFragment> arr : c.getHelpOutput())
                output.add(arr);
            output.add(new ArrayList<>());
        }
        return output;
    }

    @Override
    public ArrayList<ArrayList<TextFragment>> getHelpOutput() {
        ArrayList<ArrayList<TextFragment>> output = new ArrayList<>();
        ArrayList<TextFragment> arr = new ArrayList<>();
        arr.add(new TextFragment("'help' -- Outputs help message about each command registered to the terminal. Usage:", Color.WHITE));
        ArrayList<TextFragment> arr2 = new ArrayList<>();
        arr2.add(new TextFragment("     help", Color.WHITE));
        output.add(arr);
        output.add(arr2);
        return output;
    }
}
