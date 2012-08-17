package de.tudresden.cib.vis.runtime.java3d.views;

import de.tudresden.cib.vis.runtime.java3d.behavior.OrbitBehaviorInterim;

import javax.media.j3d.*;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * @author helga
 */
public class OrbitalView implements Camera {
    private View view;

    private OrbitBehaviorInterim orbitBehaviorInterim;
    private Canvas3D canvas;
    private BranchGroup branchGroup;

    public OrbitalView(Canvas3D canvas) {
        this.canvas = canvas;
        createViewBranch();
    }

    private void createViewBranch() {
        branchGroup = new BranchGroup();  // TODO: to better conform with the scene model return TG directly and let universeBuilder wrap the branchgroup around as needed
        TransformGroup viewTG = new TransformGroup();
        viewTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        ViewPlatform viewPlatform = new ViewPlatform();
        viewPlatform.setViewAttachPolicy(View.NOMINAL_SCREEN);
        view = new View();
        view.setBackClipDistance(300000);
        view.addCanvas3D(canvas);
        view.setPhysicalBody(new PhysicalBody());
        view.setPhysicalEnvironment(new PhysicalEnvironment());
        view.attachViewPlatform(viewPlatform);

        orbitBehaviorInterim = new OrbitBehaviorInterim(canvas, viewTG, OrbitBehaviorInterim.REVERSE_ROTATE | OrbitBehaviorInterim.REVERSE_TRANSLATE | OrbitBehaviorInterim.PROPORTIONAL_ZOOM );
        orbitBehaviorInterim.setVpView(view);
        orbitBehaviorInterim.setTransFactors(3.0, 3.0);
        orbitBehaviorInterim.setSchedulingBounds(new BoundingSphere(new Point3d(), Double.MAX_VALUE));

        viewTG.addChild(viewPlatform);
        viewTG.addChild(orbitBehaviorInterim);
        branchGroup.addChild(viewTG);
        branchGroup.compile();
    }

    private float getViewPlatformDistance(double sceneRadius, float scale) {
        double borderFactor = 1.3 / scale;
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        double ratioFactor = (canvasWidth > canvasHeight) ? (double) canvasWidth / canvasHeight : 1.0;
        return (float) (ratioFactor * borderFactor * sceneRadius / Math.tan(view.getFieldOfView() / 2));
    }

    public void zoomToExtent(Group scene, float scale) {
        Point3d center = new Point3d();
        Bounds bounds = scene.getBounds();
        BoundingSphere boundingSphere = (bounds instanceof BoundingSphere) ? (BoundingSphere) bounds : new BoundingSphere(bounds);
        float halfRadius = (float) (boundingSphere.getRadius() / 2f);
        Point3d eye = new Point3d(halfRadius, getViewPlatformDistance(boundingSphere.getRadius(), scale), halfRadius);
        boundingSphere.getCenter(center);
        eye.add(center);
        orbitBehaviorInterim.setRotationCenter(center);
        orbitBehaviorInterim.setViewingTransform(eye, center, new Vector3d(0, 0, 1), true);
    }

    public void setProjectionMode(int mode){
        orbitBehaviorInterim.setProjectionMode(mode);
    }

    public BranchGroup getViewBranch() {
        return branchGroup;
    }

    public View getView() {
        return view;
    }
}
