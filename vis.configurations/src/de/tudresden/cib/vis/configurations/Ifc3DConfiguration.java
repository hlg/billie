package de.tudresden.cib.vis.configurations;

import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.VisFactory3D;
import de.tudresden.cib.vis.scene.java3d.Java3dBuilder;
import de.tudresden.cib.vis.scene.java3d.Java3dFactory;
import org.bimserver.models.ifc2x3tc1.IfcBuildingElement;

public class Ifc3DConfiguration {

    private Mapper<EMFIfcParser.EngineEObject> mapper;

    public Ifc3DConfiguration(DataAccessor<EMFIfcParser.EngineEObject> data){
       this.mapper = new Mapper<EMFIfcParser.EngineEObject>(data, new Java3dFactory(), new Java3dBuilder());
    }

    public Ifc3DConfiguration(Mapper<EMFIfcParser.EngineEObject> mapper) {
        this.mapper = mapper;
    }

    public void config() {
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

}
