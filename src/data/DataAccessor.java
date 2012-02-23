package data;

import java.io.InputStream;
import java.util.Iterator;

public abstract class DataAccessor<E> implements Iterable<E> {

    public abstract Iterator<E> iterator();
}
