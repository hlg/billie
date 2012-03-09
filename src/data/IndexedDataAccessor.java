package data;

import java.io.File;
import java.io.IOException;

public abstract class IndexedDataAccessor<E> extends DataAccessor<E> {

    public abstract void index();
    public abstract E getIndexed(String objectID);
    public abstract void setInput(File file, String namespace) throws IOException;

}
