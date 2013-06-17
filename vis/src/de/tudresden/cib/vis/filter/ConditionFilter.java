package de.tudresden.cib.vis.filter;

import java.util.Iterator;

public class ConditionFilter<I> implements Filter.EntityEntity<Condition,I> {
    @Override
    public Iterator<I> filter(Condition condition, final Iterator<I> toBefiltered) {
        return new Iterator<I>() {
            @Override
            public boolean hasNext() {
                return toBefiltered.hasNext();
            }

            @Override
            public I next() {
                return toBefiltered.next();
            }

            @Override
            public void remove() {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }
}
