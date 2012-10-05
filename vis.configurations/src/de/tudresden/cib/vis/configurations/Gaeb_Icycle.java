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
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.graphics.Font;

import java.util.ArrayList;
import java.util.List;

public class Gaeb_Icycle extends Configuration<Hierarchic<EObject>, Draw2dFactory.Draw2dObject, Panel> {

    public Gaeb_Icycle(DataAccessor<Hierarchic<EObject>> accessor, Font font){
        super(accessor, new Draw2dFactory(font), new Draw2dBuilder());
    }

    @Override
    public void config() {
        mapper.addMapping(new PropertyMap<HierarchicGaebAccessor.HierarchicTgItemBoQCtgy, VisFactory2D.Rectangle>() {
            @Override
            protected void configure() {
                graphObject.setLeft(data.getNodesBefore()*15);
                graphObject.setWidth(data.getNodeSize()*15);
                graphObject.setTop(data.getDepth()*25);
                graphObject.setHeight(25);
                if(data.getObject() instanceof TgBoQCtgy) graphObject.setColor(200,200,100);
            }
        });
        mapper.addMapping(new PropertyMap<HierarchicGaebAccessor.HierarchicTgItemBoQCtgy, VisFactory2D.Label>() {
            @Override
            protected boolean condition() {
                return data.getChildren().isEmpty();
            }
            @Override
            protected void configure() {
                TgItem object = (TgItem) data.getObject();
                graphObject.setLeft(data.getNodesBefore() * 15 + 15);
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
                graphObject.setLeft(data.getNodesBefore() * 15 + 5);
                graphObject.setTop(data.getDepth() * 25 + 5);
                String title = object.getID();
                int doubleNodeSize = data.getNodeSize() * 2;
                graphObject.setText(title.length() <= doubleNodeSize ? title : "... " + title.substring(title.length() - doubleNodeSize, title.length() - 1));
            }
        });
        mapper.addMapping(new PropertyMap<HierarchicGaebAccessor.HierarchicTgItemBoQCtgy, VisFactory2D.Polyline>() {
            @Override
            protected boolean condition() {
                return data.getChildren().isEmpty();
            }

            @Override
            protected void configure() {
                List<Hierarchic<? extends EObject>> path = new ArrayList<Hierarchic<? extends EObject>>();
                Hierarchic<? extends EObject> current = data;
                path.add(current);
                while(current.getParent()!=null){
                    current = current.getParent();
                    path.add(current);
                }
                List<Point> layouted = new ArrayList<Point>();
                int scale = 15;
                for(Hierarchic<? extends EObject> pathEntry: path){
                    layouted.add(new Point(pathEntry.getNodesBefore()*scale+pathEntry.getNodeSize()*scale/2, 1200 - pathEntry.getDepth()*scale*20));
                }
                Point first = layouted.get(0);
                Point last = layouted.get(path.size()-1);
                int n = layouted.size();
                double b = 0.85;
                double ax = (1-b) * (last.x - first.x) / (n-1);
                double ay = (1-b) * (last.y - first.y) / (n-1);
                double cx = (1-b) * first.x;
                double cy = (1-b) * first.y;
                /*
                def first = layoutedLink[0]
                def last = layoutedLink[-1]
                def a = [x: (1 - b) * (last.x - first.x) / (n - 1), y: (1 - b) * (last.y - first.y) / (n - 1)]
                def c = [x: (1 - b) * first.x, y: (1 - b) * first.y]
                layoutedLink.eachWithIndex {p, i ->
                        p.x = (b * p.x + c.x + i * a.x) as float
                    p.y = (b * p.y + c.y + i * a.y) as float
                }
                 */
                // Pi' = b*Pi+(1-b)(P0+i/(n-1)*(Plast-P0) = Pi*b + c + i*a
                for(int i=0; i<layouted.size(); i++){
                    Point p = layouted.get(i);
                    // graphObject.addPoint((int)(b * p.x + cx + i*ax), (int) (b*p.y() + cy + i*ay));
                    graphObject.addPoint(p.x, p.y);
                }
            }
        });
    }
}
