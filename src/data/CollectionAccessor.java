package data;

import java.util.Collection;
import java.util.Iterator;

public class CollectionAccessor extends DataAccessor<Collection,Object> {

    public CollectionAccessor(Collection data) {
        super(data);
    }

    public Iterator<?> iterator() {
        return data.iterator();
    }
}
