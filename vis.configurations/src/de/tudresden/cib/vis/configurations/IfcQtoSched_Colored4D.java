package de.tudresden.cib.vis.configurations;

import cib.mf.qto.model.AnsatzType;
import cib.mf.schedule.model.activity11.Activity;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser;
import de.tudresden.cib.vis.data.multimodel.LinkedObject;
import de.tudresden.cib.vis.mapping.Configuration;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.runtime.java3d.colorTime.TypeAppearance;
import de.tudresden.cib.vis.scene.Change;
import de.tudresden.cib.vis.scene.VisFactory3D;
import org.joda.time.DateTime;
import org.joda.time.Days;

import javax.media.j3d.Shape3D;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class IfcQtoSched_Colored4D<S> extends Configuration<LinkedObject<EMFIfcParser.EngineEObject>, S> {

    private String[] lmids;
    private String qtoid;
    private Map<Activity, Map<String, Double>> accumulatedQto = new HashMap<Activity, Map<String, Double>>();

    public IfcQtoSched_Colored4D(Mapper<LinkedObject<EMFIfcParser.EngineEObject>, ?, S> mapper, String[] lmids, String qtoid) {
        super(mapper);
        this.lmids = lmids;
        this.qtoid = qtoid;
    }

    @Override
    public void config() {
        mapper.addStatistics("earliestStart", new DataAccessor.Folding<LinkedObject<EMFIfcParser.EngineEObject>, Long>(Long.MAX_VALUE) {
            @Override
            public Long function(Long aggregator, LinkedObject<EMFIfcParser.EngineEObject> element) {
                Collection<LinkedObject.ResolvedLink> resolvedLinks = element.getResolvedLinks();
                if(!resolvedLinks.isEmpty()){
                    Collection<Activity> activities = resolvedLinks.iterator().next().getScheduleObject().values();
                    if(!activities.isEmpty())
                        return Math.min(aggregator, new ActivityHelper(activities.iterator().next()).getStartDateInMillis());
                }
                return aggregator;
            }
        });
        mapper.addStatistics("accumulatedQto", new DataAccessor.Folding<LinkedObject<EMFIfcParser.EngineEObject>, Double>(0d) {
            @Override
            public Double function(Double number, LinkedObject<EMFIfcParser.EngineEObject> object) {
                if(!object.getResolvedLinks().isEmpty() && !object.getResolvedLinks().iterator().next().getScheduleObject().isEmpty()){
                    Activity theActivity = object.getResolvedLinks().iterator().next().getScheduleObject().values().iterator().next();
                    if(!accumulatedQto.containsKey(theActivity)){
                        accumulatedQto.put(theActivity, new HashMap<String, Double>());
                        for (String lmid: lmids){ accumulatedQto.get(theActivity).put(lmid, 0d);}
                        accumulatedQto.get(theActivity).put(qtoid, 0d);
                    }
                    Map<String, Double> reportEntry = accumulatedQto.get(theActivity);
                    double done = 0d;
                    for(String lmid : lmids){
                        for(LinkedObject.ResolvedLink link : object.getResolvedLinks()){
                            Map <String, AnsatzType> reports = link.getLinkedQto();
                            if(reports.containsKey(lmid)){
                                done += reports.get(lmid).getResult();
                            }
                        }
                        reportEntry.put(lmid, reportEntry.get(lmid)+done);
                    }
                    for(LinkedObject.ResolvedLink link: object.getResolvedLinks()){
                        reportEntry.put(qtoid, reportEntry.get(qtoid)+link.getLinkedQto().get(qtoid).getResult());
                    }
                    return done + number;
                } else  {
                    return number;
                }
            }
        });
        final Change reset = new Change<VisFactory3D.Polyeder>() {
            @Override
            protected void configure() {
                ((Shape3D) graph).setAppearance(TypeAppearance.OFF.getAppearance());
            }
        };
        PropertyMap<LinkedObject<EMFIfcParser.EngineEObject>, VisFactory3D.Polyeder> specialActiveMapping = new PropertyMap<LinkedObject<EMFIfcParser.EngineEObject>, VisFactory3D.Polyeder>() {
            @Override
            protected boolean condition() {
                return  !data.getResolvedLinks().isEmpty() && !data.getResolvedLinks().iterator().next().getScheduleObject().isEmpty();
            }
            @Override
            protected void configure() {
                graphObject.setNormals(data.getKeyObject().getGeometry().normals);
                graphObject.setVertizes(data.getKeyObject().getGeometry().vertizes);
                graphObject.setIndizes(data.getKeyObject().getGeometry().indizes);
                addChange(0, reset);

                DateTime earliestStart = new DateTime(new Date(mapper.getStats("earliestStart").longValue()));
                Activity activity = data.getResolvedLinks().iterator().next().getScheduleObject().values().iterator().next();
                ActivityHelper activityHelper = new ActivityHelper(activity);
                final DateTime end = activityHelper.getEndDate();
                DateTime start = activityHelper.getStartDate();
                int startDays = Days.daysBetween(earliestStart, start).getDays();
                final int duration = Days.daysBetween(start, end).getDays();
                ((Shape3D) graphObject).setAppearance(TypeAppearance.INACTIVE.getAppearance()); // TODO: generic model
                final Map<String, ActivityHelper.SetActualComparison> activityData = activityHelper.collectAmounts(lmids, qtoid, data.getResolvedLinks());
                boolean ready = false;
                for (String lm : lmids) { // asserts lm ids are sorted
                    double projectedAmount = accumulatedQto.get(activity).get(lm) / accumulatedQto.get(activity).get(qtoid)* duration;
                    double expectedTime = (double) activityData.get(lm).time;
                    if(data.getResolvedLinks().iterator().next().getLinkedQto().containsKey(lm)) ready = true;
                    addChange((int) (startDays + activityData.get(lm).time), getColorChange(expectedTime, projectedAmount, duration, ready));
                }
            }
        };

        mapper.addMapping(specialActiveMapping);

    }

    private Change<VisFactory3D.Polyeder> getColorChange(double expected, double actual, int overall, boolean ready) {
        final int r;
        final int g;
        final int alpha;
        if (ready){
            r = expected>=actual ? 255 : (int) (expected/actual* 255);
            g = 255;
            alpha = 0;
        } else {
            r = 255;
            g = 0;
            alpha = expected > actual ? (int) (1-(expected-actual)/(overall-actual)*255) : 255;
        }
        return new Change<VisFactory3D.Polyeder>() {
            @Override
            protected void configure() {
                graph.setColor(r, g, 0, alpha);
            }
        };
    }
}
