package configurations;

import cib.lib.bimserverViewer.BimserverViewer;
import cib.lib.bimserverViewer.loaders.IfcScene;
import data.DataAccessor;
import data.EMFIfcAccessor;
import mapping.Mapper;
import mapping.PropertyMap;
import mapping.TargetCreationException;
import org.bimserver.models.ifc2x3.IfcBuildingElement;
import visualization.*;

import javax.media.j3d.BranchGroup;
import java.io.File;

public class IfcGaebColored3DMapper extends BimserverViewer {

    private Mapper mapper;
    private DataAccessor data;

    private void configMapping() {
        VisBuilder builder = new Java3dBuilder();
        VisFactory2D factory = new Java3dFactory();
        mapper = new Mapper(data, factory, builder);
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

    private void loadFile() {
        long size = new File(this.getClass().getResource("/carport2.ifc").getFile()).length();
        data = new EMFIfcAccessor(this.getClass().getResourceAsStream("/carport2.ifc"), size);
    }

    private void executeMapping() throws TargetCreationException {
        scene = new IfcScene();
        scene.setSceneGroup((BranchGroup) mapper.map());
    }

    public static void main(String[] args) throws TargetCreationException {
        IfcGaebColored3DMapper ifcViewer = new IfcGaebColored3DMapper();
        ifcViewer.setupViews();
        ifcViewer.loadFile();
        ifcViewer.configMapping();
        ifcViewer.executeMapping();
        ifcViewer.showScene();
    }
}
