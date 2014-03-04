package de.tudresden.cib.vis.data.multimodel;

import cib.lib.gaeb.model.gaeb.TgItem;
import cib.lib.gaeb.model.gaeb.TgQtySplit;
import cib.mf.qto.model.AnsatzType;
import cib.mf.schedule.model.activity11.Activity;
import de.tudresden.cib.vis.data.bimserver.EMFIfcHierarchicAcessor;
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser;
import net.fortuna.ical4j.model.component.VEvent;

import java.util.*;

public class LinkedObject<T> {
    T keyObject;
    Collection<ResolvedLink> links = new HashSet<ResolvedLink>();

    public LinkedObject(T keyObject) {
        this.keyObject = keyObject;
    }

    public Collection<ResolvedLink> getResolvedLinks() {
        return links;
    }

    public T getKeyObject() {
        return keyObject;
    }

    public void addLink(ResolvedLink link) {
        links.add(link);
    }

    public static class ResolvedLink {
        private HashMap<EMTypes, Map<String, List<?>>> linkedObjects = new HashMap<EMTypes, Map<String, List<?>>>();

        public Map<String, AnsatzType> getLinkedQto() {
            return defaultEmptyList(linkedObjects.get(EMTypes.QTO));
        }

        public Map<String, TgItem> getLinkedBoQ() {
            return defaultEmptyList(linkedObjects.get(EMTypes.GAEB));
        }

        public Map<String, TgQtySplit> getLinkedBoQtySplit() {
            return defaultEmptyList(linkedObjects.get(EMTypes.GAEBSPLIT));
        }


        public Map<String, EMFIfcParser.EngineEObject> getLinkedObject() {
            return defaultEmptyList(linkedObjects.get(EMTypes.IFC));
        }

        public Map<String, Activity> getScheduleObject() {
            return defaultEmptyList(linkedObjects.get(EMTypes.ACTIVITY11));
        }

        public Map<String, VEvent> getLinkedEvent(){
            return defaultEmptyList(linkedObjects.get(EMTypes.ICAL));
        }

        public Map<String,EMFIfcHierarchicAcessor.HierarchicIfc> getLinkedHierarchicIfc(){
            return defaultEmptyList(linkedObjects.get(EMTypes.IFCHIERARCHIC));
        }

        public Map<String, HierarchicGaebAccessor.HierarchicTgItemBoQCtgy> getLinkedHierarchicGaeb(){
            return defaultEmptyList(linkedObjects.get(EMTypes.GAEBHIERARCHIC));
        }

        private <T> Map<String, T> defaultEmptyList(Map<String, List<?>> groupedMap) {
            if (groupedMap == null) return  Collections.emptyMap();
            Map<String,T> firstElementMap = new HashMap<String, T>();
            for(Map.Entry<String, List<?>> entry : groupedMap.entrySet()){
                if (entry.getValue()!=null && !entry.getValue().isEmpty()) firstElementMap.put(entry.getKey(), (T) entry.getValue().get(0));
            }
            return firstElementMap;
        }

        public Collection<AnsatzType> getAllLinkedQtos(String modelId) {
            return defaultEmptyAllList(linkedObjects.get(EMTypes.QTO), modelId);
        }

        public Collection<TgItem> getAllLinkedBoQs(String modelId) {
            return defaultEmptyAllList(linkedObjects.get(EMTypes.GAEB), modelId);
        }

        public Collection<EMFIfcParser.EngineEObject> getAllLinkedObjects(String modelId) {
            return defaultEmptyAllList(linkedObjects.get(EMTypes.IFC), modelId);
        }

        public Collection<Activity> getAllScheduleObjects(String modelId) {
            return defaultEmptyAllList(linkedObjects.get(EMTypes.ACTIVITY11),modelId);
        }

        public Collection<VEvent> getAllLinkedEvents(String modelId) {
            return defaultEmptyAllList(linkedObjects.get(EMTypes.ICAL), modelId);
        }

        public Collection<EMFIfcHierarchicAcessor.HierarchicIfc> getAllLinkedHierarchicIfcs(String modelId){
            return defaultEmptyAllList(linkedObjects.get(EMTypes.IFCHIERARCHIC), modelId);
        }

        public Collection<HierarchicGaebAccessor.HierarchicTgItemBoQCtgy> getAllLinkedHierarchicGaebs(String modelId){
            return defaultEmptyAllList(linkedObjects.get(EMTypes.GAEBHIERARCHIC), modelId);
        }

        public Collection<TgQtySplit> getAllLinkedQtySplits(String modelId){
            return defaultEmptyAllList(linkedObjects.get(EMTypes.GAEBSPLIT), modelId);
        }

        private <T> List<T> defaultEmptyAllList(Map<String, List<?>> groupedMap, String modelId) {
            return groupedMap == null ? Collections.<T>emptyList() : (List<T>) groupedMap.get(modelId);
        }

        public void addObject(String modelId, Object object) {
            EMTypes type = EMTypes.find(object);
            addObject(type, modelId, object);
        }

        private <T> void addObject(EMTypes type, String modelId, T object) {
            if (!linkedObjects.containsKey(type)) linkedObjects.put(type, new HashMap<String, List<?>>());
            if (!linkedObjects.get(type).containsKey(modelId)) linkedObjects.get(type).put(modelId, new ArrayList<T>());
            ((List<T>) linkedObjects.get(type).get(modelId)).add(object);
        }

    }
}
