package de.tudresden.cib.vis.filter;

import java.util.Iterator;

public interface Filter<C,I,O> {
    public interface ModelModel<C,M> extends Filter<C,M,M> {
        public M filter(C condition, M toBeFiltered);
    }
    public interface ModelEntity<C,M,E> extends Filter<C,M,Iterator<E>> {
        public Iterator<E> filter(C condition, M toBeFiltered);
    }
    public interface EntityEntity<C,E> extends Filter<C,Iterator<E>, Iterator<E>> {
        public Iterator<E> filter(C condition, Iterator<E> toBefiltered);
    }
    public O filter(C condition, I input);
}
