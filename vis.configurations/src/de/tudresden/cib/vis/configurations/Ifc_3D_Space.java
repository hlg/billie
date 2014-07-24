package de.tudresden.cib.vis.configurations;

import de.tudresden.cib.vis.data.Geometry;
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser;
import de.tudresden.cib.vis.filter.Condition;
import de.tudresden.cib.vis.mapping.Configuration;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.VisFactory3D;
import org.bimserver.models.ifc2x3tc1.IfcSpace;

public class Ifc_3D_Space<S> extends Configuration<EMFIfcParser.EngineEObject, Condition<EMFIfcParser.EngineEObject>, S> {

    public Ifc_3D_Space(Mapper<EMFIfcParser.EngineEObject, Condition<EMFIfcParser.EngineEObject>, ?, S> mapper) {
        super(mapper);
    }

    public void config() {
        mapper.addMapping(new Condition<EMFIfcParser.EngineEObject>() {
            @Override
            public boolean matches(EMFIfcParser.EngineEObject data) {
                return data.getObject() instanceof IfcSpace
                        && ((IfcSpace) data.getObject()).getRepresentation() != null
                        // && ((IfcSpace) data.getObject()).getDecomposes().get(0).getRelatingObject().getName().equals("20.OG")
                ;
            }
        }, new PropertyMap<EMFIfcParser.EngineEObject, VisFactory3D.Polyeder>() {
            @Override
            protected void configure() {
                Geometry geometry = data.getGeometry();
                assert geometry != null;
                graphObject.setVertizes(geometry.vertizes);
                graphObject.setNormals(geometry.normals);
                graphObject.setIndizes(geometry.indizes);
            }
        });
    }


}
