package de.tudresden.cib.vis.data;

import java.util.Collection;

public interface Hierarchic<T> extends DataObject<T> {
    public Hierarchic<? extends T> getParent();
    public Collection<Hierarchic<? extends T>> getChildren();
    public void addChild(Hierarchic<? extends T> child);
    public int getNodeSize();
    void setNodeSize(int size);
    int getNodesBefore();
    int getDepth();
}
