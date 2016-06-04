package org.themachineproject.machine;

/**
 * Created by nathr on 5/23/2016.
 */
public class Permissions {
    public enum PermissionLevel {
        KIND_READ_WRITE_MODIFY,
        KIND_READ_WRITE,
        KIND_READ,
        KIND_REGULAR,
        KIND_LOCKED
    }

    public static PermissionLevel getPermissionsFromIdentity(Identity id) {
        if(id.getPermissionsKind() == Identity.PermissionsKind.KIND_ADMIN)
            return PermissionLevel.KIND_READ_WRITE_MODIFY;
        else if(id.getPermissionsKind() == Identity.PermissionsKind.KIND_AUX_ADMIN || id.getPermissionsKind() == Identity.PermissionsKind.KIND_ANALOG_INTERFACE)
            return PermissionLevel.KIND_READ_WRITE;
        else if(id.getPermissionsKind() == Identity.PermissionsKind.KIND_ASSET)
            return PermissionLevel.KIND_READ;
        else if(id.getPermissionsKind() == Identity.PermissionsKind.KIND_THREAT)
            return PermissionLevel.KIND_LOCKED;
        return PermissionLevel.KIND_LOCKED;
    }

    public static PermissionLevel getPermissionsFromFace(Face f) {
        if(f.getIdentity() == null)
            return PermissionLevel.KIND_REGULAR;
        else if(f.getIdentity().getPermissionsKind() == Identity.PermissionsKind.KIND_ADMIN)
            return PermissionLevel.KIND_READ_WRITE_MODIFY;
        else if(f.getIdentity().getPermissionsKind() == Identity.PermissionsKind.KIND_ANALOG_INTERFACE || f.getIdentity().getPermissionsKind() == Identity.PermissionsKind.KIND_AUX_ADMIN)
            return PermissionLevel.KIND_READ_WRITE;
        else if(f.getIdentity().getPermissionsKind() == Identity.PermissionsKind.KIND_ASSET)
            return PermissionLevel.KIND_READ;
        else if(f.getIdentity().getPermissionsKind() == Identity.PermissionsKind.KIND_THREAT)
            return PermissionLevel.KIND_LOCKED;
        return PermissionLevel.KIND_REGULAR;
    }

    public static int getNumericPermissions(PermissionLevel pl) {
        switch(pl) {
            case KIND_LOCKED:
                return 0;
            case KIND_REGULAR:
                return 1;
            case KIND_READ:
                return 2;
            case KIND_READ_WRITE:
                return 3;
            case KIND_READ_WRITE_MODIFY:
                return 4;
        }
        return -1;
    }

    public static PermissionLevel getPermissionsFromNumber(int i) {
        switch(i) {
            case 0:
                return PermissionLevel.KIND_LOCKED;
            case 1:
                return PermissionLevel.KIND_REGULAR;
            case 2:
                return PermissionLevel.KIND_READ;
            case 3:
                return PermissionLevel.KIND_READ_WRITE;
            case 4:
                return PermissionLevel.KIND_READ_WRITE_MODIFY;
        }
        return PermissionLevel.KIND_LOCKED;
    }
}
