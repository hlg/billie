package de.tudresden.cib.vis.mapping;

import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.scene.TimeLine;
import de.tudresden.cib.vis.scene.VisBuilder;
import de.tudresden.cib.vis.scene.VisFactory2D;

import java.util.*;

public class Mapper<E> {
    ClassMap propertyMaps = new ClassMap();
    private DataAccessor<E> dataAccessor;
    private VisFactory2D visFactory;
    private VisBuilder visBuilder;
    private Map<Class, TimeLine> timeLines = new HashMap<Class, TimeLine>();

    private Map<String, DataAccessor.Folding<E, ? extends Number>> statistics = new HashMap<String, DataAccessor.Folding<E, ? extends Number>>();
    private Map<String, PreProcessing<Double>> globals = new HashMap<String, PreProcessing<Double>>();


    // TODO: collect and keep a map of already mapped objects

    public Mapper(DataAccessor<E> dataAccessor, VisFactory2D visFactory, VisBuilder visBuilder) {
        this.dataAccessor = dataAccessor;
        this.visFactory = visFactory;
        this.visBuilder = visBuilder;
    }

    public <S, T extends VisFactory2D.GraphObject> void addMapping(PropertyMap<S, T> propertyMap) {
        propertyMap.with(visFactory.getProvider(propertyMap.graphClass));
        if (!timeLines.containsKey(propertyMap.graphClass)) timeLines.put(propertyMap.graphClass, new TimeLine<T>());
        propertyMap.with(timeLines.get(propertyMap.graphClass));
        propertyMaps.addPropertyMap(propertyMap.dataClass, propertyMap);
    }


    public Object map() throws TargetCreationException {
        visBuilder.init();
        preProcess();
        mainPass();
        visBuilder.finish();
        return visBuilder.getScene();
    }

    public void animate() {
        if (hasAnimations()) {
            TimerTask animation = new TimerTask() {
                int frame = 0;
                int maxFrame = getLongestTimeLine();

                @Override
                public void run() {
                    for (TimeLine timeLine : timeLines.values()) {
                        timeLine.changeAll(frame);
                    }
                    frame++;
                    if (frame == maxFrame) frame = 0;    // TODO: time line initial state and reset
                }
            };
            new Timer().schedule(animation, 2000, 40);   // 1 frame = 1 hour schedule, 1 frame = 40 ms animation -> 1 s animation = 1 day schedule time
        }
    }

    private int getLongestTimeLine() {
        int longest = 0;
        for (TimeLine<?> timeLine : this.timeLines.values()) {
            longest = Math.max(timeLine.lastKey(), longest);
        }
        return longest;
    }

    private boolean hasAnimations() {
        for (TimeLine timeLine : timeLines.values()) {
            if (!timeLine.isEmpty()) return true;
        }
        return false;
    }

    private void preProcess() {
        for (DataAccessor.Folding<E, ?> stats : statistics.values()) {
            stats.fold(dataAccessor);
        }
    }

    private void mainPass() throws TargetCreationException {
        int mappingIndex = 0; // TODO: per class or even per property map index?
        for (Object source : dataAccessor) {
            if (mapAndBuild(source, mappingIndex)) mappingIndex++;
        }
    }

    private <A> boolean mapAndBuild(A source, int mappingIndex) throws TargetCreationException {
        Class<A> sClass = (Class<A>) source.getClass();
        Collection<PropertyMap<A, ?>> matchingPropMaps = propertyMaps.getPropertyMaps(sClass);
        boolean matchedAny = false;
        for (PropertyMap<? super A, ?> propertyMap : matchingPropMaps) {
            if (propertyMap.checkCondition(source)) {
                matchedAny = true;
                propertyMap.map(source, mappingIndex);
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
