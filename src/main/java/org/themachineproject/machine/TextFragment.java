package org.themachineproject.machine;

import javafx.scene.paint.Color;

/**
 * Created by nathr on 5/25/2016.
 */
public class TextFragment {

    String text;
    Color color;

    public TextFragment(String t, Color c) {
        text = t;
        color = c;
    }

    public String getText() {
        return text;
    }

    public Color getColor() {
        return color;
    }
}
