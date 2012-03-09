package data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class DataAccessor<E> implements Iterable<E> {

    private Map<String, Folder<E, Number>> stats = new HashMap<String, Folder<E, Number>>();


    public void addPreprocessor(String name, Folder<E, Number> statisticsFunction) {
        stats.put(name, statisticsFunction);
    }

    public void preProcess() {
        for (Folder<E, Number> stat : stats.values()) {
            stat.fold(this);
        }
    }

    abstract void setInput(File file) throws IOException;

    public static abstract class Folder<A, B> {
        private B result;

        public Folder(B start){
            result = start;
        }
        
        public abstract B function(B b, A a);

        public void fold(Iterable<A> list) {
            for (A elem : list) {
                result = function(result, elem);
            }
        }
        
        public B getResult(){
            return result;
        }
    }

}
