package de.tudresden.cib.vis.configurations;

import cib.mf.schedule.model.activity10.ActivityData;
import cib.mf.schedule.model.activity10.Timestamp;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.multimodel.EMFSchedule10Accessor;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.Change;
import de.tudresden.cib.vis.scene.VisFactory2D;
import de.tudresden.cib.vis.scene.draw2d.Draw2dBuilder;
import de.tudresden.cib.vis.scene.draw2d.Draw2dFactory;
import org.eclipse.draw2d.Panel;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.graphics.Font;

import java.io.IOException;
import java.io.InputStream;

public class TimelineConfiguration extends Configuration<EObject, Draw2dFactory.Draw2dObject, Panel> {

    public TimelineConfiguration(Font font, InputStream inputStream) throws IOException {
        this(new EMFSchedule10Accessor(inputStream), font);
    }

    public TimelineConfiguration(DataAccessor<EObject> data, Font font){
        super(data, new Draw2dFactory(font), new Draw2dBuilder());
    }

    @Override
    public void config() {
        final int scale = 3600 * 500; // scale to half hours
        mapper.addStatistics("earliestStart", new DataAccessor.Folding<EObject, Long>(Long.MAX_VALUE) {
            @Override
            public Long function(Long aggregator, EObject element) {
                return (element instanceof ActivityData) ? Math.min(aggregator, getTimeInMillis(((ActivityData) element).getStart())) : aggregator;
            }
        });
        mapper.addMapping(new PropertyMap<ActivityData, VisFactory2D.Rectangle>() {
            @Override
            protected void configure() {
                graphObject.setLeft((int) (getTimeInMillis(data.getStart())-mapper.getStats("earliestStart").longValue())/scale);
                graphObject.setWidth((int) (getTimeInMillis(data.getEnd())-getTimeInMillis(data.getStart()))/scale);
                graphObject.setTop(index*25);
                graphObject.setHeight(20);
            }
        });
        mapper.addMapping(new PropertyMap<ActivityData, VisFactory2D.Rectangle>(){
            @Override
            protected void configure() {
                final int left = (int) (getTimeInMillis(data.getStart()) - mapper.getStats("earliestStart").longValue())/scale;
                final int maxWidth = (int) (getTimeInMillis(data.getEnd())-getTimeInMillis(data.getStart()))/scale;
                graphObject.setLeft(left);
                graphObject.setTop(index * 25);
                graphObject.setHeight(20);
                int startTime = left/10 + 1;
                int endTime = (left+maxWidth)/10 + 1;
                for(int i=startTime; i<endTime; i++){
                    final int right = i*10;
                    addChange(i*25, new Change<VisFactory2D.Rectangle>() {
                        @Override
                        protected void configure() {
                            graph.setWidth(right-left);
                        }
                    });
                }
                addChange(endTime*25, new Change<VisFactory2D.Rectangle>() {
                    @Override
                    protected void configure() {
                        graph.setWidth(maxWidth);
                    }
                });
            }
        });
    }

    private long getTimeInMillis(Timestamp timeStamp) {
        long dateMillis = timeStamp.getDate().toGregorianCalendar().getTimeInMillis();
        long timeMillis = timeStamp.getTime().toGregorianCalendar().getTimeInMillis();
        return dateMillis + timeMillis;
    }


}
