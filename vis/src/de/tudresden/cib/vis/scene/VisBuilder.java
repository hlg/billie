package de.tudresden.cib.vis.scene;

public interface VisBuilder<T extends VisFactory2D.GraphObject,S> {
    void init();
    void addPart(T graphicalObject);
    void finish();
    S getScene();
    UIContext getUiContext();
}
