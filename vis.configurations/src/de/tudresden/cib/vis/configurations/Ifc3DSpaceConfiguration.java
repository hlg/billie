package de.tudresden.cib.vis.configurations;

import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.VisFactory3D;
import de.tudresden.cib.vis.scene.java3d.Java3dBuilder;
import de.tudresden.cib.vis.scene.java3d.Java3dFactory;
import org.bimserver.models.ifc2x3tc1.IfcSpace;

public class Ifc3DSpaceConfiguration {

    private Mapper<EMFIfcParser.EngineEObject> mapper;

    public Ifc3DSpaceConfiguration(DataAccessor<EMFIfcParser.EngineEObject> data){
        this.mapper = new Mapper<EMFIfcParser.EngineEObject>(data, new Java3dFactory(), new Java3dBuilder());
    }

    public Ifc3DSpaceConfiguration(Mapper<EMFIfcParser.EngineEObject> mapper) {
        this.mapper = mapper;
    }

    public void config() {
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


}
