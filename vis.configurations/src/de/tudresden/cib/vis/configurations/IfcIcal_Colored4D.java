package de.tudresden.cib.vis.configurations;

import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser;
import de.tudresden.cib.vis.data.multimodel.LinkedObject;
import de.tudresden.cib.vis.filter.Condition;
import de.tudresden.cib.vis.mapping.Configuration;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.runtime.java3d.colorTime.TypeAppearance;
import de.tudresden.cib.vis.scene.Change;
import net.fortuna.ical4j.model.component.VEvent;
import org.bimserver.models.ifc2x3tc1.IfcProduct;
import org.eclipse.emf.ecore.EObject;

import javax.media.j3d.Appearance;
import javax.media.j3d.Shape3D;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import static de.tudresden.cib.vis.scene.VisFactory3D.Polyeder;

public class IfcIcal_Colored4D<S> extends Configuration<LinkedObject<EMFIfcParser.EngineEObject>, Condition<LinkedObject<EMFIfcParser.EngineEObject>>, S> {

    public IfcIcal_Colored4D(Mapper<LinkedObject<EMFIfcParser.EngineEObject>, Condition<LinkedObject<EMFIfcParser.EngineEObject>>, ?, S> mapper) {
        super(mapper);
    }

    public void config() {
        mapper.addStatistics("earliestStart", new DataAccessor.Folding<LinkedObject<EMFIfcParser.EngineEObject>, Long>(Long.MAX_VALUE) {
            @Override
            public Long function(Long aggregator, LinkedObject<EMFIfcParser.EngineEObject> element) {
                for (LinkedObject.ResolvedLink link : element.getResolvedLinks()) {
                    for (VEvent event : link.getLinkedEvent().values()) {
                        aggregator = Math.min(aggregator, event.getStartDate().getDate().getTime());
                    }
                }
                return aggregator;
            }
        });
        mapper.addStatistics("latestEnd", new DataAccessor.Folding<LinkedObject<EMFIfcParser.EngineEObject>, Long>(Long.MIN_VALUE) {
            @Override
            public Long function(Long aggregator, LinkedObject<EMFIfcParser.EngineEObject> element) {
                for (LinkedObject.ResolvedLink link : element.getResolvedLinks()) {
                    for (VEvent event : link.getLinkedEvent().values()) {
                        aggregator = Math.max(aggregator, event.getEndDate().getDate().getTime());
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
                long earliestStart = mapper.getStats("earliestStart").longValue();
                Map<Long, Integer> activityHistogram = getActivityHistogram(data.getResolvedLinks(), earliestStart);
                if (!activityHistogram.isEmpty() && !activityHistogram.containsKey((long) 0)) addChange(0, reset);
                for (Map.Entry<Long, Integer> histEntry : activityHistogram.entrySet()) {
                    addChange((int) (histEntry.getKey() / scale), histEntry.getValue() > 0 ? activate : (histEntry.getValue() == 0 ? reset : finish));
                }
            }
        };


        mapper.addMapping(new Condition<LinkedObject<EMFIfcParser.EngineEObject>>() {
            @Override
            public boolean matches(LinkedObject<EMFIfcParser.EngineEObject> data) {
                EObject ifcObject = data.getKeyObject().getObject();
                return ifcObject instanceof IfcProduct && ((IfcProduct) ifcObject).isSetRepresentation();
            }
        },anyActiveMapping);


    }

    private Map<Long, Integer> getActivityHistogram(Collection<LinkedObject.ResolvedLink> links, long earliestStart) {
        TreeMap<Long, Integer> result = new TreeMap<Long, Integer>();
        for (LinkedObject.ResolvedLink link : links) {
            for (VEvent activity : link.getLinkedEvent().values()) {
                long start = activity.getStartDate().getDate().getTime() - earliestStart;
                result.put(start, result.containsKey(start) ? result.get(start) + 1 : 1);
                long end = activity.getEndDate().getDate().getTime() - earliestStart;
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

}
