package de.tudresden.cib.vis.data.multimodel;

import cib.lib.gaeb.model.gaeb.TgItem;
import cib.mf.qto.model.AnsatzType;
import cib.mf.schedule.model.activity.Activity;
import de.mefisto.model.container.ElementaryModelType;
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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
        private Map<String, EMFIfcParser.EngineEObject> ifcObjects = new HashMap<String, EMFIfcParser.EngineEObject>();
        private Map<String, TgItem> gaebObjects = new HashMap<String, TgItem>();
        private Map<String, AnsatzType> qtoObjects = new HashMap<String, AnsatzType>();
        private Map<String, Activity> scheduleObjects = new HashMap<String, Activity>();

        public Map<String, AnsatzType> getLinkedQto() {
            return qtoObjects;
        }

        public Map<String, TgItem> getLinkedBoQ() {
            return gaebObjects;
        }

        public Map<String, EMFIfcParser.EngineEObject> getLinkedObject() {
            return ifcObjects;
        }

        public Map<String, Activity> getScheduleObjects() {
            return scheduleObjects;
        }

        public Map<String, ?> getLinksOfType(ElementaryModelType elementaryModelType) {
            if (elementaryModelType.equals(ElementaryModelType.BO_Q)) return gaebObjects;
            if (elementaryModelType.equals(ElementaryModelType.OBJECT)) return ifcObjects;
            if (elementaryModelType.equals(ElementaryModelType.QTO)) return qtoObjects;
            if (elementaryModelType.equals(ElementaryModelType.ACTIVITY)) return scheduleObjects;
            return null;
        }

        public void addObject(String modelId, Object object) {
            if (object instanceof TgItem) gaebObjects.put(modelId, (TgItem) object);
            if (object instanceof EMFIfcParser.EngineEObject)
                ifcObjects.put(modelId, (EMFIfcParser.EngineEObject) object);
            if (object instanceof AnsatzType) qtoObjects.put(modelId, (AnsatzType) object);
            if (object instanceof Activity) scheduleObjects.put(modelId, (Activity) object);
        }

    }
}
