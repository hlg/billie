package de.tudresden.cib.vis.configurations;

import cib.mf.schedule.model.activity11.Activity;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.multimodel.LinkedObject;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.Change;
import de.tudresden.cib.vis.scene.VisFactory2D;
import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.Date;
import java.util.Map;

public class QtoSched_GanttAnim<S> extends Configuration<LinkedObject<Activity>, S> {

    private final String[] LM_IDS;
    private final String QTO_ID;
    private final int pxPerDay = 5;
    private final int scale = 1000 * 3600 * 24 / pxPerDay;

    public QtoSched_GanttAnim(Mapper<LinkedObject<Activity>, ?, S> mapper, String[] LM_IDS, String QTO_ID) {
        super(mapper);
        this.LM_IDS = LM_IDS;
        this.QTO_ID = QTO_ID;
    }

    @Override
    public void config() {
        mapper.addStatistics("earliestStart", new DataAccessor.Folding<LinkedObject<Activity>, Long>(Long.MAX_VALUE) {
            @Override
            public Long function(Long aggregator, LinkedObject<Activity> element) {
                return Math.min(aggregator, new ActivityHelper(element.getKeyObject()).getStartDateInMillis());
            }
        });
        mapper.addMapping(new PropertyMap<LinkedObject<Activity>, VisFactory2D.Rectangle>() {
            @Override
            protected void configure() { // base activity
                ActivityHelper activityHelper = new ActivityHelper(data.getKeyObject());
                DateTime start = activityHelper.getStartDate();
                DateTime end = activityHelper.getEndDate();
                DateTime earliestStart = new DateTime(new Date(mapper.getStats("earliestStart").longValue()));
                int startDays = Days.daysBetween(earliestStart, start).getDays();
                int durationDays = Days.daysBetween(start, end).getDays();
                graphObject.setTop(index * 25);
                graphObject.setHeight(20);
                graphObject.setLeft((int) (startDays * pxPerDay));
                graphObject.setWidth((int) (durationDays * pxPerDay));
            }
        });

        mapper.addMapping(new PropertyMap<LinkedObject<Activity>, VisFactory2D.Rectangle>() {
            @Override
            protected void configure() { // progress as should be
                ActivityHelper activityHelper = new ActivityHelper(data.getKeyObject());
                final Map<String, ActivityHelper.SetActualComparison> activityData = activityHelper.collectAmounts(LM_IDS, QTO_ID, data.getResolvedLinks());
                graphObject.setTop(index * 25);
                graphObject.setHeight(20);
                graphObject.setColor(255, 255, 0);
                DateTime start = activityHelper.getStartDate();
                DateTime earliestStart = new DateTime(new Date(mapper.getStats("earliestStart").longValue()));
                int startDays = Days.daysBetween(earliestStart, start).getDays();
                graphObject.setLeft(startDays * pxPerDay);
                addChange(0, new Change<VisFactory2D.Rectangle>() {
                    @Override
                    protected void configure() {
                        graph.setWidth(0);
                    }
                });
                for (int month = 4; month <= 8; month++) {
                    final String lmid = String.format("FM%d", month + 1);
                    // int daysTillActivityEnd = (int) (startDays + activityData.get(lmid).time);
                    int daysTillBillingPeriodEnd = Days.daysBetween(earliestStart, new DateTime(2012, month + 2, 1, 0, 0)).getDays();  // TODO move to globals
                    addChange(daysTillBillingPeriodEnd * 2, new Change<VisFactory2D.Rectangle>() {
                        @Override
                        protected void configure() {
                            int expectedTime = (int) activityData.get(lmid).time;
                            graph.setWidth(expectedTime * pxPerDay);
                        }
                    });
                    /*
                   actual:   100 * activityData.get(lmid).amount / activityData.get(QTO_ID).amount
                   expected: 100 * activityData.get(lmid).time / activityData.get(QTO_ID).time
                    */
                }
            }
        });
        mapper.addMapping(new PropertyMap<LinkedObject<Activity>, VisFactory2D.Rectangle>() {
            @Override
            protected void configure() {  // diff
                ActivityHelper activityHelper = new ActivityHelper(data.getKeyObject());
                final Map<String, ActivityHelper.SetActualComparison> activityData = activityHelper.collectAmounts(LM_IDS, QTO_ID, data.getResolvedLinks());
                graphObject.setHeight(20);
                graphObject.setTop(25 * index);
                DateTime earliestStart = new DateTime(new Date(mapper.getStats("earliestStart").longValue()));
                DateTime start = activityHelper.getStartDate();
                final DateTime end = activityHelper.getEndDate();
                final int startDays = Days.daysBetween(earliestStart, start).getDays();
                final int duration = Days.daysBetween(start, end).getDays();
                for (int month = 4; month <= 8; month++) {
                    final String lmid = String.format("FM%d", month + 1);
                    final int daysTillBillingPeriodEnd = Days.daysBetween(earliestStart, new DateTime(2012, month + 2, 1, 0, 0)).getDays();  // TODO move to globals
                    addChange(0, new Change<VisFactory2D.Rectangle>() {
                        @Override
                        protected void configure() {
                            graph.setWidth(0);
                        }
                    });
                    addChange(daysTillBillingPeriodEnd * 2, new Change<VisFactory2D.Rectangle>() {
                        @Override
                        protected void configure() {
                            double projectedAmount = activityData.get(lmid).amount / activityData.get("FM3").amount * duration;
                            int expectedTime = (int) activityData.get(lmid).time;
                            if (projectedAmount < expectedTime) {
                                graph.setColor(255, 0, 0);
                                graph.setLeft((int) ((startDays + projectedAmount) * pxPerDay));
                                graph.setWidth((int) ((expectedTime - projectedAmount) * pxPerDay));
                            } else {
                                graph.setColor(0, 255, 0);
                                graph.setLeft((startDays + expectedTime) * pxPerDay);
                                graph.setWidth((int) ((projectedAmount - expectedTime) * pxPerDay));
                            }
                        }
                    });
                }
            }
        });
        mapper.addMapping(new PropertyMap<LinkedObject<Activity>, VisFactory2D.Label>() {
            @Override
            protected void configure() { // label
                ActivityHelper activityHelper = new ActivityHelper(data.getKeyObject());
                graphObject.setText(activityHelper.extractActivityDescription());
                graphObject.setLeft((int) ((activityHelper.getStartDateInMillis() - mapper.getStats("earliestStart").longValue()) / scale + 5));
                graphObject.setTop(index * 25);
            }
        });

    }

}
