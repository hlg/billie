package data;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class DataAccessor<E> implements Iterable<E> {

    private Map<String, Folding<E, Number>> stats = new HashMap<String, Folding<E, Number>>();


    public void addPreprocessor(String name, Folding<E, Number> statisticsFunction) {
        stats.put(name, statisticsFunction);
    }

    public void preProcess() {
        for (Folding<E, Number> stat : stats.values()) {
            stat.fold(this);
        }
    }

    public abstract void setInput(File file) throws IOException;

    public static abstract class Folding<A, B> {
        private B result;

        public Folding(B start) {
            result = start;
        }

        public abstract B function(B b, A a);

        public void fold(Iterable<A> list) {
            for (A elem : list) {
                result = function(result, elem);
            }
        }

        public B getResult() {
            return result;
        }
    }

}
