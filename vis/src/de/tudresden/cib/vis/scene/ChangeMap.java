package de.tudresden.cib.vis.scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangeMap extends HashMap<VisFactory2D.GraphObject, List<Change>> {

    public <S extends VisFactory2D.GraphObject> void addChange(Change<S> change, S  graphObject) {
        if(!containsKey(graphObject)) put(graphObject, new ArrayList<Change>());
        get(graphObject).add(change);
    }

    public void changeAll() {
        for(Map.Entry<VisFactory2D.GraphObject, List<Change>> changeEntry : this.entrySet()){
            VisFactory2D.GraphObject toBeChanged = changeEntry.getKey();
            for(Change change : changeEntry.getValue()) change.change(toBeChanged);
        }
    }
}
