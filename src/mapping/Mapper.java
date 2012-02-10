package mapping;

import data.DataAccessor;
import visualization.VisBuilder;
import visualization.VisFactory;

import java.util.*;

public class Mapper {
    ClassMap propertyMaps = new ClassMap();
    public DataAccessor dataAccessor;
    public VisFactory visFactory;
    public VisBuilder visBuilder;
    // TODO: collect and keep a map of already mapped objects

    public <S, T extends VisFactory.GraphObject> void addMapping(PropertyMap<S, T> propertyMap, Class<S> sourceClass, Class<T> targetClass) {
        // TODO: find a way to get the sourceclass from the Propertymap or use generics for propertymap matching
        propertyMap.with(visFactory.getProvider(targetClass));
        propertyMaps.addPropertyMap(sourceClass, propertyMap);
    }

    public void map() throws TargetCreationException {
        visBuilder.init();
        Iterator iterator = dataAccessor.iterator();
        while (iterator.hasNext()) {
            Object source = iterator.next();
            mapAndBuild(source);
        }
        visBuilder.finish();
    }

    private <A> void mapAndBuild(A source) throws TargetCreationException {
        Class<A> sClass = (Class<A>) source.getClass();
        propertyMaps.get(sClass);
        for(PropertyMap<? super A,?> propertyMap: propertyMaps.getPropertyMaps(sClass)){
            propertyMap.map(source);
            visBuilder.addPart(propertyMap.graphObject);
        }
    }
    
    class ClassMap extends HashMap<Class, Collection<PropertyMap>>{
        
        public <S> void addPropertyMap(Class<S> sourceClass, PropertyMap<S,?> propertyMap){
            if(!containsKey(sourceClass)){
                put(sourceClass, new HashSet<PropertyMap>());
            }
            get(sourceClass).add(propertyMap);
        }
        
        public <S> Collection<PropertyMap<S,?>> getPropertyMaps(Class<S> sourceClass){
            Collection<PropertyMap<S,?>> res = new ArrayList<PropertyMap<S, ?>>();
            for(PropertyMap pm: get(sourceClass)){
                res.add((PropertyMap<S,?>)pm);
            }
            return  res;
        }

    }
}
