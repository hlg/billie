package de.tudresden.cib.vis.scene;

public abstract class Change<S> {
    protected S graph;

    protected abstract void configure();

    public void change(S graph) {
        this.graph = graph;
        configure();
    }

}
