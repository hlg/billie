import java.util.Collection;
import java.util.Iterator;

public class CollectionAccessor implements DataAccessor<Collection>{

    private Collection data;

    CollectionAccessor(Collection data){
        this.data = data;
    }

    public Iterator iterator() {
        return data.iterator();
    }
}
