package de.tudresden.cib.vis.scene;

import java.util.*;

public class TimeLine<S extends VisFactory2D.GraphObject> extends TreeMap<Integer, Map<Change<S>, Set<S>>> {
    // TODO: separate timelines for separate objects?
    // TODO: execution order for changes sorted according to their generation
    // TODO: swap nested map indizes (change / graph object)

    public void addChange (int time, S graph, Change<S> change) {
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

}
