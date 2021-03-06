package de.tudresden.cib.vis.runtime.java3d.viewers;

import com.sun.j3d.loaders.Loader;
import com.sun.j3d.utils.universe.SimpleUniverse;
import de.tudresden.cib.vis.data.bimserver.SimplePluginManager;
import de.tudresden.cib.vis.runtime.java3d.UniverseBuilder;
import de.tudresden.cib.vis.runtime.java3d.colorTime.TypeAppearance;
import de.tudresden.cib.vis.runtime.java3d.loaders.BimserverStoreyLoader;
import de.tudresden.cib.vis.runtime.java3d.loaders.MultiLoader;
import de.tudresden.cib.vis.runtime.java3d.views.AxonometricView;
import de.tudresden.cib.vis.runtime.java3d.views.OrbitalView;
import org.bimserver.plugins.PluginException;

import javax.media.j3d.Appearance;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.ViewSpecificGroup;
import java.awt.*;
import java.io.FileNotFoundException;
import java.util.Collection;

/**
 * @author helga
 */
public class ExplodedAxonometrie extends SimpleViewer {

    private Collection<ViewSpecificGroup> storeys;
    private Canvas3D loaderCanvas;

    ExplodedAxonometrie(Loader loader) {
        super(loader);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        setTitle("IFC Visualiser");
        setVisible(true);
    }

    void setupLoaderView() {
        setSize(800, 600);
        universe = new UniverseBuilder();
        loaderCanvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        universe.addView(new OrbitalView(loaderCanvas));
        add(loaderCanvas, BorderLayout.CENTER);
        loaderCanvas.setVisible(true);
        validate();
    }

    @Override
    public void setupViews() {
        remove(loaderCanvas);
        Panel multiviews = new Panel(new GridLayout(storeys.size(), 0));
        multiviews.setSize(800, storeys.size() * 400);
        int row = 0;
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.add(multiviews);
        for (ViewSpecificGroup storey : storeys) {
            Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
            canvas.setSize(800, 400);
            AxonometricView view = new AxonometricView(canvas);
            universe.addView(view);
            view.zoomToExtent(storey, 1);
            storey.addView(view.getView());
            multiviews.add(canvas, row);
            canvas.setVisible(true);
            row++;
        }
        add(scrollPane, BorderLayout.CENTER);
        validate();
    }

    @Override
    public void run(String input) throws FileNotFoundException {
        setupLoaderView();
        loadFile(input);
        setStoreys(((MultiLoader) loader).getSubScenes());  // TODO: remove downcast
        setupViews();
        showScene();
    }

    public static void main(String[] args) throws FileNotFoundException, PluginException {
        SimplePluginManager pluginManager = new SimplePluginManager();
        BimserverStoreyLoader loader = new BimserverStoreyLoader(pluginManager);
        Appearance noCullingAppearance = TypeAppearance.IfcWallImpl.createAppearance();
        PolygonAttributes pgonAttrs = new PolygonAttributes();
        pgonAttrs.setCullFace(PolygonAttributes.CULL_NONE);
        noCullingAppearance.setPolygonAttributes(pgonAttrs);
        loader.setDefaultAppearance(noCullingAppearance);
        ExplodedAxonometrie ifcViewer = new ExplodedAxonometrie(loader);
        ifcViewer.chooseAndRun(args.length > 0 ? args[0] : null, "ifc", false);
    }

    private void setStoreys(Collection<ViewSpecificGroup> storeyNodes) {
        storeys = storeyNodes;
    }

}
