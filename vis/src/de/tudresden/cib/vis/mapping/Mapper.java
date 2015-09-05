package de.tudresden.cib.vis.mapping;

import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.scene.Event;
import de.tudresden.cib.vis.scene.SceneManager;
import de.tudresden.cib.vis.scene.VisBuilder;
import de.tudresden.cib.vis.scene.VisFactory2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Mapper<E, C, G extends VisFactory2D.GraphObject,S> {
    Map<C, ClassMap> propertyMapsByConditions;
    private DataAccessor<E, C> dataAccessor;
    private VisFactory2D visFactory;
    private VisBuilder<G, S> visBuilder;
    private SceneManager<E, S> sceneManager = new SceneManager<E, S>();
    private Logger logger = LoggerFactory.getLogger(getClass());

    public Mapper(DataAccessor<E, C> dataAccessor, VisFactory2D visFactory, VisBuilder<G,S> visBuilder) {
        this.dataAccessor = dataAccessor;
        this.visFactory = visFactory;
        this.visBuilder = visBuilder;
    }

    public SceneManager<E,S> map(Configuration<E,C,S> configuration) throws TargetCreationException {
        this.propertyMapsByConditions = configuration.getPropertyMapsByConditions();
        logger.info("start mapping data to scenegraph");
        visBuilder.init();
        preProcess(configuration.getStatistics(), configuration.getGlobals());
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

    private void preProcess(Map<String, DataAccessor.Folding<E, ? extends Number>> statistics, Map<String, PreProcessing<Double>> globals) {
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
            for(E source :  dataAccessor.filter(condition==null ? dataAccessor.getDefaultCondition() : condition)){
                if(mapAndBuild(source, mappingIndex, propertyMapsByConditions.get(condition))) mappingIndex++;
            }
        }
        sceneManager.logStatistics(logger);
    }

    private boolean mapAndBuild(E source, int mappingIndex, ClassMap classMap) throws TargetCreationException {
        List<PropertyMap<E, G>> matchingPropMaps = classMap.getPropertyMaps(source);
        for (PropertyMap<E, G> propertyMap : matchingPropMaps) {
                propertyMap.with(sceneManager);
                propertyMap.with(visFactory.getProvider(propertyMap.graphClass));
                propertyMap.map(source, mappingIndex);
                visBuilder.addPart(propertyMap.graphObject); // TODO: make sure provider is the right one
        }
        return !matchingPropMaps.isEmpty();
    }

    public static abstract class PreProcessing<R> {
        private R result;

        protected void evaluate(){
            result = getResult();
        }

        protected R getEvaluatedResult(){
            return result;
        }

        public abstract R getResult();
    }
}
