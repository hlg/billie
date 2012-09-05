package de.tudresden.cib.vis.scene;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ChangeMap extends HashMap<VisFactory2D.GraphObject, List<Change>> {

    public <S extends VisFactory2D.GraphObject> void addChange(Change<S> change, S  graphObject) {
        if(!containsKey(graphObject)) put(graphObject, new ArrayList<Change>());
        get(graphObject).add(change);
    }

    public void changeAll() {
        for(VisFactory2D.GraphObject toBeChanged : this.keySet()) applyChanges(toBeChanged);
    }

    public void change(Collection<VisFactory2D.GraphObject> graphObjects){
        for (VisFactory2D.GraphObject toBeChanged: graphObjects) applyChanges(toBeChanged);
    }

    public void change(VisFactory2D.GraphObject graphObject){
        applyChanges(graphObject);
    }

    private void applyChanges(VisFactory2D.GraphObject toBeChanged) {
        List<Change> changes = get(toBeChanged);
        if (changes!=null) for(Change change : changes) change.change(toBeChanged);
    }

}
