package visualization;

public interface VisBuilder<T extends VisFactory.GraphObject,S> {
    void init();
    void addPart(T graphicalObject);
    void finish();
    S getScene();
}
