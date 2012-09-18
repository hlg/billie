package de.tudresden.cib.vis.configurations;

import cib.mf.schedule.model.activity11.Activity;
import cib.mf.schedule.model.activity11.Timestamp;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.multimodel.LinkedObject;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.Change;
import de.tudresden.cib.vis.scene.VisFactory2D;
import de.tudresden.cib.vis.scene.draw2d.Draw2dBuilder;
import de.tudresden.cib.vis.scene.draw2d.Draw2dFactory;
import org.eclipse.draw2d.Panel;
import org.eclipse.swt.graphics.Font;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Duration;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class QtoSched_GanttAnim extends Configuration<LinkedObject<Activity>, Draw2dFactory.Draw2dObject, Panel> {

    private final String[] LM_IDS;
    private final String QTO_ID;
    private final int pxPerDay = 5;
    private final int scale = 1000 * 3600 * 24 / pxPerDay;

    public QtoSched_GanttAnim(DataAccessor<LinkedObject<Activity>> data, String[] lm_ids, String qto_id, Font font) {
        super(data, new Draw2dFactory(font), new Draw2dBuilder());
        this.LM_IDS = lm_ids;
        this.QTO_ID = qto_id;
    }

    @Override
    public void config() {
        mapper.addStatistics("earliestStart", new DataAccessor.Folding<LinkedObject<Activity>, Long>(Long.MAX_VALUE) {
            @Override
            public Long function(Long aggregator, LinkedObject<Activity> element) {
                return Math.min(aggregator, getStartDateInMillis(element));
            }
        });

        mapper.addMapping(new PropertyMap<LinkedObject<Activity>, VisFactory2D.Rectangle>() {
            @Override
            protected void configure() { // base activity
                DateTime start = new DateTime(data.getKeyObject().getActivityData().getStart().getDate().toGregorianCalendar());
                DateTime end = new DateTime(data.getKeyObject().getActivityData().getEnd().getDate().toGregorianCalendar());
                DateTime earliestStart = new DateTime(new Date(mapper.getStats("earliestStart").longValue()));
                int startDays = Days.daysBetween(earliestStart, start).getDays();
                int durationDays = Days.daysBetween(start, end).getDays();
                graphObject.setTop(index * 25);
                graphObject.setHeight(20);
                graphObject.setLeft((int) (startDays * pxPerDay));
                graphObject.setWidth((int) (durationDays * pxPerDay));
            }
        });

        mapper.addMapping(new PropertyMap<LinkedObject<Activity>, VisFactory2D.Rectangle>(){
            @Override
            protected void configure() { // progress as should be
                ActivityHelper activityHelper = new ActivityHelper(data.getKeyObject());
                final Map<String, ActivityHelper.SetActualComparison> activityData = activityHelper.collectAmounts(LM_IDS, QTO_ID, data.getResolvedLinks());
                graphObject.setTop(index * 25);
                graphObject.setHeight(20);
                graphObject.setColor(255,255,0);
                DateTime start = new DateTime(data.getKeyObject().getActivityData().getStart().getDate().toGregorianCalendar());
                DateTime earliestStart = new DateTime(new Date(mapper.getStats("earliestStart").longValue()));
                int startDays = Days.daysBetween(earliestStart, start).getDays();
                final int left = (int) startDays * pxPerDay;
                graphObject.setLeft(left);
                addChange(0, new Change<VisFactory2D.Rectangle>() {
                    @Override
                    protected void configure() {
                        graph.setWidth(0);
                    }
                });
                for (final String lmid : LM_IDS) {
                        addChange((int) (startDays + activityData.get(lmid).time), new Change<VisFactory2D.Rectangle>() {
                            @Override
                            protected void configure() {
                                graph.setWidth((int) (activityData.get(lmid).time * pxPerDay));
                            }
                        });
                        /*
                        actual:   100 * activityData.get(lmid).amount / activityData.get(QTO_ID).amount
                        expected: 100 * activityData.get(lmid).time / activityData.get(QTO_ID).time
                         */
                }
            }
        });
        mapper.addMapping(new PropertyMap<LinkedObject<Activity>, VisFactory2D.Label>() {
            @Override
            protected void configure() {
                ActivityHelper activityHelper = new ActivityHelper(data.getKeyObject());
                graphObject.setText(activityHelper.extractActivityDescription());
                graphObject.setLeft((int)((getStartDateInMillis(data)-mapper.getStats("earliestStart").longValue()) / scale));
                graphObject.setTop(index * 25);
            }
        });

    }

    private long getStartDateInMillis(LinkedObject<Activity> element) {    // TODO move to ActivityHelper
        return getDateInMillis(element.getKeyObject().getActivityData().getStart());
    }

    private long getEndDateInMillis(LinkedObject<Activity> element) {
        return getDateInMillis(element.getKeyObject().getActivityData().getEnd());
    }

    private long getDateInMillis(Timestamp timeStamp) {
        long dateMillis = timeStamp.getDate().toGregorianCalendar().getTimeInMillis();
        long timeMillis = timeStamp.getTime().toGregorianCalendar().getTimeInMillis();
        return dateMillis + timeMillis;
    }

}
