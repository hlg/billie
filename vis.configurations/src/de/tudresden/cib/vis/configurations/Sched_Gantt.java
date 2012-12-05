package de.tudresden.cib.vis.configurations;

import cib.mf.schedule.model.activity11.Activity;
import cib.mf.schedule.model.activity11.ActivityData;
import cib.mf.schedule.model.activity11.Timestamp;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.Change;
import de.tudresden.cib.vis.scene.VisFactory2D;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

public class Sched_Gantt<S> extends Configuration<EObject, S> {

    public Sched_Gantt(Mapper<EObject, ?, S> mapper) {
        super(mapper);
    }

    @Override
    public void config() {
        final int scale = 3600 * 5000; // scale to hours
        mapper.addStatistics("earliestStart", new DataAccessor.Folding<EObject, Long>(Long.MAX_VALUE) {
            @Override
            public Long function(Long aggregator, EObject element) {
                return (element instanceof ActivityData) ? Math.min(aggregator, getTimeInMillis(((ActivityData) element).getStart())) : aggregator;
            }
        });
        mapper.addMapping(new PropertyMap<Activity, VisFactory2D.Rectangle>() {
            @Override
            protected boolean condition() {
                return !data.getActivityData().getStart().equals(data.getActivityData().getEnd());
            }

            @Override
            protected void configure() {
                graphObject.setLeft((int) ((getTimeInMillis(data.getActivityData().getStart()) - mapper.getStats("earliestStart").longValue()) / scale));
                graphObject.setWidth((int) ((getTimeInMillis(data.getActivityData().getEnd()) - getTimeInMillis(data.getActivityData().getStart())) / scale));
                graphObject.setTop(index * 25);
                graphObject.setHeight(20);
            }
        });
        mapper.addMapping(new PropertyMap<Activity, VisFactory2D.Rectangle>() {
            @Override
            protected boolean condition() {
                return EcoreUtil.equals(data.getActivityData().getStart(), data.getActivityData().getEnd());
            }

            @Override
            protected void configure() {
                graphObject.setLeft((int) ((getTimeInMillis(data.getActivityData().getStart()) - mapper.getStats("earliestStart").longValue()) / scale) - 10);
                graphObject.setWidth(20);
                graphObject.setTop(index * 25);
                graphObject.setHeight(20);
                graphObject.setColor(255, 255, 0);
            }
        });
        mapper.addMapping(new PropertyMap<Activity, VisFactory2D.Rectangle>() {
            @Override
            protected void configure() {
                final int left = (int) ((getTimeInMillis(data.getActivityData().getStart()) - mapper.getStats("earliestStart").longValue()) / scale);
                final int maxWidth = (int) ((getTimeInMillis(data.getActivityData().getEnd()) - getTimeInMillis(data.getActivityData().getStart())) / scale);
                graphObject.setLeft(left);
                graphObject.setTop(index * 25);
                graphObject.setHeight(20);
                graphObject.setColor(255, 0, 0);
                int startTime = left / 10 + 1;
                int endTime = (left + maxWidth) / 10 + 1;
                for (int i = startTime; i < endTime; i++) {
                    final int right = i * 10;
                    addChange(i * 25, new Change<VisFactory2D.Rectangle>() {
                        @Override
                        protected void configure() {
                            graph.setWidth(right - left);
                        }
                    });
                }
                addChange(endTime * 25, new Change<VisFactory2D.Rectangle>() {
                    @Override
                    protected void configure() {
                        graph.setWidth(maxWidth);
                    }
                });
            }
        });
        mapper.addMapping(new PropertyMap<Activity, VisFactory2D.Label>() {
            @Override
            protected void configure() {
                graphObject.setLeft((int) ((getTimeInMillis(data.getActivityData().getStart()) - mapper.getStats("earliestStart").longValue()) / scale) + 12);
                graphObject.setTop(index * 25 + 2);
                graphObject.setText(new ActivityHelper(data).extractActivityDescription());
            }
        });
    }

    private long getTimeInMillis(Timestamp timeStamp) {
        long dateMillis = timeStamp.getDate().toGregorianCalendar().getTimeInMillis();
        long timeMillis = timeStamp.getTime().toGregorianCalendar().getTimeInMillis();
        return dateMillis + timeMillis;
    }

}
