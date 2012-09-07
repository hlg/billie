package de.tudresden.cib.vis.configurations;

import cib.lib.gaeb.model.gaeb.TgBoQCtgy;
import cib.lib.gaeb.model.gaeb.TgItem;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.multimodel.EMFGaebAccessor;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.VisFactory2D;
import de.tudresden.cib.vis.scene.draw2d.Draw2dBuilder;
import de.tudresden.cib.vis.scene.draw2d.Draw2dFactory;
import org.eclipse.draw2d.Panel;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.graphics.Font;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

public class GaebBarchartConfiguration extends Configuration<EObject, Draw2dFactory.Draw2dObject, Panel> {

    public GaebBarchartConfiguration(Font font, InputStream input) throws IOException {
        super(new EMFGaebAccessor(input), new Draw2dFactory(font), new Draw2dBuilder());
    }

    public GaebBarchartConfiguration(Font font, DataAccessor<EObject> data){
        super(data, new Draw2dFactory(font), new Draw2dBuilder());
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
        mapper.addMapping(new PropertyMap<TgItem, VisFactory2D.Rectangle>() {
            @Override
            protected void configure() {
                graphObject.setHeight(15);
                graphObject.setWidth((int) (data.getUP().intValue() * mapper.getGlobal("widthFactor")));
                graphObject.setLeft(200);
                graphObject.setTop(index * 20); // TODO: alternative to iterator index ? Layoutmanager, dataacessor sorting parameters
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
                labelText.append(data.getDescription().getCompleteText().getOutlineText().getOutlTxt().getTextOutlTxt().get(0).getP().get(0).getSpan().get(0).getValue());
                graphObject.setText(labelText.toString());
                graphObject.setLeft(0);
                graphObject.setTop(index * 20);
            }
        });
    }

}
