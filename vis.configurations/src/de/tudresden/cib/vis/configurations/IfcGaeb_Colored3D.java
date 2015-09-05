package de.tudresden.cib.vis.configurations;

import cib.lib.gaeb.model.gaeb.TgItem;
import cib.mf.qto.model.AnsatzType;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.Geometry;
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser;
import de.tudresden.cib.vis.data.multimodel.LinkedObject;
import de.tudresden.cib.vis.filter.Condition;
import de.tudresden.cib.vis.mapping.Configuration;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.VisFactory3D;
import groovy.lang.GroovyClassLoader;
import org.bimserver.models.ifc2x3tc1.*;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;

public class IfcGaeb_Colored3D<S> extends Configuration<LinkedObject<EMFIfcParser.EngineEObject>, Condition<LinkedObject<EMFIfcParser.EngineEObject>>, S> {

    private ColorScale colorScale = new DefaultColorScale();
    public String gaebX84Id = "M3"; // "FM1";
    public String gaebX83Id = "M3";
    public boolean absolute = true;

    public IfcGaeb_Colored3D() {
        super();
    }

    public IfcGaeb_Colored3D(File groovyColorScale) throws IllegalAccessException, InstantiationException, IOException {
        this();
        GroovyClassLoader gcl = new GroovyClassLoader();
        Class clazz = gcl.parseClass(groovyColorScale);
        Object aScript = clazz.newInstance();
        colorScale = (ColorScale) aScript;
    }

    public void config() {
        this.addStatistics("maxTotal", new DataAccessor.Folding<LinkedObject<EMFIfcParser.EngineEObject>, Double>((double) 0) {
            @Override
            public Double function(Double aggregator, LinkedObject<EMFIfcParser.EngineEObject> element) {
                return Math.max(colorScale.calculateValue(element), aggregator);
            }
        });
        this.addGlobal("halfMaxTotal", new Mapper.PreProcessing<Double>() {
            @Override
            public Double getResult() {
                return getStats("maxTotal").doubleValue() * 0.5;
            }
        });
        this.addMapping(
            new Condition<LinkedObject<EMFIfcParser.EngineEObject>>(){
                @Override
                public boolean matches(LinkedObject<EMFIfcParser.EngineEObject> data) {
                    return data.getKeyObject() != null;
                }
            },
            new PropertyMap<LinkedObject<EMFIfcParser.EngineEObject>, VisFactory3D.Polyeder>() {
                @Override
                protected void configure() {
                    Geometry geometry = data.getKeyObject().getGeometry();
                    graphObject.setVertizes(geometry.vertizes);
                    graphObject.setNormals(geometry.normals);
                    graphObject.setIndizes(geometry.indizes);
                    Double value = colorScale.calculateValue(data);
                    Double halfMaxTotal = getGlobal("halfMaxTotal");
                    int red = value <= halfMaxTotal ? (int) (value * 255 / halfMaxTotal) : 255;
                    int green = value > halfMaxTotal ? (int) (255 - (value - halfMaxTotal) * 255 / halfMaxTotal) : 255;
                    graphObject.setColor(red, green, 0, 0);     // 0 1 0 green, 1 1 0 yellow, 1 0 0 red
                }
            }
        );
    }

    public interface ColorScale {
        double calculateValue(LinkedObject<EMFIfcParser.EngineEObject> data);
    }

    private class DefaultColorScale implements ColorScale {

        public double calculateValue(LinkedObject<EMFIfcParser.EngineEObject> element) {
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
        private double extractVolume(Collection<LinkedObject.ResolvedLink> resolvedLinks) {
            for(LinkedObject.ResolvedLink resolvedLink: resolvedLinks){
                if("m3".equals(resolvedLink.getLinkedBoQ().get(gaebX83Id).getQU())) return resolvedLink.getLinkedQto().values().iterator().next().getResult();
            }
            return 0;
        }
    }
}
