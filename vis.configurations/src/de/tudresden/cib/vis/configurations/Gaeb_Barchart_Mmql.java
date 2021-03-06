package de.tudresden.cib.vis.configurations;

import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.mmqlserver.MmqlServerAccessor;
import de.tudresden.cib.vis.mapping.Configuration;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.Change;
import de.tudresden.cib.vis.scene.DefaultEvent;
import de.tudresden.cib.vis.scene.VisFactory2D;

import java.math.BigDecimal;

public class Gaeb_Barchart_Mmql extends Configuration<MmqlServerAccessor.MMQLRow, String> {

    public void config() {
        this.addStatistics("UPmax", new DataAccessor.Folding<MmqlServerAccessor.MMQLRow, BigDecimal>(new BigDecimal(0)) {
            @Override
            public BigDecimal function(BigDecimal aggregator, MmqlServerAccessor.MMQLRow elem) {
                return aggregator.max(BigDecimal.valueOf(Double.valueOf(elem.getCell("UP"))));
            }
        });
        this.addGlobal("widthFactor", new Mapper.PreProcessing<Double>() {
            @Override
            public Double getResult() {
                return 1000. / getStats("UPmax").doubleValue();
            }
        });
        this.addMapping(
                new PropertyMap<MmqlServerAccessor.MMQLRow, VisFactory2D.Rectangle>() {
                    @Override
                    protected void configure() {
                        graphObject.setHeight(15);
                        graphObject.setWidth((int) (Double.valueOf(data.getCell("UP")).intValue() * getGlobal("widthFactor")));
                        graphObject.setLeft(200);
                        graphObject.setTop(index * 20); // TODO: alternative to iterator index ? Layoutmanager, dataacessor sorting parameters
                        addChange(DefaultEvent.CLICK, new Change<VisFactory2D.Rectangle>() {
                            @Override
                            protected void configure() {
                                graph.setColor(150, 0, 0);
                            }
                        });
                        addTrigger(DefaultEvent.CLICK);
                    }
                });
        this.addMapping(new PropertyMap<MmqlServerAccessor.MMQLRow, VisFactory2D.Label>() {
            @Override
            protected void configure() {
                graphObject.setText(data.getCell("ID")+" "+data.getCell("outline"));
                graphObject.setLeft(0);
                graphObject.setTop(index * 20);
            }
        });
    }

}
