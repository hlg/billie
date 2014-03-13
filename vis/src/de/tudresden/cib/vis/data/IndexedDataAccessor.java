package de.tudresden.cib.vis.data;

import java.io.IOException;
import java.net.URL;

public abstract class IndexedDataAccessor<E, C> extends DataAccessor<E, C> {

    public String namespace = "";

    public abstract void index() throws DataAccessException;
    public abstract E getIndexed(String objectID);

    public void read(URL url, String namespace) throws IOException, DataAccessException {
        read(url);
        this.namespace = namespace==null ? "" : namespace + "::";
    }

    public abstract void read(URL url) throws IOException, DataAccessException;

}
