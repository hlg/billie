package de.tudresden.cib.vis.filter;

import java.util.Iterator;

public interface CodeFilter<I,O> extends Filter<String,I,O> {
    public interface ModelModel<M> extends Filter.ModelModel<String,M>{
        public M filter(String code, M toBeFiltered);
    }
    public interface ModelEntity<M,E> extends Filter.ModelEntity<String,M,E>{
        public Iterable<E> filter(String code, M toBeFiltered);
    }
    public interface EntityEntity<E> extends Filter.EntityEntity<String,E>{
        public Iterator<E> filter(String code, Iterator<E> toBeFiltered);
    }
}
