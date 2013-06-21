package de.tudresden.cib.vis.configurations;

import de.tudresden.cib.vis.data.Geometry;
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser;
import de.tudresden.cib.vis.filter.Condition;
import de.tudresden.cib.vis.mapping.Configuration;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.VisFactory3D;
import org.bimserver.models.ifc2x3tc1.IfcBuildingElement;
import org.bimserver.models.ifc2x3tc1.IfcRelContainedInSpatialStructure;
import org.bimserver.models.ifc2x3tc1.IfcRelDecomposes;
import org.eclipse.emf.common.util.EList;

public class Ifc_3D<S> extends Configuration<EMFIfcParser.EngineEObject, Condition<EMFIfcParser.EngineEObject>, S> {

    public Ifc_3D(Mapper<EMFIfcParser.EngineEObject, Condition<EMFIfcParser.EngineEObject>, ?, S> mapper) {
        super(mapper);
    }

    public void config() {
        mapper.addMapping(new Condition<EMFIfcParser.EngineEObject>() {
            @Override
            public boolean matches(EMFIfcParser.EngineEObject data) {
                return data.getObject() instanceof IfcBuildingElement;
            }
        }, new PropertyMap<EMFIfcParser.EngineEObject, VisFactory3D.Polyeder>() {
            @Override
            protected void configure() {
                Geometry geometry = data.getGeometry();
                assert geometry != null;
                graphObject.setColor(220,220,220,230);
                EList<IfcRelContainedInSpatialStructure> containedInStructure = ((IfcBuildingElement) data.getObject()).getContainedInStructure();
                if (!containedInStructure.isEmpty()){
                    EList<IfcRelDecomposes> containedIn = containedInStructure.get(0).getRelatingStructure().getDecomposes();
                    if(!containedIn.isEmpty()) {
                        String ancestorId = containedIn.get(0).getRelatingObject().getGlobalId().getWrappedValue();
                        if(ancestorId.equals("31Ym9dOxj3JhclZuVA4A$p")||ancestorId.equals("3xXriqK0r02OQHfxL3VouW")) graphObject.setColor(150,0,0,0);
                    }
                }
                graphObject.setVertizes(geometry.vertizes);
                graphObject.setNormals(geometry.normals);
                graphObject.setIndizes(geometry.indizes);
            }
        });
    }

}
