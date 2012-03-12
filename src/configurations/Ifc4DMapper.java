package configurations;

import data.EMFIfcAccessor;
import data.MultiModelAccessor;
import mapping.PropertyMap;
import mapping.TargetCreationException;
import visualization.VisFactory3D;

public class Ifc4DMapper extends MappedBimserverViewer<MultiModelAccessor.LinkedObject<EMFIfcAccessor.EngineEObject>> {
    @Override
    void configMapping() {
        mapper.addMapping(new PropertyMap<MultiModelAccessor.LinkedObject<EMFIfcAccessor.EngineEObject>, VisFactory3D.Polyeder>() {
            @Override
            protected void configure() {
                graphObject.setNormals(data.getKeyObject().getGeometry().normals);
                graphObject.setVertizes(data.getKeyObject().getGeometry().vertizes);
                graphObject.setColor(.5f, .5f, .5f);
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
