package de.tudresden.cib.vis.scene;

import java.util.Collection;

public interface VisBuilder<T extends VisFactory2D.GraphObject,S> {
    void init();
    void addPart(T graphicalObject);
    void finish();
    S getScene();
    UIContext getUiContext();
    void addTriggers(Event event, Collection<VisFactory2D.GraphObject> triggers, SceneManager<?, S> sceneManager);
}
