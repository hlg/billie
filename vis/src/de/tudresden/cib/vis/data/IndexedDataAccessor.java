package de.tudresden.cib.vis.data;

import java.io.IOException;
import java.io.InputStream;

public abstract class IndexedDataAccessor<E> extends DataAccessor<E> {

    public abstract void index();
    public abstract E getIndexed(String objectID);
    public abstract void read(InputStream inputStream, String namespace, long size) throws IOException;

}
