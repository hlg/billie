package de.tudresden.cib.vis.configurations;

import cib.lib.gaeb.model.gaeb.TgItem;
import cib.mf.qto.model.AnsatzType;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.Geometry;
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser;
import de.tudresden.cib.vis.data.multimodel.LinkedObject;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.VisFactory3D;

import java.math.BigDecimal;
import java.util.Collection;

public class IfcGaeb_Colored3D<S> extends Configuration<LinkedObject<EMFIfcParser.EngineEObject>, S> {

    public String gaebX84Id = "FM1";
    public String gaebX83Id = "FM10";

    public IfcGaeb_Colored3D(Mapper<LinkedObject<EMFIfcParser.EngineEObject>, ?, S> mapper) {
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
                Geometry geometry = data.getKeyObject().getGeometry();
                graphObject.setVertizes(geometry.vertizes);
                graphObject.setNormals(geometry.normals);
                graphObject.setIndizes(geometry.indizes);
                int price = calculateOveralPrice(data.getResolvedLinks()).intValue();
                int halfMaxTotal = mapper.getGlobal("halfMaxTotal").intValue();
                int red = price <= halfMaxTotal ? price * 255 / halfMaxTotal : 255;
                int green = price > halfMaxTotal ? (255 - (price - halfMaxTotal) * 255 / halfMaxTotal) : 255;
                graphObject.setColor(red, green, 0, 0);     // 0 1 0 green, 1 1 0 yellow, 1 0 0 red
            }
        });
    }

    private BigDecimal calculateOveralPrice(Collection<LinkedObject.ResolvedLink> resolvedLinks) {
        BigDecimal price = new BigDecimal(0);
        for (LinkedObject.ResolvedLink link : resolvedLinks) {
            if (!link.getLinkedBoQ().isEmpty() && !link.getLinkedQto().isEmpty()) {
                TgItem gaebAngebot = link.getLinkedBoQ().get(gaebX84Id);
                AnsatzType qto = link.getLinkedQto().values().iterator().next();
                price = price.add(gaebAngebot.getUP().multiply(BigDecimal.valueOf(qto.getResult())));
            }
        }
        return price;
    }
}
