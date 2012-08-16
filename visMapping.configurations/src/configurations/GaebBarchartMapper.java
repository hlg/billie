package configurations;

import cib.lib.gaeb.model.gaeb.TgBoQCtgy;
import cib.lib.gaeb.model.gaeb.TgItem;
import data.multimodel.EMFGaebAccessor;
import org.eclipse.draw2d.Panel;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import visMapping.data.DataAccessor;
import visMapping.mapping.Mapper;
import visMapping.mapping.PropertyMap;
import visMapping.mapping.TargetCreationException;
import visMapping.visualization.VisFactory2D;
import visualization.draw2d.Draw2dBuilder;
import visualization.draw2d.Draw2dFactory;

import java.io.IOException;
import java.math.BigDecimal;

public class GaebBarchartMapper {

    private Mapper<EObject> mapper;

    GaebBarchartMapper(Font font) throws IOException {
        EMFGaebAccessor data = new EMFGaebAccessor(this.getClass().getResourceAsStream("/resources/LV1.X81"));
        Draw2dFactory visFactory = new Draw2dFactory(font);
        Draw2dBuilder visBuilder = new Draw2dBuilder();
        mapper = new Mapper<EObject>(data, visFactory, visBuilder);
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
                graphObject.setHeight(75);
                graphObject.setWidth((int) (data.getUP().intValue() * mapper.getGlobal("widthFactor") * 5));
                graphObject.setLeft(1000);
                graphObject.setTop(index * 100); // TODO: alternative to iterator index ? Layoutmanager, dataacessor sorting parameters
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
                graphObject.setTop(index * 100);
            }
        });
    }

    public Panel execute() throws TargetCreationException {
        return (Panel) mapper.map();
    }

    public static void main(String[] args) throws TargetCreationException, IOException {
        Draw2DViewer viewer = new Draw2DViewer();
        Font big = new Font(viewer.getDefaultFont().getDevice(), "Times New Roman", 50, 0);
        GaebBarchartMapper gaebBarchartMapper = new GaebBarchartMapper(big);
        gaebBarchartMapper.config();
        Panel content = gaebBarchartMapper.execute();
        viewer.setSnapShotParams("D:/test.png", SWT.IMAGE_PNG);
        viewer.showContent(content);
        big.dispose();
    }
}
