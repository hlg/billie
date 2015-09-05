package de.tudresden.cib.vis.configurations;

import cib.lib.gaeb.model.gaeb.TgBoQCtgy;
import cib.lib.gaeb.model.gaeb.TgItem;
import cib.mf.schedule.model.activity11.Activity;
import cib.mf.schedule.model.activity11.Timestamp;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser;
import de.tudresden.cib.vis.data.multimodel.LinkedObject;
import de.tudresden.cib.vis.filter.Condition;
import de.tudresden.cib.vis.mapping.Configuration;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.runtime.java3d.colorTime.TypeAppearance;
import de.tudresden.cib.vis.scene.Change;
import org.bimserver.models.ifc2x3tc1.IfcProduct;
import org.eclipse.emf.ecore.EObject;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import java.util.*;

import static de.tudresden.cib.vis.scene.VisFactory3D.Polyeder;

public class IfcSched_Colored4D<S> extends Configuration<LinkedObject<EMFIfcParser.EngineEObject>, Condition<LinkedObject<EMFIfcParser.EngineEObject>>, S> {

    public void config() {
        this.addStatistics("earliestStart", new DataAccessor.Folding<LinkedObject<EMFIfcParser.EngineEObject>, Long>(Long.MAX_VALUE) {
            @Override
            public Long function(Long aggregator, LinkedObject<EMFIfcParser.EngineEObject> element) {
                for (LinkedObject.ResolvedLink link : element.getResolvedLinks()) {
                    for (Activity activity : link.getScheduleObject().values()) {
                        aggregator = Math.min(aggregator, getTimeInMillis(activity.getActivityData().getStart()));
                    }
                }
                return aggregator;
            }
        });
        this.addStatistics("latestEnd", new DataAccessor.Folding<LinkedObject<EMFIfcParser.EngineEObject>, Long>(Long.MIN_VALUE) {
            @Override
            public Long function(Long aggregator, LinkedObject<EMFIfcParser.EngineEObject> element) {
                for (LinkedObject.ResolvedLink link : element.getResolvedLinks()) {
                    for (Activity activity : link.getScheduleObject().values()) {
                        aggregator = Math.max(aggregator, getTimeInMillis(activity.getActivityData().getEnd()));
                    }
                }
                return aggregator;
            }
        });
        final int scale = 3600 * 1000; // scale to hours TODO: globals?
        final Appearance inactive = TypeAppearance.INACTIVE.getAppearance();
        final Change<Polyeder> reset = new Change<Polyeder>() {
            protected void configure() {
                ((Shape3D) graph).setAppearance(inactive);
            }
        };
        final Appearance activated = TypeAppearance.ACTIVATED.getAppearance();
        final Change<Polyeder> activate = new Change<Polyeder>() {
            public void configure() {
                ((Shape3D) graph).setAppearance(activated);
            }
        };
        final Appearance finished = TypeAppearance.DEACTIVATED.getAppearance();
        final Change<Polyeder> finish = new Change<Polyeder>() {
            public void configure() {
                ((Shape3D) graph).setAppearance(finished);
            }
        };
        PropertyMap<LinkedObject<EMFIfcParser.EngineEObject>, Polyeder> anyActiveMapping = new PropertyMap<LinkedObject<EMFIfcParser.EngineEObject>, Polyeder>() {
            @Override
            protected void configure() {
                graphObject.setNormals(data.getKeyObject().getGeometry().normals);
                graphObject.setVertizes(data.getKeyObject().getGeometry().vertizes);
                graphObject.setIndizes(data.getKeyObject().getGeometry().indizes);
                ((Shape3D) graphObject).setAppearance(inactive);
                long earliestStart = getStats("earliestStart").longValue();
                Map<Long, Integer> activityHistogram = getActivityHistogram(data.getResolvedLinks(), earliestStart);
                if (!activityHistogram.isEmpty() && !activityHistogram.containsKey((long) 0)) addChange(0, reset);
                for (Map.Entry<Long, Integer> histEntry : activityHistogram.entrySet()) {
                    addChange((int) (histEntry.getKey() / scale), histEntry.getValue() > 0 ? activate : (histEntry.getValue() == 0 ? reset : finish));
                }
            }
        };

        final Map<Set<ActivityType>, Change<Polyeder>> colorScale = new HashMap<Set<ActivityType>, Change<Polyeder>>();
        final AppearanceCache appearanceCache = new AppearanceCache();

        /*
        PropertyMap<LinkedObject<EMFIfcParser.EngineEObject>, Polyeder> specialActiveMapping = new PropertyMap<LinkedObject<EMFIfcParser.EngineEObject>, Polyeder>() {
            @Override
            protected void configure() {
                graphObject.setNormals(data.getKeyObject().getGeometry().normals);
                graphObject.setVertizes(data.getKeyObject().getGeometry().vertizes);
                graphObject.setIndizes(data.getKeyObject().getGeometry().indizes);
                ((Shape3D) graphObject).setAppearance(TypeAppearance.DEFAULT.getAppearance()); // TODO: generic model
                long earliestStart = mapper.getStats("earliestStart").longValue();
                Map<Integer, Event> activityList = getActivityList(data.getResolvedLinks(), earliestStart);
                if (!activityList.containsKey(0)) addChange(0, reset);
                for (Map.Entry<Integer, Event> histEntry : activityList.entrySet()) {
                    Event value = histEntry.getValue();
                    if (!colorScale.containsKey(value.active))
                        colorScale.put(value.active, getActiveColorChange(value.active, appearanceCache));
                    addChange(histEntry.getKey() / scale, colorScale.get(value.active));
                }
            }
        };
        */

        this.addMapping(new Condition<LinkedObject<EMFIfcParser.EngineEObject>>() {
            @Override
            public boolean matches(LinkedObject<EMFIfcParser.EngineEObject> data) {
                EObject ifcObject = data.getKeyObject().getObject();
                return ifcObject instanceof IfcProduct && ((IfcProduct) ifcObject).isSetRepresentation();
            }
        },anyActiveMapping);


    }

    private Change<Polyeder> getActiveColorChange(Set<ActivityType> active, final AppearanceCache appearanceCache) {
        final float R = active.contains(ActivityType.SCHALUNG) ? 1 : 0;
        final float G = active.contains(ActivityType.STAHL) ? 1 : 0;
        final float B = active.contains(ActivityType.BETON) ? 1 : 0;
        final float alpha = (R == 0 && G == 0 && B == 0) ? 0.9f : 0;
        return new Change<Polyeder>() {
            @Override
            protected void configure() {
                ((Shape3D) graph).setAppearance(appearanceCache.getAppearance(R, G, B, alpha));
            }
        };
    }

    private Map<Long, Integer> getActivityHistogram(Collection<LinkedObject.ResolvedLink> links, long earliestStart) {
        TreeMap<Long, Integer> result = new TreeMap<Long, Integer>();
        for (LinkedObject.ResolvedLink link : links) {
            for (Activity activity : link.getScheduleObject().values()) {
                long start = getTimeInMillis(activity.getActivityData().getStart()) - earliestStart;
                result.put(start, result.containsKey(start) ? result.get(start) + 1 : 1);
                long end = getTimeInMillis(activity.getActivityData().getEnd()) - earliestStart;
                result.put(end, result.containsKey(end) ? result.get(end) - 1 : -1);
            }
        }
        if (!result.isEmpty()) {
            int current = 0;
            for (long time : result.keySet()) {
                current += result.get(time);
                result.put(time, current);
            }
            long currTime = result.lastKey();
            while (result.get(currTime) == 0) {
                result.put(currTime, -1);
                currTime = result.lowerKey(currTime);
            }
        }
        return result;
    }

    private Map<Integer, Event> getActivityList(Collection<LinkedObject.ResolvedLink> links, long earliestStart) {
        TreeMap<Integer, Event> result = new TreeMap<Integer, Event>();
        for (LinkedObject.ResolvedLink link : links) {
            assert link.getScheduleObject().size() == 1;
            Collection<Activity> activities = link.getScheduleObject().values();
            if (!activities.isEmpty()) {
                Activity theActivity = activities.iterator().next();
                String descr = (link.getLinkedBoQ().size() >= 1)
                        ? descriptionFromSpec(link.getLinkedBoQ().values().iterator().next())
                        : theActivity.getDesc();
                ActivityType type = ActivityType.fromDescription(descr);
                int start = (int) (getTimeInMillis(theActivity.getActivityData().getStart()) - earliestStart);
                if (!result.containsKey(start)) result.put(start, new Event());
                result.get(start).starting.add(type);
                int end = (int) (getTimeInMillis(theActivity.getActivityData().getEnd()) - earliestStart);
                if (!result.containsKey(end)) result.put(end, new Event());
                result.get(end).ending.add(type);
            }
        }
        HashSet<ActivityType> current = new HashSet<ActivityType>();
        for (int time : result.keySet()) {
            current.addAll(result.get(time).starting);
            current.removeAll(result.get(time).ending);
            result.get(time).active.addAll(current);
        }
        return result;
    }

    private String descriptionFromSpec(TgItem theSpec) {
        return ((TgBoQCtgy) (theSpec.eContainer().eContainer().eContainer())).getLblTx().getP().get(0).getSpan().get(0).getValue();
    }

    private long getTimeInMillis(Timestamp timeStamp) {
        long dateMillis = timeStamp.getDate().toGregorianCalendar().getTimeInMillis();
        long timeMillis = timeStamp.getTime().toGregorianCalendar().getTimeInMillis();
        return dateMillis + timeMillis;
    }

    private enum ActivityType {
        SCHALUNG, STAHL, BETON, UNKNOWN;

        public static ActivityType fromDescription(String descr) {
            if (descr.toLowerCase().contains("beton")) return BETON;
            if (descr.toLowerCase().contains("bewehr")) return STAHL;
            if (descr.toLowerCase().contains("schal")) return SCHALUNG;
            return UNKNOWN;
        }
    }

    private class Event {
        Set<ActivityType> starting = new HashSet<ActivityType>();
        Set<ActivityType> ending = new HashSet<ActivityType>();
        Set<ActivityType> active = new HashSet<ActivityType>();
    }

    private class AppearanceCache {
        // TODO: move to java3dfactory
        Set<Appearance> appearances = new HashSet<Appearance>();

        Appearance getAppearance(float R, float G, float B, float alpha) {
            Color3f newColor = new Color3f(R, G, B);
            Color3f compareTo = new Color3f();
            for (Appearance appearance : appearances) {
                appearance.getMaterial().getDiffuseColor(compareTo);
                float transparency = appearance.getTransparencyAttributes() == null ? 0 : appearance.getTransparencyAttributes().getTransparency();
                if (compareTo.equals(newColor) && transparency == alpha)
                    return appearance;
            }
            Material material = new Material(newColor, new Color3f(0f, 0f, 0f), newColor, newColor, 10f);
            material.setLightingEnable(true);
            Appearance newAppearance = new Appearance();
            newAppearance.setMaterial(material);
            PolygonAttributes pa = new PolygonAttributes();
            pa.setCullFace(PolygonAttributes.CULL_NONE);
            newAppearance.setPolygonAttributes(pa);
            if (alpha > 0)
                newAppearance.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NICEST, alpha));
            appearances.add(newAppearance);
            return newAppearance;
        }
    }
}
