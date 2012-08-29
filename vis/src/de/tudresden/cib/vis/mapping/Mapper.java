package de.tudresden.cib.vis.mapping;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.scene.SceneManager;
import de.tudresden.cib.vis.scene.VisBuilder;
import de.tudresden.cib.vis.scene.VisFactory2D;

import java.util.*;

public class Mapper<E> {
    ClassMap propertyMaps = new ClassMap();
    private DataAccessor<E> dataAccessor;
    private VisFactory2D visFactory;
    private VisBuilder visBuilder;
    private SceneManager sceneManager = new SceneManager();

    private Map<String, DataAccessor.Folding<E, ? extends Number>> statistics = new HashMap<String, DataAccessor.Folding<E, ? extends Number>>();
    private Map<String, PreProcessing<Double>> globals = new HashMap<String, PreProcessing<Double>>();
    private BiMap<E, VisFactory2D.GraphObject> mapped = HashBiMap.create();

    public Mapper(DataAccessor<E> dataAccessor, VisFactory2D visFactory, VisBuilder visBuilder) {
        this.dataAccessor = dataAccessor;
        this.visFactory = visFactory;
        this.visBuilder = visBuilder;
    }

    public <S, T extends VisFactory2D.GraphObject> void addMapping(PropertyMap<S, T> propertyMap) {
        propertyMap.with(visFactory.getProvider(propertyMap.graphClass));
        propertyMap.with(sceneManager.getTimeLine(propertyMap.graphClass));
        propertyMaps.addPropertyMap(propertyMap.dataClass, propertyMap);
    }


    public Object map() throws TargetCreationException {
        visBuilder.init();
        preProcess();
        mainPass();
        visBuilder.finish();
        return visBuilder.getScene();
    }

    public SceneManager getSceneManager(){
        return sceneManager;
    }

    private void preProcess() {
        for (DataAccessor.Folding<E, ?> stats : statistics.values()) {
            stats.fold(dataAccessor);
        }
    }

    private void mainPass() throws TargetCreationException {
        int mappingIndex = 0; // TODO: per class or even per property map index?
        for (E source : dataAccessor) {
            if (mapAndBuild(source, mappingIndex)) mappingIndex++;
        }
    }

    private boolean mapAndBuild(E source, int mappingIndex) throws TargetCreationException {
        Class<E> sClass = (Class<E>) source.getClass();
        Collection<PropertyMap<E, ?>> matchingPropMaps = propertyMaps.getPropertyMaps(sClass);
        boolean matchedAny = false;
        for (PropertyMap<? super E, ?> propertyMap : matchingPropMaps) {
            if (propertyMap.checkCondition(source)) {
                matchedAny = true;
                propertyMap.map(source, mappingIndex);
                mapped.put(source, propertyMap.graphObject);
                visBuilder.addPart(propertyMap.graphObject);
            }
        }
        return matchedAny;

    }

    public <T extends Number> void addStatistics(String name, DataAccessor.Folding<E, T> folder) {
        statistics.put(name, folder);
    }
    
    public Number getStats(String name) {
        return statistics.get(name).getResult();
    }

    public void addGlobal(String name, PreProcessing<Double> uPmax) {
        uPmax.setMapper(this);
        globals.put(name, uPmax);
    }

    public Double getGlobal(String name) {
        return globals.get(name).getResult();
    }

    public VisFactory2D.GraphObject getGraph(E data) {
        return mapped.get(data);
    }

    public E getData(VisFactory2D.GraphObject graphObject) {
        return mapped.inverse().get(graphObject);
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

    public static abstract class PreProcessing<R> {
        protected Mapper mp;

        protected void setMapper(Mapper mapper) {
            mp = mapper;
        }

        public abstract R getResult();
    }
}
