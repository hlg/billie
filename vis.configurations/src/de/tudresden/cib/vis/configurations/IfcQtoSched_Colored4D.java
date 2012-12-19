package de.tudresden.cib.vis.configurations;

import cib.mf.schedule.model.activity11.Activity;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser;
import de.tudresden.cib.vis.data.multimodel.LinkedObject;
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
import java.util.Map;

public class IfcQtoSched_Colored4D<S> extends Configuration<LinkedObject<EMFIfcParser.EngineEObject>, S> {

    private String[] lmids;
    private String qtoid;

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
                    Collection<Activity> activities = resolvedLinks.iterator().next().getScheduleObjects().values();
                    if(!activities.isEmpty())
                        return Math.min(aggregator, new ActivityHelper(activities.iterator().next()).getStartDateInMillis());
                }
                return aggregator;
            }
        });
        final Change reset = new Change<VisFactory3D.Polyeder>() {
            @Override
            protected void configure() {
                ((Shape3D) graph).setAppearance(TypeAppearance.INACTIVE.getAppearance());
            }
        };
        PropertyMap<LinkedObject<EMFIfcParser.EngineEObject>, VisFactory3D.Polyeder> specialActiveMapping = new PropertyMap<LinkedObject<EMFIfcParser.EngineEObject>, VisFactory3D.Polyeder>() {
            @Override
            protected boolean condition() {
                return  !data.getResolvedLinks().isEmpty() && !data.getResolvedLinks().iterator().next().getScheduleObjects().isEmpty();
            }

            @Override
            protected void configure() {
                graphObject.setNormals(data.getKeyObject().getGeometry().normals);
                graphObject.setVertizes(data.getKeyObject().getGeometry().vertizes);
                graphObject.setIndizes(data.getKeyObject().getGeometry().indizes);
                addChange(0, reset);

                DateTime earliestStart = new DateTime(new Date(mapper.getStats("earliestStart").longValue()));
                Activity activity = data.getResolvedLinks().iterator().next().getScheduleObjects().values().iterator().next();
                ActivityHelper activityHelper = new ActivityHelper(activity);
                final DateTime end = activityHelper.getEndDate();
                DateTime start = activityHelper.getStartDate();
                int startDays = Days.daysBetween(earliestStart, start).getDays();
                final int duration = Days.daysBetween(start, end).getDays();
                ((Shape3D) graphObject).setAppearance(TypeAppearance.INACTIVE.getAppearance()); // TODO: generic model
                final Map<String, ActivityHelper.SetActualComparison> activityData = activityHelper.collectAmounts(lmids, qtoid, data.getResolvedLinks());
                for (String lm : lmids) {
                    double projectedAmount = activityData.get(lm).amount / activityData.get(qtoid).amount * duration;
                    double expectedTime = (double) activityData.get(lm).time;
                    addChange((int) (startDays + activityData.get(lm).time), getColorChange(expectedTime / projectedAmount));
                }

                // for (lm ...) {
                // addChange(time, colorScale.get(value));

//                 }
            }
        };

        mapper.addMapping(specialActiveMapping);

    }

    private Change<VisFactory3D.Polyeder> getColorChange(double expected_actual) {
        final int r;
        final int g;
        if (expected_actual < 1) {
            g = 255;
            r = (int) (expected_actual * 255);
        } else {
            r = 255;
            g = (int) (1 / expected_actual * 255);
        }
        return new Change<VisFactory3D.Polyeder>() {
            @Override
            protected void configure() {
                graph.setColor(r, g, 0);
            }
        };
    }
}
