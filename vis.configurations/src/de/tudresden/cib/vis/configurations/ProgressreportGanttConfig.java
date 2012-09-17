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

import java.util.Map;

public class ProgressreportGanttConfig extends Configuration<LinkedObject<Activity>, Draw2dFactory.Draw2dObject, Panel> {

    private final String[] LM_IDS;
    private final String QTO_ID;
    private final int pxPerDay = 5;
    private final int scale = 1000 * 3600 * 24 / pxPerDay;

    public ProgressreportGanttConfig(DataAccessor<LinkedObject<Activity>> data, String[] lm_ids, String qto_id, Font font) {
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
            protected void configure() {
                int left = (int) ((getStartDateInMillis(data) - mapper.getStats("earliestStart").longValue()) / scale);
                int width = (int) ((getEndDateInMillis(data) - getStartDateInMillis(data)) / scale);
                graphObject.setTop(index * 25);
                graphObject.setHeight(20);
                graphObject.setLeft(left);
                graphObject.setWidth(width);
            }
        });

        mapper.addMapping(new PropertyMap<LinkedObject<Activity>, VisFactory2D.Rectangle>(){
            @Override
            protected void configure() {
                ActivityHelper activityHelper = new ActivityHelper(data.getKeyObject());
                Map<String, ActivityHelper.SetActualComparison> activityData = activityHelper.collectAmounts(LM_IDS, QTO_ID, data.getResolvedLinks());
                graphObject.setTop(index * 25);
                graphObject.setHeight(20);
                graphObject.setColor(255,255,0);
                final int left = (int) (( getStartDateInMillis(data) - mapper.getStats("earliestStart").longValue()) / scale);
                graphObject.setLeft(left);
                addChange(0, new Change<VisFactory2D.Rectangle>() {
                    @Override
                    protected void configure() {
                        graph.setWidth(0);
                    }
                });
                for (final Map.Entry<String, ActivityHelper.SetActualComparison> entry : activityData.entrySet()) {
                    if (!entry.getKey().equals(QTO_ID) && entry.getValue().amount > 0) {
                        addChange((int) (entry.getValue().time), new Change<VisFactory2D.Rectangle>() {
                            @Override
                            protected void configure() {
                                graph.setWidth((int) (entry.getValue().time * pxPerDay - left));
                            }
                        });
                        /*
                        text.append("\t");
                        text.append(entry.getKey());
                        text.append(": actual ");
                        text.append((int) (100 * entry.getValue().amount / activityData.get(QTO_ID).amount));
                        text.append("%, expected ");
                        text.append((int) (100 * entry.getValue().time / activityData.get(QTO_ID).time));
                        text.append("%\n");
                         */
                    }
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
