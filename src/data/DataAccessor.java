package data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public interface DataAccessor<E> extends Iterable<E> {

    public void setInput(File file) throws IOException;
}
