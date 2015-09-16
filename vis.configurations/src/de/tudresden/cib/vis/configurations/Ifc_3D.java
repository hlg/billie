package de.tudresden.cib.vis.configurations;

import de.tudresden.cib.vis.data.Geometry;
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser;
import de.tudresden.cib.vis.filter.Condition;
import de.tudresden.cib.vis.mapping.Configuration;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.Change;
import de.tudresden.cib.vis.scene.Event;
import de.tudresden.cib.vis.scene.VisFactory3D;
import org.bimserver.models.ifc2x3tc1.*;
import org.eclipse.emf.common.util.EList;

public class Ifc_3D extends Configuration<EMFIfcParser.EngineEObject, Condition<EMFIfcParser.EngineEObject>> {

    public void config() {
        final Change<VisFactory3D.Polyeder> hide = new Change<VisFactory3D.Polyeder>() {
            @Override
            protected void configure() {
                graph.setColor(100, 100, 100, 255);
            }
        };
        final Change<VisFactory3D.Polyeder> show = new Change<VisFactory3D.Polyeder>() {
            @Override
            protected void configure() {
                graph.setColor(128,128,128,150);
            }
        };
        final Change<VisFactory3D.Polyeder> highlight = new Change<VisFactory3D.Polyeder>() {
            @Override
            protected void configure() {
                graph.setColor(200,0,0,0);
            }
        };
        final Change<VisFactory3D.Polyeder> unhighlight = new Change<VisFactory3D.Polyeder>() {
            @Override
            protected void configure() {
                graph.setColor(128,128,128,150);
            }
        };
        this.addMapping(new Condition<EMFIfcParser.EngineEObject>() {
            @Override
            public boolean matches(EMFIfcParser.EngineEObject data) {
                return data.getObject() instanceof IfcBuildingElement;
            }
        }, new PropertyMap<EMFIfcParser.EngineEObject, VisFactory3D.Polyeder>() {
            @Override
            protected void configure() {
                Geometry geometry = data.getGeometry();
                assert geometry != null;
                if(data.getObject() instanceof IfcSlab || data.getObject() instanceof IfcRoof){
                    // graphObject.setColor(128,128,128,0);
                    graphObject.setColor(200,0,0,0);
                } else {
                    // graphObject.setColor(128,128,128,255);
                    graphObject.setColor(128,128,128,150);
                }
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
                addChange(EventX.HIGHLIGHT, highlight);
                addChange(EventX.UNHIGHLIGHT, unhighlight);
            }
        });
    }

    public enum EventX implements Event {
        HIGHLIGHT, UNHIGHLIGHT
    }
}
