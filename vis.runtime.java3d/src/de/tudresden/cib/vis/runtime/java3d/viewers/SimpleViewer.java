package de.tudresden.cib.vis.runtime.java3d.viewers;

import com.sun.j3d.loaders.Loader;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.behaviors.PickMouseBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;
import de.tudresden.cib.vis.runtime.java3d.UniverseBuilder;
import de.tudresden.cib.vis.runtime.java3d.colorTime.TypeAppearance;
import de.tudresden.cib.vis.runtime.java3d.views.AxonometricView;
import de.tudresden.cib.vis.runtime.java3d.views.OrbitalView;
import org.slf4j.LoggerFactory;

import javax.media.j3d.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.vecmath.Point3d;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
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

    private boolean axonometric = false;

    public SimpleViewer() {
        logger = LoggerFactory.getLogger(this.getClass());
        selectedAppearance = TypeAppearance.IfcSpaceImpl.getAppearance();
        defaultAppearance = TypeAppearance.DEFAULT.getAppearance();
        noAppearance = TypeAppearance.OFF.getAppearance();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        setTitle("Simple Viewer für Java3D scenes");
        setVisible(true);
    }

    public void setPickingEnabled(boolean pickingEnabled) {
        this.pickingEnabled = pickingEnabled;
    }

    public void setAxonometric(boolean axonometric) {
        this.axonometric = axonometric;
    }

    private boolean pickingEnabled = true;

    public SimpleViewer(Loader loader) {
        this();
        this.loader = loader;
    }

    protected void setupViews() {
        setSize(800, 600);
        canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        universe = new UniverseBuilder();
        universe.addView(axonometric ? new AxonometricView(canvas) : new OrbitalView(canvas));
        canvas.setVisible(true);
        add(canvas);
        validate();
    }

    public void run(String path) throws FileNotFoundException {
        setupViews();
        loadFile(path);
        if (pickingEnabled) setupBehaviour(scene.getSceneGroup());
        showScene();
    }

    public void run(BranchGroup scene) {
        setupViews();
        if (pickingEnabled) setupBehaviour(scene);
        universe.addLights(scene);
        universe.showScene(scene);
    }

    void loadFile(String path) throws FileNotFoundException {
        universe.showLoader();
        scene = loader.load(path);
    }

    protected void showScene() {
        universe.addLights(scene.getSceneGroup());
        universe.showScene(scene.getSceneGroup());
    }

    public void chooseAndRun(String fileOrDirectoryPath, final String fileType, boolean directorySelection) throws FileNotFoundException {
        File fileOrDirectory = new File(fileOrDirectoryPath);
        if (fileOrDirectory.isDirectory()) {
            File choice = chooseFile(fileOrDirectoryPath, fileType, directorySelection);
            if (choice != null) {
                run(choice.getPath());
            } else {
                dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            }

        } else {
            run(fileOrDirectoryPath);
        }
    }

    public File chooseFile(String directoryPath, final String fileType, final boolean directorySelection) {
        if(directoryPath!=null){
            File directory = new File(directoryPath);
            if (directory.isFile()) return directory;
        }
        JFileChooser chooser = (directoryPath != null) ? new JFileChooser(directoryPath) : new JFileChooser();
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File f) {
                return (directorySelection && f.isDirectory()) || f.getName().endsWith(fileType);
            }

            @Override
            public String getDescription() {
                return fileType + " files";
            }
        };
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int returnVal = chooser.showOpenDialog(this);
        return (returnVal == JFileChooser.APPROVE_OPTION) ? chooser.getSelectedFile() : null;
    }

    protected void setupBehaviour(BranchGroup mainScene) {
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
