package de.tudresden.cib.vis.data;

import java.util.Collection;

public abstract class HierarchicalIndexedDataAccessor<E> { // extends IndexedDataAccessor<HierarchicalIndexedDataAccessor.Node<E>> {

    public abstract void indexHierarchical();
    public abstract E getParentOf(E object);
    public abstract Collection<E> getChildrenOf(E object);

    private class Node<E> { // wrapper
    }

    // EnhancedObject
    /*
    example
      with geometry             wrappedIfcEngineObject.getGeometry() ...
      with linkedObjects        linkedObject.getKeyObject(), *.getResolvedLinks() ...
      with hierarchical index   hierObject.getParent(), *.getChildren(), *.getPosition() ...
      with order                sorted.getPrevious(), *.getNext(), *.getIndex() ...
     */
}
