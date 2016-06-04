package org.themachineproject.machine;

import org.opencv.core.Rect;

/**
 * Created by nathr on 3/20/2016.
 */
public class Face {

    private Rect rect;
    private Identity identity;

    public Face(Rect r, Identity i) {
        identity = i;
        rect = r;
    }

    public Rect getRect() {
        return rect;
    }

    public Identity getIdentity() {
        return identity;
    }
}
