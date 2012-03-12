package configurations;

import cib.lib.bimserverViewer.colorTime.TimeLine;
import cib.lib.bimserverViewer.colorTime.TypeAppearance;
import cib.mf.schedule.model.activity.Activity;
import data.EMFIfcAccessor;
import data.MultiModelAccessor;
import mapping.PropertyMap;
import mapping.TargetCreationException;
import visualization.VisFactory3D;

import javax.media.j3d.Appearance;
import javax.media.j3d.Shape3D;
import java.util.HashMap;
import java.util.Map;

public class Ifc4DMapper extends MappedBimserverViewer<MultiModelAccessor.LinkedObject<EMFIfcAccessor.EngineEObject>> {
    @Override
    void configMapping() {
        final Map<TimeLine.Change, Appearance> colorScheme = new HashMap<TimeLine.Change, Appearance>(); // global
        colorScheme.put(TimeLine.Change.ACTIVATE, TypeAppearance.ACTIVATED.getAppearance());
        colorScheme.put(TimeLine.Change.DEACTIVATE, TypeAppearance.DEACTIVATED.getAppearance());
        mapper.addMapping(new PropertyMap<MultiModelAccessor.LinkedObject<EMFIfcAccessor.EngineEObject>, VisFactory3D.Polyeder>() {
            @Override
            protected void configure() {
                graphObject.setNormals(data.getKeyObject().getGeometry().normals);
                graphObject.setVertizes(data.getKeyObject().getGeometry().vertizes);
                ((Shape3D) graphObject).setAppearance(TypeAppearance.DEFAULT.getAppearance()); // TODO: generic model
                for (MultiModelAccessor.ResolvedLink link : data.getResolvedLinks()) {
                    for (Activity activity : link.getScheduleObjects().values()) {
                        activity.getActivityData().getStart();
                        colorScheme.get(TimeLine.Change.ACTIVATE);
                        activity.getActivityData().getStart();
                        colorScheme.get(TimeLine.Change.DEACTIVATE);
                    }
                }
            }
        });
    }

    @Override
    void loadFile() {
        data = new MultiModelAccessor<EMFIfcAccessor.EngineEObject>(this.getClass().getResource("/carport"));
    }

    public static void main(String[] args) throws TargetCreationException {
        new Ifc4DMapper().run();
    }
}
