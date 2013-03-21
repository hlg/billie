package de.tudresden.cib.vis.scene.text;

import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.scene.*;

import java.util.Collection;

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

    @Override
    public void addTriggers(Event event, Collection<VisFactory2D.GraphObject> triggers, SceneManager<?, String> sceneManager) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public static <E> Mapper<E, TextFactory.TextLabel, String> createMapper(DataAccessor<E> data){
        return new Mapper<E,TextFactory.TextLabel, String>(data, new TextFactory(), new TextBuilder());
    }
}
