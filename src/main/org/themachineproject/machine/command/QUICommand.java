package org.themachineproject.machine.command;

import org.themachineproject.machine.IdentityDataBaseFile;
import org.themachineproject.machine.Permissions;
import org.themachineproject.machine.Terminal;
import org.themachineproject.machine.TextFragment;
import org.themachineproject.machine.command.qui.QUIShell;

import java.util.ArrayList;

/**
 * Created by Rocco on 05/06/2016.
 */
public class QUICommand extends Command {


    public QUICommand(IdentityDataBaseFile idbf) {
        super(idbf, "qui", Permissions.PermissionLevel.KIND_REGULAR);
    }

    @Override
    public ArrayList<ArrayList<TextFragment>> getCommandOutput(String[] input) {
        QUIShell shell = new QUIShell(getIdentityDataBaseFile());
        shell.showShell();
        return null;
    }

    @Override
    public ArrayList<ArrayList<TextFragment>> getHelpOutput() {
        return null;
    }
}
