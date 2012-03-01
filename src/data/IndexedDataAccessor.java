package data;

import java.io.File;
import java.io.IOException;

public interface IndexedDataAccessor<E> extends DataAccessor<E> {

    public void index();
    public E getIndexed(String objectID);
    public void setInput(File file, String namespace) throws IOException;

}
