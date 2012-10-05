package de.tudresden.cib.vis.data;

import java.util.Collection;
import java.util.HashSet;

public abstract class HierarchicBase<T> implements Hierarchic<T> {
        private int nodeSize;
        private Collection<Hierarchic<? extends T>> children = new HashSet<Hierarchic<? extends T>>();
        private Hierarchic<? extends T> parent;
        private final int nodesBefore;
        private final int depth;
        private final T object;

        public HierarchicBase(Hierarchic<? extends T> parentNode, int nodesBefore, int depth, T object) {
            this.parent = parentNode;
            this.nodesBefore = nodesBefore;
            this.depth = depth;
            this.object = object;
        }

        public Hierarchic<? extends T> getParent() {
            return parent;
        }

        public Collection<Hierarchic<? extends T>> getChildren() {
            return children;
        }

        public void addChild(Hierarchic<? extends T> child){
            children.add(child);
        }

        public void setNodeSize(int nodeSize){
            this.nodeSize = nodeSize;
        }

        public int getNodesBefore() {
            return nodesBefore;
        }

        public int getDepth() {
            return depth;
        }

        public int getNodeSize() {
            return nodeSize;
        }

        public T getObject() {
            return object;
        }

}
