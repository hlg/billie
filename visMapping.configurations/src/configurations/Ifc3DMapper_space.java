package configurations;

import data.bimserver.EMFIfcAccessor;
import data.bimserver.EMFIfcParser;
import org.bimserver.models.ifc2x3tc1.IfcSpace;
import org.bimserver.plugins.PluginException;
import runtime.java3d.viewers.SimpleViewer;
import visMapping.mapping.PropertyMap;
import visMapping.mapping.TargetCreationException;
import visMapping.visualization.VisFactory3D;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class Ifc3DMapper_space extends MappedJ3DLoader<EMFIfcParser.EngineEObject> {

    protected void configMapping() {
        mapper.addMapping(new PropertyMap<EMFIfcParser.EngineEObject, VisFactory3D.Polyeder>() {
            @Override
            protected boolean condition() {
                return data.getObject() instanceof IfcSpace
                        && ((IfcSpace) data.getObject()).getRepresentation() != null
                        && ((IfcSpace) data.getObject()).getDecomposes().get(0).getRelatingObject().getName().equals("20.OG")
                    ;
            }

            @Override
            protected void configure() {
                EMFIfcParser.Geometry geometry = data.getGeometry();
                assert geometry != null;
                graphObject.setVertizes(geometry.vertizes);
                graphObject.setNormals(geometry.normals);
                graphObject.setIndizes(geometry.indizes);
            }
        });
    }

    @Override
    void load(InputStream inputStream) throws IOException {
        EMFIfcAccessor data = new EMFIfcAccessor();
        data.setInput(inputStream);
        this.data = data;
    }

    public static void main(String[] args) throws TargetCreationException, IOException, PluginException {
        Ifc3DMapper_space loader = new Ifc3DMapper_space();
        SimpleViewer viewer = new SimpleViewer(loader);
        viewer.run(new FileReader(viewer.chooseFile(".","ifc")));
    }

}
