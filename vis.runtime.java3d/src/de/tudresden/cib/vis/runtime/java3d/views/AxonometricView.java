package de.tudresden.cib.vis.runtime.java3d.views;

import javax.media.j3d.*;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.awt.*;

/**
 * @author helga
 */
public class AxonometricView implements Camera {
    private View view;

    private Canvas3D canvas;
    private BranchGroup branchGroup;
    private TransformGroup viewTG;

    public AxonometricView(Canvas3D canvas) {
        this.canvas = canvas;
        createViewBranch();
    }

    // TODO: factor out common base implementation?

    private void createViewBranch() {
        branchGroup = new BranchGroup();
        viewTG = new TransformGroup();

        Matrix3d rotZ = new Matrix3d();
        rotZ.rotZ(Math.PI / 4);
        Matrix3d rotX = new Matrix3d();
        rotX.rotX(360 / 32.254 * 2 * Math.PI);
        Matrix3d rot = new Matrix3d();
        rot.mul(rotZ, rotX);
        Transform3D viewTrans = new Transform3D();
        viewTrans.setRotation(rot);
        viewTG.setTransform(viewTrans);
        viewTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        ViewPlatform viewPlatform = new ViewPlatform();
        // viewPlatform.setViewAttachPolicy(View.NOMINAL_SCREEN);
        view = new View();
        view.setBackClipDistance(30000);
        view.addCanvas3D(canvas);
        view.setPhysicalBody(new PhysicalBody());
        view.setPhysicalEnvironment(new PhysicalEnvironment());
        view.attachViewPlatform(viewPlatform);
        view.setProjectionPolicy(View.PARALLEL_PROJECTION);
        view.setScreenScalePolicy(View.SCALE_EXPLICIT);

        viewTG.addChild(viewPlatform);
        branchGroup.addChild(viewTG);
        branchGroup.compile();
    }

    public void zoomToExtent(Group scene, float scale) {
        Bounds bounds = scene.getBounds();
        BoundingSphere boundingSphere = (bounds instanceof BoundingSphere) ? (BoundingSphere) bounds : new BoundingSphere(bounds);
        view.setScreenScale(1. / boundingSphere.getRadius() / 7);
        Transform3D transform = new Transform3D();
        viewTG.getTransform(transform);
        Point3d center = new Point3d();
        boundingSphere.getCenter(center);
        Vector3d centerVector = new Vector3d(center);
        transform.setTranslation(centerVector);
        viewTG.setTransform(transform);
/*
        Transform3D toBeScaled =new Transform3D();
        viewTG.getTransform(toBeScaled);
        toBeScaled.setScale(boundingSphere.getRadius() * 2);
        viewTG.setTransform(toBeScaled);
*/
    }

    public BranchGroup getViewBranch() {
        return branchGroup;
    }


    public View getView() {
        return view;
    }
}
