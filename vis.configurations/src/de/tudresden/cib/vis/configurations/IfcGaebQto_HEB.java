package de.tudresden.cib.vis.configurations;

import cib.mf.qto.model.AnsatzType;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.bimserver.EMFIfcHierarchicAcessor;
import de.tudresden.cib.vis.data.multimodel.HierarchicGaebAccessor;
import de.tudresden.cib.vis.data.multimodel.LinkedObject;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.VisFactory2D;
import de.tudresden.cib.vis.scene.draw2d.Draw2dBuilder;
import de.tudresden.cib.vis.scene.draw2d.Draw2dFactory;
import org.eclipse.draw2d.Panel;
import org.eclipse.swt.graphics.Font;

public class IfcGaebQto_HEB extends Configuration<LinkedObject<AnsatzType>, Draw2dFactory.Draw2dObject, Panel> {

    private int ifcScale;
    private int gaebScale;

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
                int smallSize = 4;
                ifcScale = topBigger ? smallSize: (int) (1./ratio * smallSize);
                gaebScale = topBigger ? (int) (ratio * smallSize) : smallSize;
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
