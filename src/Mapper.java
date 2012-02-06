import com.sun.xml.internal.bind.v2.TODO;

import java.util.*;

public class Mapper<S,T> {
    Map<Class, PropertyMap> propertyMaps = new HashMap<Class, PropertyMap>();
    DataAccessor<S> dataAccessor;
    VisBuilder<T> visBuilder;
    // TODO: collect and keep a map of already mapped objects

    public void addMapping(PropertyMap propertyMap, Class sourceClass){
        // TODO: find a way to get the sourceclass from the Propertymap or use generics for propertymap matching
        propertyMaps.put(sourceClass, propertyMap);
    }

    public void map(S data){
        Iterator iterator = dataAccessor.iterator();
        while (iterator.hasNext()){
            Object next = iterator.next();
            PropertyMap map = propertyMaps.get(next.getClass());
            map.map(next, visBuilder);   // TODO: pass visbuilder to propertymapper instead of provider
        }
    }
}
