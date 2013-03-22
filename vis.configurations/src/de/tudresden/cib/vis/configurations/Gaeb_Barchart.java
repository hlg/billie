package de.tudresden.cib.vis.configurations;

import cib.lib.gaeb.model.gaeb.TgBoQCtgy;
import cib.lib.gaeb.model.gaeb.TgItem;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.mapping.Configuration;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.VisFactory2D;
import org.eclipse.emf.ecore.EObject;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class Gaeb_Barchart<S> extends Configuration<EObject, S> {

    private List<String> highlightingIds = Arrays.asList("ILAFBAAA", "ILAFAGFA", "ILAFBFHA", "ILAFKNLA", "ILAFKIEA", "ILAFLDCA", "ILAFDAKA", "ILAFDGBA", "ILAFDLIA", "ILAFEAPA", "ILAFCLDA", "ILFCAGFA", "ILFCBFHA");

    public Gaeb_Barchart(Mapper<EObject, ?, S> mapper) {
        super(mapper);
    }

    public void config() {
        mapper.addStatistics("UPmax", new DataAccessor.Folding<EObject, BigDecimal>(new BigDecimal(0)) {
            @Override
            public BigDecimal function(BigDecimal aggregator, EObject elem) {
                return elem instanceof TgItem ? aggregator.max(((TgItem) elem).getUP()) : aggregator;
            }
        });
        mapper.addGlobal("widthFactor", new Mapper.PreProcessing<Double>() {
            @Override
            public Double getResult() {
                return 1000. / mp.getStats("UPmax").doubleValue();
            }
        });
        mapper.addMapping(
                new PropertyMap<TgItem, VisFactory2D.Rectangle>() {
                    @Override
                    protected void configure() {
                        graphObject.setHeight(15);
                        graphObject.setWidth((int) (data.getUP().intValue() * mapper.getGlobal("widthFactor")));
                        graphObject.setLeft(200);
                        graphObject.setTop(index * 20); // TODO: alternative to iterator index ? Layoutmanager, dataacessor sorting parameters
                        if(highlightingIds.contains(data.getID())) graphObject.setColor(150,0,0);
                    }
                });
        mapper.addMapping(new PropertyMap<TgItem, VisFactory2D.Label>() {
            @Override
            protected void configure() {
                EObject container = data.eContainer().eContainer().eContainer();
                StringBuilder labelText = new StringBuilder(data.getRNoPart());
                while (container instanceof TgBoQCtgy) {
                    labelText.insert(0, '.');
                    labelText.insert(0, ((TgBoQCtgy) container).getRNoPart());
                    container = container.eContainer().eContainer();
                }
                labelText.append(" ");
                // labelText.append(data.getDescription().getCompleteText().getOutlineText().getOutlTxt().getTextOutlTxt().get(0).getP().get(0).getSpan().get(0).getValue());
                graphObject.setText(labelText.toString());
                graphObject.setLeft(0);
                graphObject.setTop(index * 20);
            }
        });
    }

}
