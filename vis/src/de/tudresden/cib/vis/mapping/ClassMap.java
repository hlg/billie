package de.tudresden.cib.vis.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassMap<S> extends HashMap<Class<S>, List<PropertyMap<? extends S, ?>>> {

    public void addPropertyMap(Class<S> sourceClass, PropertyMap<? extends S, ?> propertyMap) {
        if (!containsKey(sourceClass)) {
            put(sourceClass, new ArrayList<PropertyMap<? extends S,?>>()); // todo: make sure they are unique
        }
        get(sourceClass).add(propertyMap);
    }

    public List<PropertyMap<S, ?>> getPropertyMaps(S source) {
        List<PropertyMap<S, ?>> res = new ArrayList<PropertyMap<S, ?>>();
        for (Map.Entry<Class<S>, List<PropertyMap<? extends S,?>>> classMap : this.entrySet()) {
            if (classMap.getKey().isInstance(source)) for (PropertyMap pm : classMap.getValue()) res.add(pm);
        }
        return res;
    }
}
