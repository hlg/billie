package configurations;

import data.EMFIfcAccessor;
import mapping.PropertyMap;
import mapping.TargetCreationException;
import org.bimserver.models.ifc2x3.IfcBuildingElement;
import visualization.VisFactory3D;

import java.io.File;

public class Ifc3DMapper extends MappedBimserverViewer<EMFIfcAccessor.EngineEObject> {

    protected void configMapping() {
        mapper.addMapping(new PropertyMap<EMFIfcAccessor.EngineEObject, VisFactory3D.Polyeder>() {
            @Override
            protected boolean condition() {
                return data.getObject() instanceof IfcBuildingElement && ((IfcBuildingElement) data.getObject()).getRepresentation() != null;
            }

            @Override
            protected void configure() {
                EMFIfcAccessor.Geometry geometry = data.getGeometry();
                assert geometry != null;
                graphObject.setVertizes(geometry.vertizes);
                graphObject.setNormals(geometry.normals);
            }
        });
    }

    @Override
    void loadFile() {
        long size = new File(this.getClass().getResource("/carport2.ifc").getFile()).length();
        EMFIfcAccessor data = new EMFIfcAccessor();
        data.setInput(this.getClass().getResourceAsStream("/carport2.ifc"), size);
        this.data = data;
    }

    public static void main(String[] args) throws TargetCreationException {
        Ifc3DMapper ifcViewer = new Ifc3DMapper();
        ifcViewer.run();
    }
}
