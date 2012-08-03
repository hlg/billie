package visMapping.visualization;

import java.util.*;

public class TimeLine<S extends VisFactory2D.GraphObject> extends TreeMap<Integer, Map<TimeLine.Change<S>, Set<S>>> {  // TODO: separate timelines for separate objects?

    public void addChange(int time, S graph, Change<S> change) {
        if (!containsKey(time)) {
            put(time, new HashMap<Change<S>, Set<S>>());
        }
        if (!get(time).containsKey(change)) {
            get(time).put(change, new HashSet<S>());
        }
        get(time).get(change).add(graph);
    }

    public void changeAll(int time){
        if(containsKey(time)){
            for(Change<S> change : get(time).keySet()){
                Set<S> objects = get(time).get(change);
                for(S object: objects){
                    change.change(object);
                }
            }
        }
    }
    
    public static abstract class Change<S> {
        protected S graph;

        protected abstract void configure();

        public void change(S graph) {
            this.graph = graph;
            configure();
        }

    }
}
