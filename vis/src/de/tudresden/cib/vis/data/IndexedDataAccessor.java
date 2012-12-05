package de.tudresden.cib.vis.data;

import java.io.IOException;
import java.io.InputStream;

public abstract class IndexedDataAccessor<E> extends DataAccessor<E> {

    public String namespace;

    public abstract void index();
    public abstract E getIndexed(String objectID);

    public void read(InputStream inputStream, String namespace, long size) throws IOException, DataAccessException {
        read(inputStream, size);
        this.namespace = namespace==null ? "" : namespace + "::";
    }

    public abstract void read(InputStream inputStream, long size) throws IOException, DataAccessException;

}
