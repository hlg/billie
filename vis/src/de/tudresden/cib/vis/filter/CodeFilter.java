package de.tudresden.cib.vis.filter;

import java.util.Iterator;

public interface CodeFilter extends Filter<String,?,?> {
    public interface ModelModel<M> extends Filter.ModelModel<M,String>{
        public M filter(M toBeFiltered, String code);
    }
    public interface ModelEntity<M,E> extends Filter.ModelEntity<M,E,String>{
        public Iterator<E> filter(M toBeFiltered, String code);
    }
    public interface EntityEntity<E> extends Filter.EntityEntity<E,String>{
        public Iterator<E> filter(Iterator<E> toBeFiltered, String code);
    }
}
