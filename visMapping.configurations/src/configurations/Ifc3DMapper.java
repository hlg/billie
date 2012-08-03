package configurations;

import data.bimserver.EMFIfcAccessor;
import data.bimserver.EMFIfcParser;
import org.bimserver.models.ifc2x3tc1.IfcBuildingElement;
import org.bimserver.plugins.PluginException;
import runtime.java3d.viewers.SimpleViewer;
import visMapping.mapping.PropertyMap;
import visMapping.mapping.TargetCreationException;
import visMapping.visualization.VisFactory3D;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class Ifc3DMapper extends MappedJ3DLoader<EMFIfcParser.EngineEObject> {

    protected void configMapping() {
        mapper.addMapping(new PropertyMap<EMFIfcParser.EngineEObject, VisFactory3D.Polyeder>() {
            @Override
            protected boolean condition() {
                return data.getObject() instanceof IfcBuildingElement
                       /* && ((IfcBuildingElement) data.getObject()).getRepresentation() != null
                        && !((IfcBuildingElement) data.getObject()).getContainedInStructure().isEmpty()
                        && ((IfcBuildingElement) data.getObject()).getContainedInStructure().get(0).getRelatingStructure().getName().equals("E14")*/
                        ;
            }

            @Override
            protected void configure() {
                EMFIfcParser.Geometry geometry = data.getGeometry();
                assert geometry != null;
                /* EList<IfcRelContainedInSpatialStructure> containedInStructure = ((IfcBuildingElement) data.getObject()).getContainedInStructure();
                if (!containedInStructure.isEmpty() && containedInStructure.get(0).getRelatingStructure().getName().equals("E14"))
                    graphObject.setColor(1, 0, 0); */
                graphObject.setVertizes(geometry.vertizes);
                graphObject.setNormals(geometry.normals);
                graphObject.setIndizes(geometry.indizes);
            }
        });
    }

    @Override
    void load(InputStream in) throws IOException {
        EMFIfcAccessor data = new EMFIfcAccessor();
        data.setInput(in);
        this.data = data;
    }

    public static void main(String[] args) throws TargetCreationException, IOException, PluginException {
        Ifc3DMapper loader = new Ifc3DMapper();
        SimpleViewer viewer = new SimpleViewer(loader);
        viewer.run(new FileReader(viewer.chooseFile("D:\\Nutzer\\helga\\div\\ifc-modelle", "ifc")));
    }
}
