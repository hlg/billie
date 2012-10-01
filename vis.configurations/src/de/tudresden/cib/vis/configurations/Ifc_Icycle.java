package de.tudresden.cib.vis.configurations;

import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.Hierarchic;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.VisFactory2D;
import de.tudresden.cib.vis.scene.draw2d.Draw2dBuilder;
import de.tudresden.cib.vis.scene.draw2d.Draw2dFactory;
import org.bimserver.emf.IdEObject;
import org.bimserver.models.ifc2x3tc1.IfcElement;
import org.bimserver.models.ifc2x3tc1.IfcSpatialStructureElement;
import org.eclipse.draw2d.Panel;
import org.eclipse.swt.graphics.Font;

public class Ifc_Icycle extends Configuration<Hierarchic<IdEObject>, Draw2dFactory.Draw2dObject, Panel> {

    public Ifc_Icycle(DataAccessor<Hierarchic<IdEObject>> accessor, Font font){
        super(accessor, new Draw2dFactory(font), new Draw2dBuilder());
    }

    @Override
    public void config() {
        mapper.addMapping(new PropertyMap<Hierarchic<IdEObject>, VisFactory2D.Rectangle>() {
            @Override
            protected void configure() {
                graphObject.setLeft(data.getNodesBefore()*15);
                graphObject.setWidth(data.getNodeSize()*15);
                graphObject.setTop(data.getDepth()*25);
                graphObject.setHeight(25);
                if(data.getObject() instanceof IfcSpatialStructureElement) graphObject.setColor(200,200,100);
            }
        });
        mapper.addMapping(new PropertyMap<Hierarchic<IdEObject>, VisFactory2D.Label>() {
            @Override
            protected boolean condition() {
                return data.getChildren().isEmpty();
            }
            @Override
            protected void configure() {
                IfcElement object = (IfcElement) data.getObject();
                graphObject.setLeft(data.getNodesBefore() * 15);
                graphObject.setTop(data.getDepth() * 25 + 150);
                String title = object.getName();
                graphObject.setText(title.length() <=20 ? title : "... " + title.substring(title.length()-20,title.length()-1));
                graphObject.setRotation(-90);
            }
        });
        mapper.addMapping(new PropertyMap<Hierarchic<IdEObject>, VisFactory2D.Label>() {
            @Override
            protected boolean condition() {
                return !data.getChildren().isEmpty();
            }
            @Override
            protected void configure() {
                IfcSpatialStructureElement object = (IfcSpatialStructureElement ) data.getObject();
                graphObject.setLeft(data.getNodesBefore() * 15 + 5);
                graphObject.setTop(data.getDepth() * 25 + 5);
                String title = object.getName();
                int doubleNodeSize = data.getNodeSize() * 2;
                graphObject.setText(title.length() <= doubleNodeSize ? title : "... " + title.substring(title.length() - doubleNodeSize, title.length() - 1));
            }
        });
    }
}
