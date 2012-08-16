package visMapping.data;

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

    public void setInput(InputStream inputStream) throws IOException {
        throw new UnsupportedOperationException();
        // TODO inheritance : FileAccessor, IndexableAccessor ...
    }
}