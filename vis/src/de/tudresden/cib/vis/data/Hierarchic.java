package de.tudresden.cib.vis.data;

import java.util.Collection;
import java.util.List;

public interface Hierarchic<T> extends DataObject<T> {
    public T getParent();
    public Collection<? extends T> getChildren();
    public List<Integer> getPath();
    public int getNodeSize();
    void setNodeSize(int size);
    int getNodesBefore();
    int getDepth();
}
