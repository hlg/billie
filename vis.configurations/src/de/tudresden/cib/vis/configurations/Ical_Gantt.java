package de.tudresden.cib.vis.configurations;

import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.filter.Condition;
import de.tudresden.cib.vis.mapping.Configuration;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.Change;
import de.tudresden.cib.vis.scene.VisFactory2D;
import net.fortuna.ical4j.model.component.VEvent;

public class Ical_Gantt extends Configuration<VEvent, Condition<VEvent>> {

    @Override
    public void config() {
        final int scale = 3600 * 5000; // scale to hours
        this.addStatistics("earliestStart", new DataAccessor.Folding<VEvent, Long>(Long.MAX_VALUE) {
            @Override
            public Long function(Long aggregator, VEvent element) {
                return Math.min(aggregator, element.getStartDate().getDate().getTime());
            }
        });
        this.addMapping(new Condition<VEvent>() {
            @Override
            public boolean matches(VEvent data) {
                return !data.getStartDate().getDate().equals(data.getEndDate().getDate());
            }
        }, new PropertyMap<VEvent, VisFactory2D.Rectangle>() {
            @Override
            protected void configure() {
                graphObject.setLeft((int) ((data.getStartDate().getDate().getTime() - getStats("earliestStart").longValue()) / scale));
                graphObject.setWidth((int) ((data.getEndDate().getDate().getTime() - data.getStartDate().getDate().getTime()) / scale));
                graphObject.setTop(index * 25);
                graphObject.setHeight(20);
                graphObject.setBackground();
            }
        });
        this.addMapping(new Condition<VEvent>() {
            @Override
            public boolean matches(VEvent data) {
                return data.getStartDate().getDate().equals(data.getEndDate().getDate());
            }
        }, new PropertyMap<VEvent, VisFactory2D.Rectangle>() {
            @Override
            protected void configure() {
                graphObject.setLeft((int) ((data.getStartDate().getDate().getTime() - getStats("earliestStart").longValue()) / scale) - 10);
                graphObject.setWidth(20);
                graphObject.setTop(index * 25);
                graphObject.setHeight(20);
                graphObject.setColor(255, 255, 0);
                graphObject.setBackground();
            }
        });
        this.addMapping(new PropertyMap<VEvent, VisFactory2D.Rectangle>() {
            @Override
            protected void configure() {
                final int left = (int) ((data.getStartDate().getDate().getTime() - getStats("earliestStart").longValue()) / scale);
                final int maxWidth = (int) ((data.getEndDate().getDate().getTime() - data.getStartDate().getDate().getTime()) / scale);
                graphObject.setLeft(left);
                graphObject.setTop(index * 25);
                graphObject.setHeight(20);
                graphObject.setColor(255, 0, 0);
                int startTime = left / 10 + 1;
                int endTime = (left + maxWidth) / 10 + 1;
                addChange(0, new Change<VisFactory2D.Rectangle>() {
                    @Override
                    protected void configure() {
                        graph.setWidth(0);
                    }
                });
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
        this.addMapping(new PropertyMap<VEvent, VisFactory2D.Label>() {
            @Override
            protected void configure() {
                graphObject.setLeft((int) ((data.getEndDate().getDate().getTime() - getStats("earliestStart").longValue()) / scale) + 5);
                graphObject.setTop(index * 25 + 5);
                graphObject.setText((data.getSummary() != null ? data.getSummary().getValue() : data.getName()) + (data.getLocation() !=null ? (", " + data.getLocation().getValue()) : ""));
            }
        });
    }

}
