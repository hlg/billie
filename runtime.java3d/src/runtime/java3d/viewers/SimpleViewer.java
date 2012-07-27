package runtime.java3d.viewers;

import com.sun.j3d.loaders.Loader;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.behaviors.PickMouseBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;
import org.bimserver.plugins.PluginException;
import org.slf4j.LoggerFactory;
import runtime.java3d.UniverseBuilder;
import runtime.java3d.colorTime.TypeAppearance;
import runtime.java3d.views.OrbitalView;

import javax.media.j3d.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.vecmath.Point3d;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Helga Tauscher
 */
public class SimpleViewer extends JFrame {
    static org.slf4j.Logger logger;
    UniverseBuilder universe;
    protected Scene scene;
    private Appearance selectedAppearance;
    private Appearance defaultAppearance;
    Appearance noAppearance;
    Canvas3D canvas;
    Loader loader;
    private Set<Shape3D> selection = new HashSet<Shape3D>();

    public SimpleViewer(Loader loader) {
        logger = LoggerFactory.getLogger(this.getClass());
        selectedAppearance = TypeAppearance.ACTIVATED.getAppearance();
        defaultAppearance = TypeAppearance.DEFAULT.getAppearance();
        noAppearance = TypeAppearance.OFF.getAppearance();
        this.loader = loader;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        setTitle("IFC Visualiser");
        setVisible(true);
    }

    protected void setupViews() {
        setSize(800, 600);
        canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        universe = new UniverseBuilder();
        universe.addView(new OrbitalView(canvas));
        canvas.setVisible(true);
        add(canvas);
        validate();
    }

    public void run(Reader input) throws PluginException, FileNotFoundException {
        setupViews();
        loadFile(input);
        setupBehaviour();
        showScene();
    }

    protected void loadFile(Reader input) throws FileNotFoundException {
        universe.showLoader();
        scene = loader.load(input);
        // Hashtable<IfcRoot, Shape3D> visMap = scene.getNamedObjects();
    }

    protected void showScene() {
        universe.addLights(scene.getSceneGroup());
        universe.showScene(scene.getSceneGroup());
    }

    public Reader chooseFile(String directoryPath) throws FileNotFoundException {
        JFileChooser chooser = (directoryPath != null) ? new JFileChooser(directoryPath) : new JFileChooser();
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().endsWith("ifc");
            }

            @Override
            public String getDescription() {
                return "IFC files";
            }
        };
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(this);
        return (returnVal == JFileChooser.APPROVE_OPTION) ? new FileReader(chooser.getSelectedFile()) : null;
    }

    protected void setupBehaviour() {
        BranchGroup mainScene = scene.getSceneGroup();
        PickMouseBehavior pickMouseBehavior = new PickMouseBehavior(canvas, mainScene, null) {
            @Override
            public void updateScene(int x, int y) {
                pickCanvas.setShapeLocation(x, y);
                PickResult pickInfo = pickCanvas.pickClosest();
                if (pickInfo != null) {
                    Shape3D selected = (Shape3D) pickInfo.getObject();
                    if (selection.contains(selected)) {
                        selection.remove(selected);
                        selected.setAppearance(defaultAppearance);
                    } else {
                        selection.add(selected);
                        selected.setAppearance(selectedAppearance);
                    }
                }
            }
        };
        pickMouseBehavior.setSchedulingBounds(new BoundingSphere(new Point3d(0, 0, 0), Double.MAX_VALUE));
        mainScene.addChild(pickMouseBehavior);
    }


}
