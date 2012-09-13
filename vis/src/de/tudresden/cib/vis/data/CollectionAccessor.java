package de.tudresden.cib.vis.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

public class CollectionAccessor<T> extends DataAccessor<T> {

    private Collection<T> data;

    public CollectionAccessor(Collection<T> data) {
        this.data = data;
    }

    public Iterator<T> iterator() {
        return data.iterator();
    }

    public void read(InputStream inputStream, long size) throws IOException {
        throw new UnsupportedOperationException();
        // TODO inheritance : FileAccessor, IndexableAccessor ...
    }
}
