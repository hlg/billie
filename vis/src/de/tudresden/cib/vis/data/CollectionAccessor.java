package de.tudresden.cib.vis.data;

import de.tudresden.cib.vis.filter.Condition;
import de.tudresden.cib.vis.filter.ConditionFilter;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

public class CollectionAccessor<T> extends DataAccessor<T, Condition<T>> {

    private Collection<T> data;
    private ConditionFilter<T> filter;

    public CollectionAccessor(Collection<T> data) {
        this.data = data;
        filter = new ConditionFilter<T>();
    }

    public Iterator<T> iterator() {
        return data.iterator();
    }

    public void read(URL url) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterable<? extends T> filter(Condition<T> condition) {
        return filter.filter(condition, this);
    }

    @Override
    public Condition<T> getDefaultCondition() {
        return new Condition<T>() {
            @Override
            public boolean matches(T data) {
                return true;
            }
        };
    }
}
