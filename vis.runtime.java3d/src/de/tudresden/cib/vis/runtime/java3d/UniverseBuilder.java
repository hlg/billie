package de.tudresden.cib.vis.runtime.java3d;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.universe.Viewer;
import de.tudresden.cib.vis.runtime.java3d.views.Camera;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Helga Tauscher
 */
public class UniverseBuilder {
    private Set<Camera> views = new HashSet<Camera>();
    private Switch sceneSwitch;
    private Locale locale;

    public UniverseBuilder(){
        initSceneGraph();
    }

    private void initSceneGraph() {
        VirtualUniverse universe = new VirtualUniverse();
        locale = new Locale(universe);

        BranchGroup sceneRoot = new BranchGroup();

        Background background = new Background(new Color3f(0.8f, 0.9f, 1));
        background.setApplicationBounds(new BoundingSphere(new Point3d(), Double.MAX_VALUE));
        sceneRoot.addChild(background);

        sceneSwitch = new Switch();
        sceneSwitch.setCapability(Switch.ALLOW_CHILDREN_EXTEND);
        sceneSwitch.setCapability(Switch.ALLOW_CHILDREN_WRITE);
        sceneSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);
        sceneSwitch.addChild(createLoaderSceneGraph());
        sceneSwitch.addChild(null);
        sceneRoot.addChild(sceneSwitch);

        locale.addBranchGraph(sceneRoot);
    }

    public void addView(Camera view) {
        BranchGroup viewBranchGroup = view.getViewBranch();
        locale.addBranchGraph(viewBranchGroup);
        views.add(view);
    }

    public void showLoader() {
        for (Camera view : views) {
            view.zoomToExtent((BranchGroup) sceneSwitch.getChild(0), 0.7f);
        }
        sceneSwitch.setWhichChild(0);
    }

    public void showScene(BranchGroup scene) {
        sceneSwitch.setChild(scene, 1);
        for (Camera view : views) {
            view.zoomToExtent(scene, 1);
        }
        sceneSwitch.setWhichChild(1);
    }

   public BranchGroup getMainScene(){
       return (BranchGroup) sceneSwitch.getChild(1);
   }

    public void addLights(BranchGroup group) {
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.MAX_VALUE);

        AmbientLight ambientLight = new AmbientLight(new Color3f(0.5f, 0.5f, 0.5f));
        ambientLight.setInfluencingBounds(bounds);
        group.addChild(ambientLight);

        Color3f lightColor = new Color3f(1.0f, 1.0f, 1.0f);
        Vector3f light1Direction = new Vector3f(4.0f, -8.0f, -16.0f);
        DirectionalLight light1 = new DirectionalLight(true, lightColor, light1Direction);
        light1.setInfluencingBounds(bounds);
        group.addChild(light1);

        light1Direction.negate();
        DirectionalLight light2 = new DirectionalLight(true, lightColor, light1Direction);
        light2.setInfluencingBounds(bounds);
        group.addChild(light2);
    }


    private BranchGroup createLoaderSceneGraph() {
        BranchGroup loaderBranchGroup = new BranchGroup();
        loaderBranchGroup.setCapability(BranchGroup.ALLOW_DETACH);
        final TransformGroup loaderTransformGroup = new TransformGroup();
        loaderTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        addLights(loaderBranchGroup);
        Appearance loaderAppearance = new Appearance();
        Color3f loaderColor = new Color3f(0.5f, 0.5f, 0.5f);
        Material loaderMaterial = new Material(loaderColor, new Color3f(), loaderColor, loaderColor, 10);
        loaderMaterial.setLightingEnable(true);
        loaderAppearance.setMaterial(loaderMaterial);
        for (int i = 0; i < 20; i++) {
            Transform3D translate3d = new Transform3D();
            translate3d.setTranslation(new Vector3f(0f, 0f, 0.9f));
            TransformGroup translate = new TransformGroup(translate3d);
            Transform3D rotationY3d = new Transform3D();
            rotationY3d.rotY((Math.PI * 2 * i) / 20);
            TransformGroup rotateY = new TransformGroup(rotationY3d);
            rotateY.addChild(translate);
            Box box = new Box(0.1f, 0.1f, 0.1f, loaderAppearance);
            translate.addChild(box);
            loaderTransformGroup.addChild(rotateY);
        }
        loaderBranchGroup.addChild(loaderTransformGroup);

        TransformInterpolator rotation = new TransformInterpolator(new Alpha(-1, 3000), loaderTransformGroup) {
            // infers with the loading process (jerking), separate thread solution wasn't absolutely smooth neither
            @Override
            public void computeTransform(float v, Transform3D transform3D) {
                Matrix3f rotX = new Matrix3f();
                rotX.rotX(v * 2 * (float) Math.PI);
                Matrix3f rotY = new Matrix3f();
                rotY.rotY((v - (int) (v * 2) * 2f) * 2 * (float) Math.PI);
                Matrix3f rot = new Matrix3f();
                rot.mul(rotX, rotY);
                transform3D.setRotation(rot);
            }
        };
        rotation.setSchedulingBounds(new BoundingSphere(new Point3d(), Double.MAX_VALUE));
        loaderTransformGroup.addChild(rotation);

        return loaderBranchGroup;
    }

    public void dispose() {
        for (Camera view:views){
            Enumeration allCanvas3Ds = view.getView().getAllCanvas3Ds();
            while (allCanvas3Ds.hasMoreElements()){
                Canvas3D canvas3D = (Canvas3D) allCanvas3Ds.nextElement();
                if (canvas3D.isOffScreen()) canvas3D.setOffScreenBuffer(null);
            }
            view.getView().removeAllCanvas3Ds();
            view.getView().attachViewPlatform(null);
        }
        locale.getVirtualUniverse().removeAllLocales();
        Primitive.clearGeometryCache();
    }
}
