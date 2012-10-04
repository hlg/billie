package de.tudresden.cib.vis.data;

import java.util.Collection;
import java.util.List;

public interface Hierarchic<T> extends DataObject<T> {
    public Hierarchic<T> getParent();
    public Collection<Hierarchic<? extends T>> getChildren();
    public void addChild(Hierarchic<T> child);
    public List<Integer> getPath();
    public int getNodeSize();
    void setNodeSize(int size);
    int getNodesBefore();
    int getDepth();
}
