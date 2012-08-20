package de.tudresden.cib.vis.runtime.java3d.viewers;

import com.sun.j3d.loaders.Loader;
import com.sun.j3d.utils.universe.SimpleUniverse;
import de.tudresden.cib.vis.data.bimserver.SimplePluginManager;
import de.tudresden.cib.vis.runtime.java3d.UniverseBuilder;
import de.tudresden.cib.vis.runtime.java3d.colorTime.TimeLine;
import de.tudresden.cib.vis.runtime.java3d.colorTime.TypeAppearance;
import de.tudresden.cib.vis.runtime.java3d.loaders.BimserverJava3dLoader;
import de.tudresden.cib.vis.runtime.java3d.views.OrbitalView;
import org.bimserver.models.ifc2x3tc1.IfcRoot;
import org.bimserver.plugins.PluginException;
import org.bimserver.plugins.PluginManager;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Helga Tauscher
 */
public class AnimatedViewer extends SimpleViewer {
    static org.slf4j.Logger logger;

    public AnimatedViewer(Loader loader) {
        super(loader);
    }

    public void setupViews() {
        setSize(1600, 600);
        Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration()) {
            public DrawCommand drawCommand = new DrawCommand() {
                public void execute(J3DGraphics2D canvas2D) {
                    Point2d origin = unproject(new Point3d());
                    int x = (int) origin.getX();
                    int y = (int) origin.getY();
                    canvas2D.setColor(Color.WHITE);
                    Shape sh = new Polygon(new int[]{x + 110, x - 10, x - 10, x + 110}, new int[]{y - 20, y - 20, y + 10, y + 10}, 4);
                    canvas2D.fill(sh);
                    canvas2D.setColor(Color.red);
                    canvas2D.drawString("Rocket science!", x, y);
                    canvas2D.flush(true);
                }
            };

            public Point2d unproject(Point3d point3d) {
                Transform3D temp = new Transform3D();
                this.getVworldToImagePlate(temp);
                temp.transform(point3d);
                Point2d point2d = new Point2d();
                this.getPixelLocationFromImagePlate(point3d, point2d);
                return point2d;
            }

            public void postRender() {
                drawCommand.execute(getGraphics2D());
            }
        };


        universe = new UniverseBuilder();
        universe.addView(new OrbitalView(canvas));

        JPanel multiviews = new JPanel(new GridLayout(0, 2));
        add(multiviews, BorderLayout.CENTER);

        multiviews.add(canvas);
        canvas.setVisible(true);

        Canvas3D canvas2 = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        OrbitalView secondaryView = new OrbitalView(canvas2);
        secondaryView.setProjectionMode(View.PARALLEL_PROJECTION);
        universe.addView(secondaryView);
        multiviews.add(canvas2);
        canvas2.setVisible(true);

        validate();  // TODO: was das?
    }

    interface DrawCommand {
        void execute(J3DGraphics2D canvas);
    }

    @Override
    public void run(Reader input) throws PluginException, FileNotFoundException {
        setupViews();
        loadFile(input);
        setUpAndRunAnimation();
        showScene();
    }

    public static void main(String[] args) throws FileNotFoundException, PluginException {
        PluginManager pm = new SimplePluginManager();
        pm.loadPluginsFromCurrentClassloader();
        AnimatedViewer ifcViewer = new AnimatedViewer(new BimserverJava3dLoader(pm));
        File file = ifcViewer.chooseFile(args.length > 0 ? args[0] : null, "ifc");
        ifcViewer.run(new FileReader(file));
    }

    void setUpAndRunAnimation() {
        final TimeLine timeLine = new TimeLine();
        final Map<IfcRoot, Shape3D> visMap = scene.getNamedObjects();
        int i = 0;
        for (IfcRoot object : visMap.keySet()) {
            timeLine.addToTimeLine(new TimeLine.Activity(++i, i + 3, object));
        }
        final Map<TimeLine.Change, Appearance> colorScheme = new HashMap<TimeLine.Change, Appearance>();
        colorScheme.put(TimeLine.Change.ACTIVATE, TypeAppearance.ACTIVATED.createAppearance());
        colorScheme.put(TimeLine.Change.DEACTIVATE, TypeAppearance.DEACTIVATED.createAppearance());
        final Appearance defaultAppearance = TypeAppearance.INACTIVE.createAppearance();
        TimerTask animation = new TimerTask() {
            int frame = 0;
            int maxFrame = timeLine.getLength() + 1;

            @Override
            public void run() {
                Map<IfcRoot, TimeLine.Change> changes = timeLine.getChanges(frame);
                try {
                    if (changes != null) {
                        for (Map.Entry entry : changes.entrySet()) {
                            final Shape3D shape3D = visMap.get(entry.getKey());
                            final Appearance newColor = colorScheme.get(entry.getValue());
                            shape3D.setAppearance(newColor);
                        }
                    }
                } catch (Exception E) {
                    logger.error("nested: ", E);
                }
                if (frame == maxFrame) {
                    frame = 0;
                    for (Shape3D shape3D : visMap.values()) {
                        shape3D.setAppearance(defaultAppearance);
                    }
                }
                frame++;
            }
        };

        new Timer().schedule(animation, 2000, 1000);
    }

}
