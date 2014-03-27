package de.tudresden.cib.vis.configurations;

import cib.lib.gaeb.model.gaeb.TgQtySplit;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser;
import de.tudresden.cib.vis.data.multimodel.EMTypes;
import de.tudresden.cib.vis.data.multimodel.LinkedObject;
import de.tudresden.cib.vis.filter.Condition;
import de.tudresden.cib.vis.mapping.Configuration;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.runtime.java3d.colorTime.TypeAppearance;
import de.tudresden.cib.vis.scene.Change;
import de.tudresden.cib.vis.scene.VisFactory3D;
import net.fortuna.ical4j.model.component.VEvent;
import org.joda.time.DateTime;
import org.joda.time.Days;

import javax.media.j3d.Shape3D;
import java.util.*;

public class Mmaa_Progress_Colored4D<S> extends Configuration<LinkedObject<EMFIfcParser.EngineEObject>, Condition<LinkedObject<EMFIfcParser.EngineEObject>>, S> {

    private final String scheduleId;
    private final String reportId;
    private final String qtySplitId;
    public int scale = 3600000*5;

    public Mmaa_Progress_Colored4D(Mapper<LinkedObject<EMFIfcParser.EngineEObject>, Condition<LinkedObject<EMFIfcParser.EngineEObject>>, ?, S> mapper, List<String> ids) {
        super(mapper);
        this.scheduleId = ids.get(1);
        this.reportId = ids.get(2);
        this.qtySplitId = ids.get(3);
    }

    @Override
    public void config() {
        mapper.addStatistics("earliestStart", new DataAccessor.Folding<LinkedObject<EMFIfcParser.EngineEObject>, Long>(Long.MAX_VALUE) {
            @Override
            public Long function(Long aLong, LinkedObject<EMFIfcParser.EngineEObject> engineEObjectLinkedObject) {
                Collection<LinkedObject.ResolvedLink> links = engineEObjectLinkedObject.getResolvedLinks();
                for(LinkedObject.ResolvedLink link: links){
                    for(VEvent event : link.getAllLinkedEvents(scheduleId)){
                        aLong = Math.min(event.getStartDate().getDate().getTime(), aLong);
                    }
                }
                return aLong;
            }
        });
        mapper.addStatistics("latestEnd", new DataAccessor.Folding<LinkedObject<EMFIfcParser.EngineEObject>, Long>(Long.MIN_VALUE) {
            @Override
            public Long function(Long aLong, LinkedObject<EMFIfcParser.EngineEObject> engineEObjectLinkedObject) {
                Collection<LinkedObject.ResolvedLink> links = engineEObjectLinkedObject.getResolvedLinks();
                for(LinkedObject.ResolvedLink link: links){
                    for(VEvent event : link.getAllLinkedEvents(scheduleId)){
                        aLong = Math.max(event.getEndDate().getDate().getTime(), aLong);
                    }
                }
                return aLong;
            }
        });
        final Change reset = new Change<VisFactory3D.Polyeder>() {
            @Override
            protected void configure() {
                graph.setColor(0,0,0,255);
            }
        };
        mapper.addMapping(new Condition<LinkedObject<EMFIfcParser.EngineEObject>>() {
            @Override
            public boolean matches(LinkedObject<EMFIfcParser.EngineEObject> data) {
                return !data.getResolvedLinks().isEmpty();
            }
        }, new PropertyMap<LinkedObject<EMFIfcParser.EngineEObject>, VisFactory3D.Polyeder>(){
            @Override
            protected void configure() {
                graphObject.setNormals(data.getKeyObject().getGeometry().normals);
                graphObject.setVertizes(data.getKeyObject().getGeometry().vertizes);
                graphObject.setIndizes(data.getKeyObject().getGeometry().indizes);
                long earliestStart = mapper.getStats("earliestStart").longValue();
                long latestEnd = mapper.getStats("latestEnd").longValue();
                long duration = latestEnd - earliestStart + scale;
                addChange(0, reset);
                addChange((int) (duration / scale), reset);
                addChange((int) (duration*2 / scale), reset);

                TreeMap<Long, Progress> actualFinish = new TreeMap<Long, Progress>();
                long overallTime= 0;
                Set<VEvent> scheduleEvents = new HashSet<VEvent>();
                for(LinkedObject.ResolvedLink link: data.getResolvedLinks()){    // collect qtys by finishing date
                    double qtySum = 0;
                    Collection<VEvent> reports = link.getAllLinkedEvents(reportId);
                    Collection<VEvent> schedule = link.getAllLinkedEvents(scheduleId);
                    if(reports!= null && !reports.isEmpty()){
                        assert reports.size()<=1;
                        assert schedule.size()<=1;
                        Collection<TgQtySplit> qtySplits = link.getAllLinkedQtySplits(qtySplitId);
                        for(TgQtySplit split : qtySplits) {
                            qtySum += split.getQty().doubleValue();
                        }
                        long reportTime = reports.iterator().next().getStartDate().getDate().getTime();
                        if (!actualFinish.containsKey(reportTime)) actualFinish.put(reportTime, new Progress());
                        actualFinish.get(reportTime).actualQty += qtySum;
                        VEvent scheduleEvent = schedule.iterator().next();
                        long scheduleStart = scheduleEvent.getStartDate().getDate().getTime();
                        long scheduleEnd = scheduleEvent.getEndDate().getDate().getTime();
                        scheduleEvents.add(scheduleEvent);
                        if(scheduleStart<reportTime){
                            actualFinish.get(reportTime).plannedTime += (scheduleEnd>reportTime) ? reportTime - scheduleStart : scheduleEnd - scheduleStart;
                        }
                    }
                }
                for(VEvent scheduleEvent: scheduleEvents){
                    long scheduleStart = scheduleEvent.getStartDate().getDate().getTime();
                    long scheduleEnd = scheduleEvent.getEndDate().getDate().getTime();
                    overallTime += scheduleEnd-scheduleStart;
                }
                double overallQty = 0;
                for(final Map.Entry<Long, Progress> progress : actualFinish.entrySet()){    // accumulate qtys sorted by date
                    overallQty += progress.getValue().actualQty;
                    progress.getValue().actualQty = overallQty;
                }
                for(final Map.Entry<Long, Progress> finished : actualFinish.entrySet()){
                    //overallQty += finished.getValue().actualQty;
                    final int actual = (int) (finished.getValue().actualQty * 255 / overallQty);
                    final int target = (int) (finished.getValue().plannedTime * 255 / overallTime);
                    long offset = finished.getKey() - earliestStart;
                    addChange((int) (offset /scale), new Change<VisFactory3D.Polyeder>() { // 86400000
                        @Override
                        protected void configure() {
                            graph.setColor(target, actual, 0, 255-actual);
                        }
                    });
                    addChange((int) ((duration+offset)/scale), new Change<VisFactory3D.Polyeder>() {
                        @Override
                        protected void configure() {
                            graph.setColor(target, 0, 0, 255-actual);
                        }
                    });
                    addChange((int) ((duration*2+offset)/scale), new Change<VisFactory3D.Polyeder>() {
                        @Override
                        protected void configure() {
                            graph.setColor(0, actual, 0, 255-actual);
                        }
                    });
                }
            }
        });
    }

    class Progress {
        double actualQty = 0;
        long plannedTime = 0;
    }

}
