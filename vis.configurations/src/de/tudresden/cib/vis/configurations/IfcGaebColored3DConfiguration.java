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

import java.math.BigDecimal;
import java.util.Collection;

public class IfcGaebColored3DConfiguration {

    private Mapper<LinkedObject<EMFIfcParser.EngineEObject>> mapper;

    public IfcGaebColored3DConfiguration(DataAccessor<LinkedObject<EMFIfcParser.EngineEObject>> data){
        this.mapper = new Mapper<LinkedObject<EMFIfcParser.EngineEObject>>(data, new Java3dFactory(), new Java3dBuilder());
    }

    public IfcGaebColored3DConfiguration(Mapper<LinkedObject<EMFIfcParser.EngineEObject>> mapper) {
        this.mapper = mapper;
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
                float price = calculateOveralPrice(data.getResolvedLinks()).floatValue();
                float halfMaxTotal = mapper.getGlobal("halfMaxTotal").floatValue();
                float red = price <= halfMaxTotal ? price / halfMaxTotal : 1;
                float green = price > halfMaxTotal ? (1 - (price - halfMaxTotal) / halfMaxTotal) : 1;
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
