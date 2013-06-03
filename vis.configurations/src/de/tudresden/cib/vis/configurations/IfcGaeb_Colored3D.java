package de.tudresden.cib.vis.configurations;

import cib.lib.gaeb.model.gaeb.TgItem;
import cib.mf.qto.model.AnsatzType;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.Geometry;
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser;
import de.tudresden.cib.vis.data.multimodel.LinkedObject;
import de.tudresden.cib.vis.mapping.Configuration;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.Event;
import de.tudresden.cib.vis.scene.VisFactory3D;
import groovy.lang.GroovyShell;
import org.bimserver.models.ifc2x3tc1.*;

import java.math.BigDecimal;
import java.util.Collection;

public class IfcGaeb_Colored3D<S> extends Configuration<LinkedObject<EMFIfcParser.EngineEObject>, S> {

    public String gaebX84Id = "M3"; // "FM1";
    public String gaebX83Id = "M3";
    public boolean absolute = true;

    private GroovyShell groovyShell = new GroovyShell();

    public IfcGaeb_Colored3D(Mapper<LinkedObject<EMFIfcParser.EngineEObject>, ?, S> mapper) {
        super(mapper);
    }

    public void config() {
        mapper.addStatistics("maxTotal", new DataAccessor.Folding<LinkedObject<EMFIfcParser.EngineEObject>, Double>((double) 0) {
            @Override
            public Double function(Double aggregator, LinkedObject<EMFIfcParser.EngineEObject> element) {
                return Math.max(calculateValue(element), aggregator);
            }
        });
        mapper.addGlobal("halfMaxTotal", new Mapper.PreProcessing<Double>() {
            @Override
            public Double getResult() {
                return mapper.getStats("maxTotal").doubleValue()*0.5;
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
                Double value = calculateValue(data);
                Double halfMaxTotal = mapper.getGlobal("halfMaxTotal");
                int red = value <= halfMaxTotal ? (int) (value * 255 / halfMaxTotal) : 255;
                int green = value> halfMaxTotal ? (int) (255 - (value - halfMaxTotal) * 255 / halfMaxTotal) : 255;
                graphObject.setColor(red, green, 0, 0);     // 0 1 0 green, 1 1 0 yellow, 1 0 0 red
            }
        });
    }

    private double calculateValue(LinkedObject<EMFIfcParser.EngineEObject> element) {
        double price = calculateOveralPrice(element.getResolvedLinks()).doubleValue();
        double volume = absolute ? 1 : extractVolume(element.getKeyObject());
        return (volume > 0) ? (price / volume) : 0;
    }

    private double extractVolume(EMFIfcParser.EngineEObject keyObject) {
        for(IfcRelDefines rel : ((IfcProduct) keyObject.getObject()).getIsDefinedBy()){
            if(rel instanceof IfcRelDefinesByProperties && ((IfcRelDefinesByProperties) rel).getRelatingPropertyDefinition() instanceof IfcElementQuantity){
                for(IfcPhysicalQuantity quantity :((IfcElementQuantity) ((IfcRelDefinesByProperties) rel).getRelatingPropertyDefinition()).getQuantities()){
                    if(quantity.getName().equals("GrossVolume") && quantity instanceof IfcQuantityVolume) return ((IfcQuantityVolume) quantity).getVolumeValue();
                }
            }
        }
        return 1;
    }

    private double extractVolume(Collection<LinkedObject.ResolvedLink> resolvedLinks) {
        for(LinkedObject.ResolvedLink resolvedLink: resolvedLinks){
            if("m3".equals(resolvedLink.getLinkedBoQ().get(gaebX83Id).getQU())) return resolvedLink.getLinkedQto().values().iterator().next().getResult();
        }
        return 0;
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

    enum CustomEvent implements Event {
        NEW_COLOR_SCALE
    }
}
