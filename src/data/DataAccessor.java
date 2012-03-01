package data;

import java.io.File;
import java.io.IOException;

public interface DataAccessor<E> extends Iterable<E> {
    void setInput(File file) throws IOException;
}
