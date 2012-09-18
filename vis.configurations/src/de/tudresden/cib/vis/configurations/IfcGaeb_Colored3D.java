package de.tudresden.cib.vis.configurations;

import cib.lib.gaeb.model.gaeb.TgItem;
import cib.mf.qto.model.AnsatzType;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser;
import de.tudresden.cib.vis.data.multimodel.LinkedObject;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.VisFactory3D;
import de.tudresden.cib.vis.scene.java3d.Java3dBuilder;
import de.tudresden.cib.vis.scene.java3d.Java3dFactory;

import javax.media.j3d.BranchGroup;
import java.math.BigDecimal;
import java.util.Collection;

public class IfcGaeb_Colored3D extends Configuration<LinkedObject<EMFIfcParser.EngineEObject>, Java3dFactory.Java3DGraphObject, BranchGroup> {

    public IfcGaeb_Colored3D(DataAccessor<LinkedObject<EMFIfcParser.EngineEObject>> data){
        super(data, new Java3dFactory(), new Java3dBuilder());
    }

    public IfcGaeb_Colored3D(Mapper<LinkedObject<EMFIfcParser.EngineEObject>, Java3dFactory.Java3DGraphObject, BranchGroup> mapper) {
        super(mapper);
    }

    public void config() {
        mapper.addStatistics("maxTotal", new DataAccessor.Folding<LinkedObject<EMFIfcParser.EngineEObject>, BigDecimal>(new BigDecimal(0)) {
            @Override
            public BigDecimal function(BigDecimal aggregator, LinkedObject<EMFIfcParser.EngineEObject> element) {
                return calculateOveralPrice(element.getResolvedLinks()).max(aggregator);
            }
        });
        mapper.addGlobal("halfMaxTotal", new Mapper.PreProcessing<Double>() {
            @Override
            public Double getResult() {
                return mapper.getStats("maxTotal").doubleValue() * 0.5;
            }
        });
        mapper.addMapping(new PropertyMap<LinkedObject<EMFIfcParser.EngineEObject>, VisFactory3D.Polyeder>() {
            @Override
            protected boolean condition() {
                return data.getKeyObject() != null;  // TODO -> prevent null key objects during link resolution process
            }

            @Override
            protected void configure() {
                EMFIfcParser.Geometry geometry = data.getKeyObject().getGeometry();
                graphObject.setVertizes(geometry.vertizes);
                graphObject.setNormals(geometry.normals);
                graphObject.setIndizes(geometry.indizes);
                int price = calculateOveralPrice(data.getResolvedLinks()).intValue();
                int halfMaxTotal = mapper.getGlobal("halfMaxTotal").intValue();
                int red = price <= halfMaxTotal ? price / halfMaxTotal : 1;
                int green = price > halfMaxTotal ? (1 - (price - halfMaxTotal) / halfMaxTotal) : 1;
                graphObject.setColor(red, green, 0);     // 0 1 0 green, 1 1 0 yellow, 1 0 0 red
            }
        });
    }

    private BigDecimal calculateOveralPrice(Collection<LinkedObject.ResolvedLink> resolvedLinks) {
        BigDecimal price = new BigDecimal(0);
        for (LinkedObject.ResolvedLink link : resolvedLinks) {
            if(!link.getLinkedBoQ().isEmpty()&&!link.getLinkedQto().isEmpty()){
                TgItem gaeb = link.getLinkedBoQ().values().iterator().next(); // TODO: implement getFirstLinkedBoQ ...
                AnsatzType qto = link.getLinkedQto().values().iterator().next();
                price = price.add(gaeb.getUP().multiply(BigDecimal.valueOf(qto.getResult())));
            }
        }
        return price;
    }
}
