package de.tudresden.cib.vis.mapping;

import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.scene.SceneManager;
import de.tudresden.cib.vis.scene.VisBuilder;
import de.tudresden.cib.vis.scene.VisFactory2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Mapper<E,G extends VisFactory2D.GraphObject,S> {
    ClassMap propertyMaps = new ClassMap();
    private DataAccessor<E> dataAccessor;
    private VisFactory2D visFactory;
    private VisBuilder<G, S> visBuilder;
    private SceneManager<E, S> sceneManager = new SceneManager<E, S>();
    private Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, DataAccessor.Folding<E, ? extends Number>> statistics = new HashMap<String, DataAccessor.Folding<E, ? extends Number>>();
    private Map<String, PreProcessing<Double>> globals = new HashMap<String, PreProcessing<Double>>();

    public Mapper(DataAccessor<E> dataAccessor, VisFactory2D visFactory, VisBuilder<G,S> visBuilder) {
        this.dataAccessor = dataAccessor;
        this.visFactory = visFactory;
        this.visBuilder = visBuilder;
    }

    public <S extends E, T extends VisFactory2D.GraphObject> void addMapping(PropertyMap<S, T> propertyMap) {
        propertyMap.with(visFactory.getProvider(propertyMap.graphClass));
        propertyMap.with(sceneManager);
        propertyMaps.addPropertyMap(propertyMap.dataClass, propertyMap);
    }


    public SceneManager<E, S> map() throws TargetCreationException {
        logger.info("start mapping data to scenegraph");
        visBuilder.init();
        preProcess();
        mainPass();
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
    }

    private void mainPass() throws TargetCreationException {
        int mappingIndex = 0; // TODO: per class or even per property map index?
        for (E source : dataAccessor) {
            if (mapAndBuild(source, mappingIndex)) mappingIndex++;
        }
        sceneManager.logStatistics(logger);
    }

    private boolean mapAndBuild(E source, int mappingIndex) throws TargetCreationException {
        Class<E> sClass = (Class<E>) source.getClass();
        Collection<PropertyMap<E, ?>> matchingPropMaps = propertyMaps.getPropertyMaps(sClass);
        boolean matchedAny = false;
        for (PropertyMap<? super E, ?> propertyMap : matchingPropMaps) {
            if (propertyMap.checkCondition(source)) {
                matchedAny = true;
                propertyMap.map(source, mappingIndex);
                visBuilder.addPart((G) propertyMap.graphObject); // TODO: make sure provider is the right one
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

    class ClassMap extends HashMap<Class, Collection<PropertyMap>> {

        public <S> void addPropertyMap(Class<S> sourceClass, PropertyMap<S, ?> propertyMap) {
            if (!containsKey(sourceClass)) {
                put(sourceClass, new ArrayList<PropertyMap>()); // todo: make sure they are unique
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
