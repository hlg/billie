package de.tudresden.cib.vis.data;

import java.io.IOException;
import java.net.URL;

public abstract class DataAccessor<E, C> implements Iterable<E> {

    public abstract void read(URL url) throws IOException, DataAccessException;

    public abstract Iterable<? extends E> filter(C condition);

    public abstract C getDefaultCondition();

    public static abstract class Folding<A, B> {
        private B result;

        public Folding(B start) {
            result = start;
        }

        public abstract B function(B b, A a);

        public void fold(Iterable<A> list) {
            for (A elem : list) {
                result = function(result, elem);
            }
        }

        public B getResult() {
            return result;
        }
    }

}
