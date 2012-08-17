package de.tudresden.cib.vis.configurations;

import de.tudresden.cib.vis.data.bimserver.EMFIfcAccessor;
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.mapping.TargetCreationException;
import de.tudresden.cib.vis.runtime.java3d.viewers.SimpleViewer;
import de.tudresden.cib.vis.scene.VisFactory3D;
import org.bimserver.models.ifc2x3tc1.IfcBuildingElement;
import org.bimserver.plugins.PluginException;

import java.io.FileReader;
import java.io.IOException;

public class Ifc3DMapper {

    protected void configMapping(MappedJ3DLoader<EMFIfcParser.EngineEObject> loader) {
        loader.addMapping(new PropertyMap<EMFIfcParser.EngineEObject, VisFactory3D.Polyeder>() {
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

    public static void main(String[] args) throws TargetCreationException, IOException, PluginException {
        MappedJ3DLoader<EMFIfcParser.EngineEObject> loader = new MappedJ3DLoader<EMFIfcParser.EngineEObject>(new EMFIfcAccessor());
        new Ifc3DMapper().configMapping(loader);
        SimpleViewer viewer = new SimpleViewer(loader);
        viewer.run(new FileReader(viewer.chooseFile("D:\\Nutzer\\helga\\div\\ifc-modelle", "ifc")));
    }
}
