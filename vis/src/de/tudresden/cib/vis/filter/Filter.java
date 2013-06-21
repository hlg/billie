package de.tudresden.cib.vis.filter;

public interface Filter<C,I,O> {
    public interface ModelModel<C,M> extends Filter<C,M,M> {
        public M filter(C condition, M toBeFiltered);
    }
    public interface ModelEntity<C,M,E> extends Filter<C,M,Iterable<E>> {
        public Iterable<E> filter(C condition, M toBeFiltered);
    }
    public interface EntityEntity<C,E> extends Filter<C,Iterable<E>, Iterable<E>> {
        public Iterable<E> filter(C condition, Iterable<E> toBefiltered);
    }
    public O filter(C condition, I input);
}
