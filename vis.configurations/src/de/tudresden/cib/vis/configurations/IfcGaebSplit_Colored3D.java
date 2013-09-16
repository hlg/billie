package de.tudresden.cib.vis.configurations;

import cib.lib.gaeb.model.gaeb.TgItem;
import cib.lib.gaeb.model.gaeb.TgQtySplit;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.Geometry;
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser;
import de.tudresden.cib.vis.data.multimodel.LinkedObject;
import de.tudresden.cib.vis.filter.Condition;
import de.tudresden.cib.vis.mapping.Configuration;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.VisFactory3D;

public class IfcGaebSplit_Colored3D<T> extends Configuration<LinkedObject<EMFIfcParser.EngineEObject>, Condition<LinkedObject<EMFIfcParser.EngineEObject>>, T> {

    public String gaebID;

    private IfcGaeb_Colored3D.ColorScale colorScale = new IfcGaeb_Colored3D.ColorScale() {
        @Override
        public double calculateValue(LinkedObject<EMFIfcParser.EngineEObject> data) {
            double value= 0;
            for(LinkedObject.ResolvedLink resolvedLink: data.getResolvedLinks()){
                for (TgQtySplit split : resolvedLink.getAllLinkedQtySplits(gaebID)){
                    value += split.getQty().multiply(((TgItem)split.eContainer()).getUP()).doubleValue();
                }
            }
            return value;
        }
    };

    public IfcGaebSplit_Colored3D(Mapper<LinkedObject<EMFIfcParser.EngineEObject>, Condition<LinkedObject<EMFIfcParser.EngineEObject>>, ?, T> mapper) {
        super(mapper);
    }

    @Override
    public void config() {
        mapper.addStatistics("maxTotal", new DataAccessor.Folding<LinkedObject<EMFIfcParser.EngineEObject>, Double>((double) 0) {
            @Override
            public Double function(Double aggregator, LinkedObject<EMFIfcParser.EngineEObject> element) {
                return Math.max(colorScale.calculateValue(element), aggregator);
            }
        });
        mapper.addGlobal("halfMaxTotal", new Mapper.PreProcessing<Double>() {
            @Override
            public Double getResult() {
                return mapper.getStats("maxTotal").doubleValue() * 0.5;
            }
        });
        mapper.addMapping(
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
                        Double halfMaxTotal = mapper.getGlobal("halfMaxTotal");
                        int red = value <= halfMaxTotal ? (int) (value * 255 / halfMaxTotal) : 255;
                        int green = value > halfMaxTotal ? (int) (255 - (value - halfMaxTotal) * 255 / halfMaxTotal) : 255;
                        graphObject.setColor(red, green, 0, 0);     // 0 1 0 green, 1 1 0 yellow, 1 0 0 red
                    }
                }
        );

    }
}
