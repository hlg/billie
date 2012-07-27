package runtime.java3d.views;

import javax.media.j3d.*;
import javax.vecmath.Matrix3d;

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
        viewPlatform.setViewAttachPolicy(View.NOMINAL_SCREEN);
        view = new View();
        view.setBackClipDistance(30000);
        view.addCanvas3D(canvas);
        view.setPhysicalBody(new PhysicalBody());
        view.setPhysicalEnvironment(new PhysicalEnvironment());
        view.attachViewPlatform(viewPlatform);
        view.setProjectionPolicy(View.PARALLEL_PROJECTION);

        viewTG.addChild(viewPlatform);
        branchGroup.addChild(viewTG);
        branchGroup.compile();
    }

    public void zoomToExtent(Group scene, float scale) {
        Bounds bounds = scene.getBounds();
        BoundingSphere boundingSphere = (bounds instanceof BoundingSphere) ? (BoundingSphere) bounds : new BoundingSphere(bounds);
        Transform3D toBeScaled =new Transform3D();
        viewTG.getTransform(toBeScaled);
        toBeScaled.setScale(boundingSphere.getRadius() * 2);
        viewTG.setTransform(toBeScaled);
    }

    public BranchGroup getViewBranch() {
        return branchGroup;
    }


    public View getView() {
        return view;
    }
}
