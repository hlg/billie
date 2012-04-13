package visualization;

public interface VisBuilder<T extends VisFactory2D.GraphObject,S> {
    void init();
    void addPart(T graphicalObject);
    void finish();
    S getScene();
}
