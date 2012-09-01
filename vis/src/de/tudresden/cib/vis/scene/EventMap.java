package de.tudresden.cib.vis.scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventMap extends HashMap<VisFactory2D.GraphObject, List<Change>> {

    public void addChange(Change change, VisFactory2D.GraphObject graphObject) {
        if(!containsKey(graphObject)) put(graphObject, new ArrayList<Change>());
        get(graphObject).add(change);
    }

}
