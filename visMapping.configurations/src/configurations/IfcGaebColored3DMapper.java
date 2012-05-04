package configurations;

import cib.lib.gaeb.model.gaeb.TgItem;
import cib.mmaa.qto.elementaryModel.Qto.AnsatzType;
import data.DataAccessor;
import data.EMFIfcParser;
import data.MultiModelAccessor;
import mapping.Mapper;
import mapping.PropertyMap;
import mapping.TargetCreationException;
import visualization.VisFactory3D;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;

public class IfcGaebColored3DMapper extends
        MappedBimserverViewer<MultiModelAccessor.LinkedObject<EMFIfcParser.EngineEObject>> {

    public static void main(String[] args) throws TargetCreationException, IOException {
        new IfcGaebColored3DMapper().run();
    }

    void configMapping() {
        mapper.addStatistics("maxTotal", new DataAccessor.Folding<MultiModelAccessor.LinkedObject<EMFIfcParser.EngineEObject>, BigDecimal>(new BigDecimal(0)) {
            @Override
            public BigDecimal function(BigDecimal aggregator, MultiModelAccessor.LinkedObject<EMFIfcParser.EngineEObject> element) {
                return calculateOveralPrice(element.getResolvedLinks()).max(aggregator);
            }
        });
        mapper.addGlobal("halfMaxTotal", new Mapper.PreProcessing<Double>() {
            @Override
            public Double getResult() {
                return mapper.getStats("maxTotal").doubleValue() * 0.5;
            }
        });
        mapper.addMapping(new PropertyMap<MultiModelAccessor.LinkedObject<EMFIfcParser.EngineEObject>, VisFactory3D.Polyeder>() {
            @Override
            protected void configure() {
                EMFIfcParser.Geometry geometry = data.getKeyObject().getGeometry();
                graphObject.setVertizes(geometry.vertizes);
                graphObject.setNormals(geometry.normals);
                float price = calculateOveralPrice(data.getResolvedLinks()).floatValue();
                float halfMaxTotal = mapper.getGlobal("halfMaxTotal").floatValue();
                float red = price <= halfMaxTotal ? price / halfMaxTotal : 1;
                float green = price > halfMaxTotal ? (1 - (price - halfMaxTotal) / halfMaxTotal) : 1;
                graphObject.setColor(red, green, 0);     // 0 1 0 green, 1 1 0 yellow, 1 0 0 red
            }
        });
    }

    @Override
    void loadFile() {
        data = new MultiModelAccessor<EMFIfcParser.EngineEObject>(this.getClass().getResource("/carport"));
    }

    private BigDecimal calculateOveralPrice(Collection<MultiModelAccessor.ResolvedLink> resolvedLinks) {
        BigDecimal price = new BigDecimal(0);
        for (MultiModelAccessor.ResolvedLink link : resolvedLinks) {
            TgItem gaeb = link.getLinkedBoQ().values().iterator().next(); // TODO: implement getFirstLinkedBoQ ...
            AnsatzType qto = link.getLinkedQto().values().iterator().next();
            price = price.add(gaeb.getUP().multiply(BigDecimal.valueOf(qto.getResult())));
        }
        return price;
    }
}
