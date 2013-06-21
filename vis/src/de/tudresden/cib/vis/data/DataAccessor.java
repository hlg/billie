package de.tudresden.cib.vis.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class DataAccessor<E, C> implements Iterable<E> {

    public abstract void read(InputStream inputStream, long size) throws IOException, DataAccessException;

    public void read(File file) throws IOException, DataAccessException {
        if(!file.isDirectory()){
            read(new FileInputStream(file), file.length());
        } else {
            readFromFolder(file);
        }
    }

    public abstract void readFromFolder(File directory) throws DataAccessException; // TODO: implemented in one subclass only -> this smells

    public abstract Iterable<? extends E> filter(C condition);

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
