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
import de.tudresden.cib.vis.scene.java3d.Java3dBuilder;
import de.tudresden.cib.vis.scene.java3d.Java3dFactory;
import org.joda.time.DateTime;
import org.joda.time.Days;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Shape3D;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class IfcQtoSched_Colored4D extends Configuration<LinkedObject<EMFIfcParser.EngineEObject>, Java3dFactory.Java3DGraphObject, BranchGroup> {

    private String[] lmids;
    private String qtoid;

    public IfcQtoSched_Colored4D(DataAccessor<LinkedObject<EMFIfcParser.EngineEObject>> data, String[] lmids, String qtoid){
        super(data, new Java3dFactory(), new Java3dBuilder());
        this.lmids = lmids;
        this.qtoid = qtoid;
    }

    public IfcQtoSched_Colored4D(Mapper<LinkedObject<EMFIfcParser.EngineEObject>, Java3dFactory.Java3DGraphObject, BranchGroup> mapper, String[] lmids, String qtoid){
        super(mapper);
        this.lmids = lmids;
        this.qtoid = qtoid;
    }

    @Override
    public void config() {
        PropertyMap<LinkedObject<EMFIfcParser.EngineEObject>, VisFactory3D.Polyeder> specialActiveMapping = new PropertyMap<LinkedObject<EMFIfcParser.EngineEObject>, VisFactory3D.Polyeder>() {
            @Override
            protected void configure() {
                graphObject.setNormals(data.getKeyObject().getGeometry().normals);
                graphObject.setVertizes(data.getKeyObject().getGeometry().vertizes);
                graphObject.setIndizes(data.getKeyObject().getGeometry().indizes);
                DateTime earliestStart = new DateTime(new Date(mapper.getStats("earliestStart").longValue()));
                if (!data.getResolvedLinks().isEmpty()){
                    Map<String, Activity> activities = data.getResolvedLinks().iterator().next().getScheduleObjects();
                    if(!activities.values().isEmpty()){
                        ActivityHelper activityHelper = new ActivityHelper(activities.values().iterator().next());
                        DateTime start = activityHelper.getStartDate();
                        final int startDays = Days.daysBetween(earliestStart, start).getDays();
                        ((Shape3D) graphObject).setAppearance(TypeAppearance.DEFAULT.getAppearance()); // TODO: generic model
                        final Map<String, ActivityHelper.SetActualComparison> activityData = activityHelper.collectAmounts(lmids, qtoid, data.getResolvedLinks());
                        for(String lm: lmids){
                            double expected = activityData.get(lm).time / activityData.get(qtoid).time;   // denominator equals duration
                            double actual = activityData.get(lm).amount / activityData.get(qtoid).amount;
                            addChange((int) (startDays + activityData.get(lm).time), getColorChange(expected / actual));
                        }
                    }
                }

                // for (lm ...) {
                    // addChange(time, colorScale.get(value));

//                 }
            }
        };

        mapper.addMapping(specialActiveMapping);

    }

        private Change<VisFactory3D.Polyeder> getColorChange(double expected_actual){
            final int r;
            final int g;
            if(expected_actual<1){
                g = 255;
                r = (int) (expected_actual * 255);
            } else {
                r = 255;
                g = (int) (1/expected_actual * 255);
            }
            return new Change<VisFactory3D.Polyeder>() {
                @Override
                protected void configure() {
                    graph.setColor(r, g, 0);
                }
            };
        }
}
