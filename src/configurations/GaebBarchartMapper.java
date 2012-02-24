package configurations;

import cib.lib.gaeb.model.gaeb.TgBoQCtgy;
import cib.lib.gaeb.model.gaeb.TgItem;
import data.DataAccessor;
import data.EMFGaebAccessor;
import mapping.Mapper;
import mapping.PropertyMap;
import mapping.TargetCreationException;
import org.eclipse.draw2d.*;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import visualization.Draw2dBuilder;
import visualization.Draw2dFactory;
import visualization.VisFactory2D;

import java.io.IOException;

public class GaebBarchartMapper {

    private Mapper mapper;

    GaebBarchartMapper(Font font) throws IOException {
        DataAccessor data = new EMFGaebAccessor(this.getClass().getResourceAsStream("/LV1.X81"));
        Draw2dFactory visFactory = new Draw2dFactory(font);
        Draw2dBuilder visBuilder = new Draw2dBuilder();
        mapper = new Mapper(data, visFactory, visBuilder);
    }

    public void config(){
        final double factor = 0.3; // TODO: get from some statistical data accessor function
        mapper.addMapping(new PropertyMap<TgItem, VisFactory2D.Rectangle>() {
            @Override
            protected void configure() {
                graphObject.setHeight(15);
                graphObject.setWidth((int)(data.getUP().intValue() * factor));
                graphObject.setLeft(100);
                graphObject.setTop(index * 20); // TODO: alternative to iterator index ? Layoutmanager, dataacessor sorting parameters
            }
        });
        mapper.addMapping(new PropertyMap<TgItem, VisFactory2D.Label>() {
            @Override
            protected void configure() {
                EObject container = data.eContainer().eContainer().eContainer();
                StringBuilder labelText = new StringBuilder(data.getRNoPart());
                while (container instanceof TgBoQCtgy){
                    labelText.insert(0, '.');
                    labelText.insert(0, ((TgBoQCtgy) container).getRNoPart());
                    container = container.eContainer().eContainer();
                }
                graphObject.setText(labelText.toString());
                graphObject.setLeft(0);
                graphObject.setTop(index * 20);
            }
        });
    }
    
    public Panel execute() throws TargetCreationException {
        return (Panel) mapper.map();
    }

    public static void main(String[] args) throws TargetCreationException, IOException {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());

        FigureCanvas canvas = new FigureCanvas(shell);
        LightweightSystem ls = new LightweightSystem(canvas);

        GaebBarchartMapper gaebBarchartMapper = new GaebBarchartMapper(shell.getFont());
        gaebBarchartMapper.config();

        Panel content = gaebBarchartMapper.execute();
        canvas.setContents(content);

        ls.setContents(canvas.getViewport());

        shell.open();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) display.sleep();
        }
        display.dispose();

    }
}
