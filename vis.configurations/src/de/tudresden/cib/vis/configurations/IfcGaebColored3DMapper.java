package de.tudresden.cib.vis.configurations;

import cib.lib.gaeb.model.gaeb.TgItem;
import cib.mf.qto.model.AnsatzType;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser;
import de.tudresden.cib.vis.data.bimserver.SimplePluginManager;
import de.tudresden.cib.vis.data.multimodel.LinkedObject;
import de.tudresden.cib.vis.data.multimodel.MultiModelAccessor;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.mapping.TargetCreationException;
import de.tudresden.cib.vis.runtime.java3d.viewers.SimpleViewer;
import de.tudresden.cib.vis.scene.VisFactory3D;
import org.bimserver.plugins.PluginException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;

public class IfcGaebColored3DMapper {

    public static void main(String[] args) throws TargetCreationException, IOException, PluginException {
        MappedJ3DLoader<LinkedObject<EMFIfcParser.EngineEObject>> loader = new MappedJ3DLoader<LinkedObject<EMFIfcParser.EngineEObject>>(new MultiModelAccessor<EMFIfcParser.EngineEObject>(new SimplePluginManager()));
        new IfcGaebColored3DMapper().configMapping(loader.getMapper());
        SimpleViewer viewer = new SimpleViewer(loader);
        viewer.run(viewer.chooseFile("D:\\Nutzer\\helga\\div\\", "zip").getCanonicalPath());
    }

    public void configMapping(final Mapper<LinkedObject<EMFIfcParser.EngineEObject>> mapper) {
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
                return data.getKeyObject()!=null;  // TODO -> prevent null key objects during link resolution process
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
