package data;

import java.util.Iterator;

public abstract class DataAccessor<S,E> {

    S data;

    public DataAccessor(S data){
        this.data = data;
    }

    public abstract Iterator<? extends E> iterator();
}
