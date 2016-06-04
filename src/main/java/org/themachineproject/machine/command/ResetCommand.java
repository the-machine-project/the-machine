package org.themachineproject.machine.command;

import javafx.scene.paint.Color;
import org.themachineproject.machine.Assets;
import org.themachineproject.machine.IdentityDataBaseFile;
import org.themachineproject.machine.Permissions;
import org.themachineproject.machine.TextFragment;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by nathr on 6/3/2016.
 */
public class ResetCommand extends Command {

    public ResetCommand(IdentityDataBaseFile idbf) {
        super(idbf, "reset", Permissions.PermissionLevel.KIND_READ_WRITE_MODIFY);
    }

    @Override
    public ArrayList<ArrayList<TextFragment>> getCommandOutput(String[] input) {
        ArrayList<ArrayList<TextFragment>> output = new ArrayList<>();
        if(input.length != 1) {
            ArrayList<TextFragment> errorLine = new ArrayList<>();
            errorLine.add(new TextFragment("Error", Color.RED));
            errorLine.add(new TextFragment(": 'reset' did not expect any command line arguments.", Color.WHITE));
            output.add(errorLine);
            return output;
        }
        ArrayList<TextFragment> successLine = new ArrayList<>();

        File facialData = new File(Assets.FACIAL_DATA_DIRECTORY);
        if(facialData.exists() && facialData.canRead() && facialData.listFiles() != null) for(File f : facialData.listFiles()) f.delete();
        facialData.delete();
        File training = new File(Assets.TRAINING_DIRECTORY);
        if(training.exists() && training.canRead() && training.listFiles() != null) for(File f : training.listFiles()) f.delete();
        training.delete();
        File identityDb = new File(Assets.DATABASE_FILE);
        identityDb.delete();

        successLine.add(new TextFragment("Success!", Color.GREEN));
        output.add(successLine);
        getIdentityDataBaseFile().setCloseTerminal(true);
        getIdentityDataBaseFile().setCloseApplication(true);
        return output;
    }

    @Override
    public ArrayList<ArrayList<TextFragment>> getHelpOutput() {
        ArrayList<ArrayList<TextFragment>> output = new ArrayList<>();
        ArrayList<TextFragment> arr = new ArrayList<>();
        arr.add(new TextFragment("'reset' -- Resets the machine and clears all data. Usage:", Color.WHITE));
        ArrayList<TextFragment> arr2 = new ArrayList<>();
        arr2.add(new TextFragment("     reset", Color.WHITE));
        output.add(arr);
        output.add(arr2);
        return output;
    }
}
