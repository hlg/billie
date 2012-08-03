package configurations;

import cib.lib.gaeb.model.gaeb.TgItem;
import cib.mf.qto.model.AnsatzType;
import com.sun.j3d.loaders.Loader;
import data.bimserver.EMFIfcParser;
import data.multimodel.MultiModelAccessor;
import org.bimserver.plugins.PluginException;
import runtime.java3d.viewers.SimpleViewer;
import visMapping.data.DataAccessor;
import visMapping.mapping.Mapper;
import visMapping.mapping.PropertyMap;
import visMapping.mapping.TargetCreationException;
import visMapping.visualization.VisFactory3D;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Collection;

public class IfcGaebColored3DMapper extends
        MappedJ3DLoader<MultiModelAccessor.LinkedObject<EMFIfcParser.EngineEObject>> {

    public static void main(String[] args) throws TargetCreationException, IOException, PluginException {
        Loader loader = new IfcGaebColored3DMapper();
        SimpleViewer viewer = new SimpleViewer(loader);
        viewer.run(viewer.chooseFile("D:\\Nutzer\\helga\\div\\", "zip").getCanonicalPath());
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
                graphObject.setIndizes(geometry.indizes);
                float price = calculateOveralPrice(data.getResolvedLinks()).floatValue();
                float halfMaxTotal = mapper.getGlobal("halfMaxTotal").floatValue();
                float red = price <= halfMaxTotal ? price / halfMaxTotal : 1;
                float green = price > halfMaxTotal ? (1 - (price - halfMaxTotal) / halfMaxTotal) : 1;
                graphObject.setColor(red, green, 0);     // 0 1 0 green, 1 1 0 yellow, 1 0 0 red
            }
        });
    }

    @Override
    void load(InputStream inputStream) throws IOException {
        // TODO use zipinputstrem directly instead of folder
        data = new MultiModelAccessor<EMFIfcParser.EngineEObject>(unzip(inputStream));
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
