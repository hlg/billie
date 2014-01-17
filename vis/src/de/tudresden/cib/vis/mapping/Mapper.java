package de.tudresden.cib.vis.mapping;

import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.scene.Event;
import de.tudresden.cib.vis.scene.SceneManager;
import de.tudresden.cib.vis.scene.VisBuilder;
import de.tudresden.cib.vis.scene.VisFactory2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Mapper<E, C, G extends VisFactory2D.GraphObject,S> {
    Map<C, ClassMap> propertyMapsByConditions = new HashMap<C, ClassMap>();
    private DataAccessor<E, C> dataAccessor;
    private VisFactory2D visFactory;
    private VisBuilder<G, S> visBuilder;
    private SceneManager<E, S> sceneManager = new SceneManager<E, S>();
    private Logger logger = LoggerFactory.getLogger(getClass());

    // TODO: move to data accessor?
    private Map<String, DataAccessor.Folding<E, ? extends Number>> statistics = new HashMap<String, DataAccessor.Folding<E, ? extends Number>>();
    private Map<String, PreProcessing<Double>> globals = new HashMap<String, PreProcessing<Double>>();

    public Mapper(DataAccessor<E, C> dataAccessor, VisFactory2D visFactory, VisBuilder<G,S> visBuilder) {
        this.dataAccessor = dataAccessor;
        this.visFactory = visFactory;
        this.visBuilder = visBuilder;
    }

    public <S extends E, T extends VisFactory2D.GraphObject> void addMapping(PropertyMap<S, T> propertyMap) {
        addMapping(dataAccessor.getDefaultCondition(), propertyMap);
    }

    public <S extends E, T extends VisFactory2D.GraphObject> void addMapping(C condition, PropertyMap<S, T> propertyMap) {
        propertyMap.with(visFactory.getProvider(propertyMap.graphClass));
        propertyMap.with(sceneManager);
        if(!propertyMapsByConditions.containsKey(condition)) propertyMapsByConditions.put(condition, new ClassMap());
        propertyMapsByConditions.get(condition).addPropertyMap(propertyMap.dataClass, propertyMap);
    }

    public SceneManager<E, S> map() throws TargetCreationException {
        logger.info("start mapping data to scenegraph");
        visBuilder.init();
        preProcess();
        mainPass();
        for (final Map.Entry<Event, Collection<VisFactory2D.GraphObject>> trigger : sceneManager.getTriggers().entrySet()) {
           visBuilder.addTriggers(trigger.getKey(), trigger.getValue(), sceneManager);
        }
        visBuilder.finish();
        sceneManager.setScene(visBuilder.getScene());
        sceneManager.setUiContext(visBuilder.getUiContext());
        return sceneManager;
    }

    public SceneManager<E, S> getSceneManager(){
        return sceneManager;
    }

    private void preProcess() {
        for (Map.Entry<String, DataAccessor.Folding<E, ? extends Number>> stats : statistics.entrySet()) {
            stats.getValue().fold(dataAccessor);
            logger.info(String.format("preprocessed: %s = %s", stats.getKey(), stats.getValue().getResult()));
        }
        for (Map.Entry<String, PreProcessing<Double>> global : globals.entrySet()){
            global.getValue().evaluate();
            logger.info(String.format("evaluated global: %s = %s", global.getKey(), global.getValue().getEvaluatedResult()));
        }
    }

    private void mainPass() throws TargetCreationException {
        for (C condition: propertyMapsByConditions.keySet()){
            int mappingIndex = 0; // TODO: per class or even per property map index?
            for(E source :  dataAccessor.filter(condition)){
                if(mapAndBuild(source, mappingIndex, propertyMapsByConditions.get(condition))) mappingIndex++;
            }
        }
        sceneManager.logStatistics(logger);
    }

    private boolean mapAndBuild(E source, int mappingIndex, ClassMap classMap) throws TargetCreationException {
        Collection<PropertyMap<E, ?>> matchingPropMaps = classMap.getPropertyMaps(source);
        for (PropertyMap<? super E, ?> propertyMap : matchingPropMaps) {
                propertyMap.map(source, mappingIndex);
                visBuilder.addPart((G) propertyMap.graphObject); // TODO: make sure provider is the right one
        }
        return !matchingPropMaps.isEmpty();
    }

    public <T extends Number> void addStatistics(String name, DataAccessor.Folding<E, T> folder) {
        statistics.put(name, folder);
    }
    
    public Number getStats(String name) {
        return statistics.get(name).getResult();
    }

    public void addGlobal(String name, PreProcessing<Double> preprocessing) {
        preprocessing.setMapper(this);
        globals.put(name, preprocessing);
    }

    public Double getGlobal(String name) {
        return globals.get(name).getEvaluatedResult();
    }

    class ClassMap extends HashMap<Class, Collection<PropertyMap>> {

        public <S> void addPropertyMap(Class<S> sourceClass, PropertyMap<S, ?> propertyMap) {
            if (!containsKey(sourceClass)) {
                put(sourceClass, new ArrayList<PropertyMap>()); // todo: make sure they are unique
            }
            get(sourceClass).add(propertyMap);
        }

        public <S> Collection<PropertyMap<S, ?>> getPropertyMaps(S source) {
            Collection<PropertyMap<S, ?>> res = new ArrayList<PropertyMap<S, ?>>();
            for(Map.Entry<Class, Collection<PropertyMap>> classMap : this.entrySet()){
                if(classMap.getKey().isInstance(source)) for (PropertyMap pm : classMap.getValue()) res.add(pm);
            }
            return res;
        }
    }

    public static abstract class PreProcessing<R> {
        protected Mapper mp;
        private R result;

        protected void setMapper(Mapper mapper) {
            mp = mapper;
        }

        protected void evaluate(){
            result = getResult();
        }

        protected R getEvaluatedResult(){
            return result;
        }

        public abstract R getResult();
    }
}
