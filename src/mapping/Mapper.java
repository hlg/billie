package mapping;

import data.DataAccessor;
import visualization.VisBuilder;
import visualization.VisFactory;

import java.util.*;

public class Mapper {
    ClassMap propertyMaps = new ClassMap();
    private DataAccessor dataAccessor;
    private VisFactory visFactory;
    private VisBuilder visBuilder;
    private int mappingIndex;
    // TODO: collect and keep a map of already mapped objects

    public Mapper(DataAccessor dataAccessor, VisFactory visFactory, VisBuilder visBuilder) {
        this.dataAccessor = dataAccessor;
        this.visFactory = visFactory;
        this.visBuilder = visBuilder;
    }

    public <S, T extends VisFactory.GraphObject> void addMapping(PropertyMap<S, T> propertyMap) {
        propertyMap.with(visFactory.getProvider(propertyMap.graphClass));
        propertyMaps.addPropertyMap(propertyMap.dataClass, propertyMap);
    }

    public Object map() throws TargetCreationException {
        Iterator<Object> iterator = dataAccessor.iterator();
        mappingIndex = -1;
        visBuilder.init();
        while (iterator.hasNext()) {
            Object source = iterator.next();
            mapAndBuild(source);
        }
        visBuilder.finish();
        return visBuilder.getScene();
    }

    private <A> void mapAndBuild(A source) throws TargetCreationException {
        Class<A> sClass = (Class<A>) source.getClass();
        Collection<PropertyMap<A, ?>> matchingPropMaps = propertyMaps.getPropertyMaps(sClass);
        if(!matchingPropMaps.isEmpty()) mappingIndex++;    // TODO: per class or even per property map index?
        for (PropertyMap<? super A, ?> propertyMap : matchingPropMaps) {
            propertyMap.map(source, mappingIndex);
            visBuilder.addPart(propertyMap.graphObject);
        }
    }

    class ClassMap extends HashMap<Class, Collection<PropertyMap>> {

        public <S> void addPropertyMap(Class<S> sourceClass, PropertyMap<S, ?> propertyMap) {
            if (!containsKey(sourceClass)) {
                put(sourceClass, new HashSet<PropertyMap>());
            }
            get(sourceClass).add(propertyMap);
        }

        public <S> Collection<PropertyMap<S, ?>> getPropertyMaps(Class<S> sourceClass) {
            Collection<PropertyMap<S, ?>> res = new ArrayList<PropertyMap<S, ?>>();
            List<Class<?>> sourceInterfaces = Arrays.asList(sourceClass.getInterfaces());
            for (Map.Entry<Class, Collection<PropertyMap>> classMaps : this.entrySet()) {
                if (sourceClass.equals(classMaps.getKey()) || sourceInterfaces.contains(classMaps.getKey())) {
                    for (PropertyMap pm : classMaps.getValue()) {
                        res.add((PropertyMap<S, ?>) pm);
                    }
                }
            }
            return res;
        }

    }
}
