package de.tudresden.cib.vis.scene.text;

import de.tudresden.cib.vis.scene.UIContext;
import de.tudresden.cib.vis.scene.VisBuilder;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class TextBuilder implements VisBuilder<TextFactory.TextLabel, String> {
    private StringBuffer buffer;

    public void init() {
        buffer = new StringBuffer();
    }

    public void addPart(TextFactory.TextLabel graphicalObject) {
        buffer.append(graphicalObject.text);
        buffer.append("\n");
    }

    public void finish() {
       // nothing to do
    }

    public String getScene() {
        return buffer.toString();
    }

    public UIContext getUiContext() {
       return null; // TODO: scene managers with different capabilities
    }
}
