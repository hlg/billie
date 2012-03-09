package mapping;

import data.DataAccessor;
import org.eclipse.emf.ecore.EObject;
import visualization.VisBuilder;
import visualization.VisFactory2D;

import java.math.BigDecimal;
import java.util.*;

public class Mapper {
    ClassMap propertyMaps = new ClassMap();
    private DataAccessor dataAccessor;
    private VisFactory2D visFactory;
    private VisBuilder visBuilder;
    private int mappingIndex;

    private Map<String, DataAccessor.Folder<?, BigDecimal>> statistics = new HashMap<String, DataAccessor.Folder<?, BigDecimal>>();
    private Map<String, PreProcessing<Double>> globals = new HashMap<String, PreProcessing<Double>>();


    // TODO: collect and keep a map of already mapped objects

    public Mapper(DataAccessor dataAccessor, VisFactory2D visFactory, VisBuilder visBuilder) {
        this.dataAccessor = dataAccessor;
        this.visFactory = visFactory;
        this.visBuilder = visBuilder;
    }

    public <S, T extends VisFactory2D.GraphObject> void addMapping(PropertyMap<S, T> propertyMap) {
        propertyMap.with(visFactory.getProvider(propertyMap.graphClass));
        propertyMaps.addPropertyMap(propertyMap.dataClass, propertyMap);
    }


    public Object map() throws TargetCreationException {
        visBuilder.init();
        preProcess();
        mainPass();
        return visBuilder.getScene();
    }

    private void mainPass() throws TargetCreationException {
        mappingIndex = 0;
        for(Object source: dataAccessor) {
            mapAndBuild(source);
        }
        visBuilder.finish();
    }

    public void preProcess() {
        for(DataAccessor.Folder<?, BigDecimal> stats: statistics.values()){
            stats.fold(dataAccessor);
        }
    }

    private <A> void mapAndBuild(A source) throws TargetCreationException {
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
        if (matchedAny) mappingIndex++;    // TODO: per class or even per property map index?

    }

    public void addStatistics(String name, DataAccessor.Folder<EObject, BigDecimal> folder) {
       statistics.put(name, folder);
    }

    public BigDecimal getStats(String name) {
        // TODO return statistics.get(name).getResult();#
        return statistics.get(name).getResult();
    }

    public void addGlobals(String name, PreProcessing<Double> uPmax) {
        uPmax.setMapper(this);
        globals.put(name, uPmax);
    }
    
    public Double getGlobal(String name){
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
