package de.tudresden.cib.vis.configurations;

import cib.lib.gaeb.model.gaeb.TgBoQCtgy;
import cib.lib.gaeb.model.gaeb.TgItem;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.Hierarchic;
import de.tudresden.cib.vis.data.multimodel.HierarchicGaebAccessor;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.VisFactory2D;
import de.tudresden.cib.vis.scene.draw2d.Draw2dBuilder;
import de.tudresden.cib.vis.scene.draw2d.Draw2dFactory;
import org.eclipse.draw2d.Panel;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.graphics.Font;

public class Gaeb_Icycle extends Configuration<Hierarchic<EObject>, Draw2dFactory.Draw2dObject, Panel> {

    private int scale;
    private boolean withLabels = false;

    public Gaeb_Icycle(DataAccessor<Hierarchic<EObject>> accessor, Font font){
        super(accessor, new Draw2dFactory(font), new Draw2dBuilder());
    }

    public Gaeb_Icycle(DataAccessor<Hierarchic<EObject>> hierarchicGaeb, Font defaultFont, int scale) {
        this(hierarchicGaeb, defaultFont);
        this.scale = scale;
    }

    @Override
    public void config() {
        mapper.addMapping(new PropertyMap<HierarchicGaebAccessor.HierarchicTgItemBoQCtgy, VisFactory2D.Rectangle>() {
            @Override
            protected void configure() {
                graphObject.setLeft(data.getNodesBefore()*scale);
                graphObject.setWidth(data.getNodeSize()*scale);
                graphObject.setTop(data.getDepth()*25);
                graphObject.setHeight(25);
                if(data.getObject() instanceof TgBoQCtgy) graphObject.setColor(200,200,100);
            }
        });
        if (withLabels) mapper.addMapping(new PropertyMap<HierarchicGaebAccessor.HierarchicTgItemBoQCtgy, VisFactory2D.Label>() {
            @Override
            protected boolean condition() {
                return data.getChildren().isEmpty();
            }
            @Override
            protected void configure() {
                TgItem object = (TgItem) data.getObject();
                graphObject.setLeft(data.getNodesBefore() * scale + 15);
                graphObject.setTop(data.getDepth() * 25 + 40);
                String title = object.getID();
                graphObject.setText(title.length() <=20 ? title : "... " + title.substring(title.length()-20,title.length()-1));
                graphObject.setRotation(90);
            }
        });
        mapper.addMapping(new PropertyMap<HierarchicGaebAccessor.HierarchicTgItemBoQCtgy, VisFactory2D.Label>() {
            @Override
            protected boolean condition() {
                return !data.getChildren().isEmpty();
            }
            @Override
            protected void configure() {
                TgBoQCtgy object = (TgBoQCtgy) data.getObject();
                graphObject.setLeft(data.getNodesBefore() * scale + 5);
                graphObject.setTop(data.getDepth() * 25 + 5);
                String title = object.getID();
                int doubleNodeSize = data.getNodeSize() * 2;
                graphObject.setText(title.length() <= doubleNodeSize ? title : "... " + title.substring(title.length() - doubleNodeSize, title.length() - 1));
            }
        });
    }
}
