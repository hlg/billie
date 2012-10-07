package de.tudresden.cib.vis.configurations;

import cib.mf.qto.model.AnsatzType;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.bimserver.EMFIfcHierarchicAcessor;
import de.tudresden.cib.vis.data.multimodel.HierarchicGaebAccessor;
import de.tudresden.cib.vis.data.multimodel.LinkedObject;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.VisFactory2D;
import de.tudresden.cib.vis.scene.draw2d.Draw2dBuilder;
import de.tudresden.cib.vis.scene.draw2d.Draw2dFactory;
import org.eclipse.draw2d.Panel;
import org.eclipse.swt.graphics.Font;

public class IfcGaebQto_HEB extends Configuration<LinkedObject<AnsatzType>, Draw2dFactory.Draw2dObject, Panel> {

    public IfcGaebQto_HEB(DataAccessor<LinkedObject<AnsatzType>> accessor, Font font){
        super(accessor, new Draw2dFactory(font), new Draw2dBuilder());
    }

    @Override
    public void config() {
        mapper.addMapping(new PropertyMap<LinkedObject<AnsatzType>, VisFactory2D.Polyline>() {
            @Override
            protected void configure() {
                LinkedObject.ResolvedLink resolvedLinks = data.getResolvedLinks().iterator().next(); // one and only
                EMFIfcHierarchicAcessor.HierarchicIfc hIfc = resolvedLinks.getLinkedHierarchicIfc().values().iterator().next();
                HierarchicGaebAccessor.HierarchicTgItemBoQCtgy hGaeb = resolvedLinks.getLinkedHierarchicGaeb().values().iterator().next();
                graphObject.addPoint((hIfc.getNodesBefore()+hIfc.getNodeSize()/2)*15 + 50, 50);
                graphObject.addPoint((hGaeb.getNodesBefore()+hGaeb.getNodeSize()/2)*150 + 50, 650);
            }
        });
    }
}
