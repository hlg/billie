package de.tudresden.cib.vis.configurations;

import cib.lib.gaeb.model.gaeb.*;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.filter.Condition;
import de.tudresden.cib.vis.mapping.Configuration;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.VisFactory2D;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class Gaeb_Barchart extends Configuration<EObject, Condition<EObject>> {

    private List<String> highlightingIds = Arrays.asList("ILAFBAAA", "ILAFAGFA", "ILAFBFHA", "ILAFKNLA", "ILAFKIEA", "ILAFLDCA", "ILAFDAKA", "ILAFDGBA", "ILAFDLIA", "ILAFEAPA", "ILAFCLDA", "ILFCAGFA", "ILFCBFHA");

    public void config() {
        this.addStatistics("ITmax", new DataAccessor.Folding<EObject, BigDecimal>(new BigDecimal(0)) {
            @Override
                public BigDecimal function(BigDecimal aggregator, EObject elem) {
                return elem instanceof TgItem ? aggregator.max(((TgItem) elem).getIT()) : aggregator;
            }
        });
        this.addGlobal("widthFactor", new Mapper.PreProcessing<Double>() {
            @Override
            public Double getResult() {
                return 1000. / getStats("ITmax").doubleValue();
            }
        });
        this.addMapping(
            new PropertyMap<TgItem, VisFactory2D.Rectangle>() {
                @Override
                protected void configure() {
                    graphObject.setHeight(15);
                    graphObject.setForeground();
                    graphObject.setWidth((int) (data.getIT().intValue() * getGlobal("widthFactor")));
                    graphObject.setLeft(400);
                    graphObject.setTop(index * 20); // TODO: alternative to iterator index ? Layoutmanager, dataacessor sorting parameters
                    if (highlightingIds.contains(data.getID())) graphObject.setColor(150, 0, 0);
                }
            });
        this.addMapping(new PropertyMap<TgItem, VisFactory2D.Label>() {
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
                String label = new GaebHelper(data).safeExtractText(); // TODO inject helper classes for respective data objects in configurations
                labelText.append(label.substring(0,Math.min(label.length()-1,40)));
                labelText.append(" ...");
                graphObject.setText(labelText.toString());
                graphObject.setBackground();
                graphObject.setLeft(0);
                graphObject.setTop(index * 20);
            }

        });
    }


}
