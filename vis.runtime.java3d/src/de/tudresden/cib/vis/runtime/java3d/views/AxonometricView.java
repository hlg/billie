package de.tudresden.cib.vis.runtime.java3d.views;

import javax.media.j3d.*;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.util.Enumeration;

/**
 * @author helga
 */
public class AxonometricView implements Camera {
    private View view;

    private Canvas3D canvas;
    private BranchGroup branchGroup;
    private TransformGroup viewTG;
    private Point3d center = new Point3d(0,0,0);

    public AxonometricView(Canvas3D canvas) {
        this.canvas = canvas;
        createViewBranch();
    }

    // TODO: factor out common base implementation?

    private void createViewBranch() {
        branchGroup = new BranchGroup();
        viewTG = new TransformGroup();

        Transform3D viewTrans = new Transform3D();
        viewTrans.lookAt(new Point3d(1, 1, 1), center, new Vector3d(0, 0, 1));
        viewTrans.invert();
        viewTG.setTransform(viewTrans);
        viewTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        ViewPlatform viewPlatform = new ViewPlatform();
        // viewPlatform.setViewAttachPolicy(View.NOMINAL_SCREEN);
        view = new View();
        // view.setBackClipDistance(30000);
        view.addCanvas3D(canvas);
        view.setPhysicalBody(new PhysicalBody());
        view.setPhysicalEnvironment(new PhysicalEnvironment());
        view.attachViewPlatform(viewPlatform);
        view.setProjectionPolicy(View.PARALLEL_PROJECTION);
        view.setScreenScalePolicy(View.SCALE_EXPLICIT);
        view.setFrontClipPolicy(View.VIRTUAL_SCREEN);
        view.setBackClipPolicy(View.VIRTUAL_SCREEN);
        view.setWindowResizePolicy(View.VIRTUAL_WORLD);
        view.setDepthBufferFreezeTransparent(false);
        // view.setWindowMovementPolicy(View.RELATIVE_TO_WINDOW);
        viewTG.addChild(viewPlatform);
        branchGroup.addChild(viewTG);
        NESWRotation neswRotation = new NESWRotation(viewTG);
        neswRotation.setSchedulingBounds(new BoundingSphere());
        branchGroup.addChild(neswRotation);
        branchGroup.compile();
    }

    public void zoomToExtent(Group scene, float scale) {
        Bounds bounds = scene.getBounds();
        BoundingSphere boundingSphere = (bounds instanceof BoundingSphere) ? (BoundingSphere) bounds : new BoundingSphere(bounds);
        Transform3D transform = new Transform3D();
        center = new Point3d();
        boundingSphere.getCenter(center);
        Point3d eye = new Point3d(1,1,1);
        eye.add(center);
        transform.lookAt(eye, center, new Vector3d(0, 0, 1));
        transform.invert();
        viewTG.setTransform(transform);
        view.setScreenScale(1. / boundingSphere.getRadius() / 15);
        view.setFrontClipDistance(-5*boundingSphere.getRadius());
    }

    public BranchGroup getViewBranch() {
        return branchGroup;
    }


    public View getView() {
        return view;
    }

    public class  NESWRotation extends Behavior {

        private final TransformGroup targetTg;
        private NESWDirection state = NESWDirection.NE;

        public NESWRotation(TransformGroup tg){
            this.targetTg = tg;
        }

        @Override
        public void initialize() {
            this.wakeupOn(new WakeupOnElapsedTime(2000));
        }

        @Override
        public void processStimulus(Enumeration enumeration) {
            state = state.getNext();
            Point3d eye = new Point3d(state.getPosition());
            eye.add(center);
            Transform3D transform = new Transform3D();
            transform.lookAt(eye, center, new Vector3d(0,0,1));
            transform.invert();
            viewTG.setTransform(transform);
            this.wakeupOn(new WakeupOnElapsedTime(2000));
        }

    }
    enum NESWDirection {
        NE (new Point3d(1, 1, 1)) {
            @Override
            public NESWDirection getNext() {
                return NESWDirection.ES;
            }
        },
        ES (new Point3d(1, -1, 1)) {
            @Override
            public NESWDirection getNext() {
                return NESWDirection.SW;
            }
        },
        SW (new Point3d(-1, -1, 1)) {
            @Override
            public NESWDirection getNext() {
                return NESWDirection.WN;
            }
        },
        WN (new Point3d(-1, 1, 1)) {
            @Override
            public NESWDirection getNext() {
                return NESWDirection.NE;
            }
        };

        private final Point3d position;

        NESWDirection(Point3d point3d){
            this.position = point3d;
        }

        public abstract NESWDirection getNext();

        public Point3d getPosition() {
            return position;
        }

    }
}


