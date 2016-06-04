package org.themachineproject.machine.command;

import org.themachineproject.machine.IdentityDataBaseFile;
import org.themachineproject.machine.Permissions;
import org.themachineproject.machine.TextFragment;

import java.util.ArrayList;

/**
 * Created by nathr on 5/25/2016.
 */
public abstract class Command {

    public abstract ArrayList<ArrayList<TextFragment>> getCommandOutput(String[] input);

    private Permissions.PermissionLevel minimumPermissionLevel;
    private String commandName;
    private IdentityDataBaseFile identityDataBaseFile;

    public Command(IdentityDataBaseFile idbf, String s, Permissions.PermissionLevel mpl) {
        commandName = s;
        minimumPermissionLevel = mpl;
        identityDataBaseFile = idbf;
    }

    public String getCommandName() {
        return commandName;
    }

    public Permissions.PermissionLevel getMinimumPermissionLevel() {
        return minimumPermissionLevel;
    }

    public IdentityDataBaseFile getIdentityDataBaseFile() {
        return identityDataBaseFile;
    }

    public abstract ArrayList<ArrayList<TextFragment>> getHelpOutput();
}