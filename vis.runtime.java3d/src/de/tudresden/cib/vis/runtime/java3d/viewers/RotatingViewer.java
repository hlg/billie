package de.tudresden.cib.vis.runtime.java3d.viewers;

import com.sun.j3d.utils.universe.SimpleUniverse;
import de.tudresden.cib.vis.runtime.java3d.UniverseBuilder;
import de.tudresden.cib.vis.runtime.java3d.views.AxonometricView;

import javax.media.j3d.Canvas3D;

public class RotatingViewer extends SimpleViewer {

    @Override
    protected void setupViews() {
        setSize(800, 600);
        canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        universe = new UniverseBuilder();
        AxonometricView view = new AxonometricView(canvas);
        universe.addView(view);
        canvas.setVisible(true);
        add(canvas);
        validate();
    }

}
