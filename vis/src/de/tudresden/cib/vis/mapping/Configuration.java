package de.tudresden.cib.vis.mapping;

import de.tudresden.cib.vis.TriggerListener;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.scene.VisFactory2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Configuration<E, C> {
    public Collection<TriggerListener<E>> listeners = new ArrayList<TriggerListener<E>>();
    private Map<C, ClassMap> propertyMapsByConditions = new HashMap<C, ClassMap>();
    private Map<String, DataAccessor.Folding<E, ? extends Number>> statistics = new HashMap<String, DataAccessor.Folding<E, ? extends Number>>();
    private Map<String, Mapper.PreProcessing<Double>> globals = new HashMap<String, Mapper.PreProcessing<Double>>();

    public void config(){}

    public void addMapping(PropertyMap<? extends E, ? extends VisFactory2D.GraphObject> propMap) {
        addMapping(null, propMap);
    }

    public <T extends Number> void addStatistics(String name, DataAccessor.Folding<E, T> folder) {
        statistics.put(name, folder);
    }

    public Number getStats(String name) {
        return statistics.get(name).getResult();
    }
    public void addGlobal(String name, Mapper.PreProcessing<Double> preprocessing) {
        globals.put(name, preprocessing);
    }

    public Double getGlobal(String name) {
        return globals.get(name).getEvaluatedResult();
    }

    public <E1 extends E,G1 extends VisFactory2D.GraphObject> void addMapping(C condition, PropertyMap<E1, G1> propertyMap) {
        if(!propertyMapsByConditions.containsKey(condition)) propertyMapsByConditions.put(condition, new ClassMap());
        propertyMapsByConditions.get(condition).addPropertyMap(propertyMap.dataClass, propertyMap);
    }


    public Map<C, ClassMap> getPropertyMapsByConditions() {
        return this.propertyMapsByConditions;
    }

    public Map<String, DataAccessor.Folding<E, ? extends Number>> getStatistics() {
        return statistics;
    }

    public Map<String, Mapper.PreProcessing<Double>> getGlobals() {
        return globals;
    }
}
