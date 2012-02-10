package visualization;

public interface VisBuilder {
    void init();
    void addPart(VisFactory.GraphObject graphicalObject);
    void finish();
}
