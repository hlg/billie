package de.tudresden.cib.vis.data.multimodel;

import cib.lib.gaeb.model.gaeb.TgItem;
import cib.mf.qto.model.AnsatzType;
import cib.mf.schedule.model.activity11.Activity;
import de.tudresden.cib.vis.data.bimserver.EMFIfcHierarchicAcessor;
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser;

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
        private HashMap<MultiModelAccessor.EMTypes, Map<String, ?>> linkedObjects = new HashMap<MultiModelAccessor.EMTypes, Map<String, ?>>();

        public Map<String, AnsatzType> getLinkedQto() {
            return defaultEmptyList((Map<String, AnsatzType>) linkedObjects.get(MultiModelAccessor.EMTypes.QTO));
        }

        public Map<String, TgItem> getLinkedBoQ() {
            return defaultEmptyList((Map<String, TgItem>) linkedObjects.get(MultiModelAccessor.EMTypes.GAEB));
        }

        public Map<String, EMFIfcParser.EngineEObject> getLinkedObject() {
            return defaultEmptyList((Map<String, EMFIfcParser.EngineEObject>) linkedObjects.get(MultiModelAccessor.EMTypes.IFC));
        }

        public Map<String, Activity> getScheduleObjects() {
            return defaultEmptyList((Map<String, Activity>) linkedObjects.get(MultiModelAccessor.EMTypes.ACTIVITY11));
        }

        public Map<String,EMFIfcHierarchicAcessor.HierarchicIfc> getLinkedHierarchicIfc(){
            return defaultEmptyList((Map<String, EMFIfcHierarchicAcessor.HierarchicIfc>) linkedObjects.get(MultiModelAccessor.EMTypes.IFCHIERARCHIC));
        }

        public Map<String, HierarchicGaebAccessor.HierarchicTgItemBoQCtgy> getLinkedHierarchicGaeb(){
            return defaultEmptyList((Map<String, HierarchicGaebAccessor.HierarchicTgItemBoQCtgy>) linkedObjects.get(MultiModelAccessor.EMTypes.GAEBHIERARCHIC));
        }

        private <T> Map<String, T> defaultEmptyList(Map<String, T> stringAnsatzTypeMap) {
            return stringAnsatzTypeMap == null ? Collections.<String, T>emptyMap() : stringAnsatzTypeMap;
        }

        public void addObject(String modelId, Object object) {
            MultiModelAccessor.EMTypes type = MultiModelAccessor.EMTypes.find(object);
            addObject(type, modelId, object);
        }

        private <T> void addObject(MultiModelAccessor.EMTypes type, String modelId, T object) {
            if (!linkedObjects.containsKey(type)) linkedObjects.put(type, new HashMap<String, T>());
            ((Map<String, T>) linkedObjects.get(type)).put(modelId, object);
        }

    }
}
