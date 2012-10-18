package de.tudresden.cib.vis.configurations;

import cib.lib.gaeb.model.gaeb.TgBoQCtgy;
import cib.mf.qto.model.AnsatzType;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.Hierarchic;
import de.tudresden.cib.vis.data.multimodel.HierarchicGaebAccessor;
import de.tudresden.cib.vis.data.multimodel.LinkedObject;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.VisFactory2D;
import de.tudresden.cib.vis.scene.draw2d.Draw2dBuilder;
import de.tudresden.cib.vis.scene.draw2d.Draw2dFactory;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Font;

import java.util.ArrayList;
import java.util.List;

public class IfcGaebQto_HEB extends Configuration<LinkedObject<AnsatzType>, Draw2dFactory.Draw2dObject, Panel> {

    private int ifcScale;
    private int gaebScale;
    private static int SMALLSIZE = 20;
    private static double BUNDLING = 0.4;
    private static boolean SKIP_LAST_LEVEL = true;

    public IfcGaebQto_HEB(DataAccessor<LinkedObject<AnsatzType>> accessor, Font font){
        super(accessor, new Draw2dFactory(font), new Draw2dBuilder());
    }

    @Override
    public void config() {
        mapper.addStatistics("maxIfcPos", new DataAccessor.Folding<LinkedObject<AnsatzType>, Integer>(0) {
            @Override
            public Integer function(Integer number, LinkedObject<AnsatzType> ansatzTypeLinkedObject) {
                LinkedObject.ResolvedLink link = ansatzTypeLinkedObject.getResolvedLinks().iterator().next();
                Hierarchic ifc =link.getLinkedHierarchicIfc().values().iterator().next();
                if (SKIP_LAST_LEVEL) ifc = ifc.getParent();
                int ifcPos = ifc.getNodesBefore() + ifc.getNodeSize() / 2;
                return Math.max(number, ifcPos);
            }
        });
        mapper.addStatistics("maxGaebPos", new DataAccessor.Folding<LinkedObject<AnsatzType>, Integer>(0) {
            @Override
            public Integer function(Integer integer, LinkedObject<AnsatzType> ansatzTypeLinkedObject) {
                LinkedObject.ResolvedLink link = ansatzTypeLinkedObject.getResolvedLinks().iterator().next();
                HierarchicGaebAccessor.HierarchicTgItemBoQCtgy gaeb = link.getLinkedHierarchicGaeb().values().iterator().next();
                int gaebPos = gaeb.getNodesBefore() + gaeb.getNodeSize() / 2;
                return Math.max(integer, gaebPos);
            }
        });
        mapper.addGlobal("icycleRatio", new Mapper.PreProcessing<Double>() {
            @Override
            public Double getResult() {
                double ratio = Double.valueOf((Integer) mp.getStats("maxIfcPos")) / (Integer) mp.getStats("maxGaebPos");
                boolean topBigger = ratio > 1;
                ifcScale = topBigger ? SMALLSIZE : (int) (1./ratio * SMALLSIZE);
                gaebScale = topBigger ? (int) (ratio * SMALLSIZE) : SMALLSIZE;
                return ratio;
            }
        });
        mapper.addMapping(new PropertyMap<LinkedObject<AnsatzType>, VisFactory2D.Bezier>() {
            @Override
            protected void configure() {
                LinkedObject.ResolvedLink resolvedLinks = data.getResolvedLinks().iterator().next(); // one and only
                Hierarchic currentIfc = resolvedLinks.getLinkedHierarchicIfc().values().iterator().next();
                Hierarchic currentGaeb = resolvedLinks.getLinkedHierarchicGaeb().values().iterator().next();
                assert currentGaeb.getNodeSize() == 1;
                assert currentIfc.getNodeSize() == 1;
                List<Point> layouted = new ArrayList<Point>();
                if (!SKIP_LAST_LEVEL) layouted.add(pointFor(currentIfc, ifcScale, -100, 450));
                while(currentIfc.getParent()!=null){
                    currentIfc = currentIfc.getParent();
                    if(currentIfc.getParent()!=null) layouted.add(pointFor(currentIfc, ifcScale, -100, 450)); // skip root
                }
                List<Hierarchic> gaebPath = new ArrayList<Hierarchic>();
                gaebPath.add(currentGaeb);
                while(currentGaeb.getParent()!=null){
                    currentGaeb = currentGaeb.getParent();
                    if(currentGaeb.getParent()!=null) gaebPath.add(currentGaeb); //skip root
                }
                for(int i = gaebPath.size()-1; i>= 0; i--){
                    layouted.add(pointFor(gaebPath.get(i), gaebScale, 100, 450));
                }

                Point first = layouted.get(0);
                int n = layouted.size();
                Point last = layouted.get(n - 1);
                double ax = (1- BUNDLING) * (last.x - first.x) / (n-1);
                double ay = (1- BUNDLING) * (last.y - first.y) / (n-1);
                double cx = (1- BUNDLING) * first.x;
                double cy = (1- BUNDLING) * first.y;
                for (int i = 0; i<layouted.size(); i++) {
                    Point p = layouted.get(i);
                    graphObject.addPoint((int)(BUNDLING * p.x + cx + i*ax), (int) (BUNDLING *p.y() + cy + i*ay));
                }
                TgBoQCtgy parent = ((TgBoQCtgy)data.getResolvedLinks().iterator().next().getLinkedHierarchicGaeb().values().iterator().next().getParent().getObject());
                if (parent.getID().equals("ILAGFNBA")) graphObject.setColor(220,100,0);
                // data.getResolvedLinks().iterator().next().getScheduleObjects().values().iterator().next().getActivityData().getEnd();
            }
        });
    }

    private Point pointFor(Hierarchic current, int scale, int distance, int offset) {
        return new Point(current.getNodesBefore() * scale + current.getNodeSize() * scale / 2, current.getDepth() * distance + offset);
    }

    public int getIfcScale() {
        return ifcScale;
    }

    public int getGaebScale() {
        return gaebScale;
    }

}
