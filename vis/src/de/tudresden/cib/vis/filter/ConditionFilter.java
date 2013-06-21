package de.tudresden.cib.vis.filter;

import java.util.Iterator;

public class ConditionFilter<I> implements Filter.EntityEntity<Condition<I>,I> {

    @Override
    public Iterable<I> filter(final Condition<I> condition, final Iterable<I> toBefiltered) {
        return new Iterable<I>() {
            @Override
            public Iterator<I> iterator() {
                return new Iterator<I>() {
                    boolean hasCached =false;
                    I cached = null;

                    Iterator<I> parent = toBefiltered.iterator();

                    @Override
                    public boolean hasNext() {
                        if (hasCached) return true;
                        cached = findNextMatch();
                        hasCached = cached != null;
                        return hasCached;
                    }

                    private I findNextMatch() {
                        I candidate = null;
                        while (parent.hasNext() && (candidate == null||!condition.matches(candidate))) { candidate = parent.next(); }
                        return candidate;
                    }

                    @Override
                    public I next() {
                        if (hasCached) {
                            hasCached =false; return cached;
                        }
                        return findNextMatch();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };            }
        };
    }
}
