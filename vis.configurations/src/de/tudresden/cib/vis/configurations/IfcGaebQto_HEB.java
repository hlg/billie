package de.tudresden.cib.vis.configurations;

import cib.mf.qto.model.AnsatzType;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.Hierarchic;
import de.tudresden.cib.vis.data.bimserver.EMFIfcHierarchicAcessor;
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
    private static int SMALLSIZE = 4;

    public IfcGaebQto_HEB(DataAccessor<LinkedObject<AnsatzType>> accessor, Font font){
        super(accessor, new Draw2dFactory(font), new Draw2dBuilder());
    }

    @Override
    public void config() {
        mapper.addStatistics("maxIfcPos", new DataAccessor.Folding<LinkedObject<AnsatzType>, Integer>(0) {
            @Override
            public Integer function(Integer number, LinkedObject<AnsatzType> ansatzTypeLinkedObject) {
                LinkedObject.ResolvedLink link = ansatzTypeLinkedObject.getResolvedLinks().iterator().next();
                EMFIfcHierarchicAcessor.HierarchicIfc ifc =link.getLinkedHierarchicIfc().values().iterator().next();
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
        mapper.addMapping(new PropertyMap<LinkedObject<AnsatzType>, VisFactory2D.Polyline>() {
            @Override
            protected void configure() {
                LinkedObject.ResolvedLink resolvedLinks = data.getResolvedLinks().iterator().next(); // one and only
                EMFIfcHierarchicAcessor.HierarchicIfc hIfc = resolvedLinks.getLinkedHierarchicIfc().values().iterator().next();
                HierarchicGaebAccessor.HierarchicTgItemBoQCtgy hGaeb = resolvedLinks.getLinkedHierarchicGaeb().values().iterator().next();
                assert hIfc.getNodeSize() == 1;
                assert hGaeb.getNodeSize() == 1;
                graphObject.addPoint((int) ((.5+hIfc.getNodesBefore())*ifcScale), 0);
                graphObject.addPoint((int) ((.5+hGaeb.getNodesBefore())*gaebScale), 600);


                List<Hierarchic> ifcPath = new ArrayList<Hierarchic>();
                Hierarchic current = data.getResolvedLinks().iterator().next().getLinkedHierarchicIfc().values().iterator().next();
                ifcPath.add(current);
                while(current.getParent()!=null){
                    current = current.getParent();
                    ifcPath.add(current); // TODO: dont add it, if it has no parent (skip the root)
                }
                List<Point> layoutedIfc = new ArrayList<Point>();
                for(Hierarchic pathEntry: ifcPath){
                    layoutedIfc.add(new Point(pathEntry.getNodesBefore() * ifcScale + pathEntry.getNodeSize() * ifcScale / 2, pathEntry.getDepth() * ifcScale * 20));
                }
                Point first = layoutedIfc.get(0);
                Point last = layoutedIfc.get(ifcPath.size()-1);
                int n = layoutedIfc.size();
                double bundlingStrength = 0.85;
                double ax = (1-bundlingStrength) * (last.x - first.x) / (n-1);
                double ay = (1-bundlingStrength) * (last.y - first.y) / (n-1);
                double cx = (1-bundlingStrength) * first.x;
                double cy = (1-bundlingStrength) * first.y;
                /*
                def first = layoutedLink[0]
                def last = layoutedLink[-1]
                def a = [x: (1 - bundlingStrength) * (last.x - first.x) / (n - 1), y: (1 - bundlingStrength) * (last.y - first.y) / (n - 1)]
                def c = [x: (1 - bundlingStrength) * first.x, y: (1 - bundlingStrength) * first.y]
                layoutedLink.eachWithIndex {p, i ->
                        p.x = (bundlingStrength * p.x + c.x + i * a.x) as float
                    p.y = (bundlingStrength * p.y + c.y + i * a.y) as float
                }
                 */
                // Pi' = bundlingStrength*Pi+(1-bundlingStrength)(P0+i/(n-1)*(Plast-P0) = Pi*bundlingStrength + c + i*a
                for (Point p : layoutedIfc) {
                    // graphObject.addPoint((int)(bundlingStrength * p.x + cx + i*ax), (int) (bundlingStrength*p.y() + cy + i*ay));
                    graphObject.addPoint(p.x, p.y);
                }



            }
        });
    }
    public int getIfcScale() {
        return ifcScale;
    }

    public int getGaebScale() {
        return gaebScale;
    }

}
