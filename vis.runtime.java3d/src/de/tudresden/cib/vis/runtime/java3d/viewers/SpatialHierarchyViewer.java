package de.tudresden.cib.vis.runtime.java3d.viewers;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.sun.j3d.loaders.Loader;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.behaviors.PickMouseBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;
import de.tudresden.cib.vis.data.bimserver.SimplePluginManager;
import de.tudresden.cib.vis.runtime.java3d.UniverseBuilder;
import de.tudresden.cib.vis.runtime.java3d.colorTime.TypeAppearance;
import de.tudresden.cib.vis.runtime.java3d.loaders.BimserverSpatialHierarchyLoader;
import de.tudresden.cib.vis.runtime.java3d.views.OrbitalView;

import javax.media.j3d.*;
import javax.vecmath.Point3d;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Enumeration;

/**
 * @author helga
 */
public class SpatialHierarchyViewer extends SimpleViewer {
    private Canvas3D canvas;
    private Appearance defaultAppearance;
    private Appearance noAppearance;
    private Shape3D selectedNode;
    private Appearance contextAppearance;

    SpatialHierarchyViewer(Loader loader) {
        super(loader);
        logger.info("lets go");
        defaultAppearance = TypeAppearance.DEFAULT.getAppearance();
        noAppearance = TypeAppearance.OFF.getAppearance();
        RenderingAttributes noRendering = new RenderingAttributes();
        noRendering.setVisible(false);
        noAppearance.setRenderingAttributes(noRendering);
        contextAppearance = TypeAppearance.DEACTIVATED.getAppearance();
    }

    @Override
    public void setupViews() {
        setSize(800, 600);
        canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        universe = new UniverseBuilder();
        universe.addView(new OrbitalView(canvas));
        canvas.setVisible(true);
        add(canvas);
        validate();
    }

    @Override
    public void run(String input) throws FileNotFoundException {
        loadFile(input);
        setupBehaviour(scene.getSceneGroup());
        showScene();
    }

    public static void main(String[] args) throws Exception {
        BimserverSpatialHierarchyLoader loader = new BimserverSpatialHierarchyLoader(new SimplePluginManager());
        loader.setDefaultAppearance(TypeAppearance.OFF.getAppearance());
        loader.setDefaultPickability(false);
        SpatialHierarchyViewer ifcViewer = new SpatialHierarchyViewer(loader);
        File file = ifcViewer.chooseFile(args.length > 0 ? args[0] : null, "ifc");
        ifcViewer.run(file.getPath());
    }

    public void setupBehaviour(BranchGroup mainScene) {
        BranchGroup root = mainScene;
        while (root.getAllChildren().hasMoreElements() && !Iterators.any(Iterators.<Node>forEnumeration(root.getAllChildren()), new Predicate<Node>() {
            public boolean apply(Node o) {
                return o instanceof Shape3D;
            }
        }))
            root = (BranchGroup) Iterators.find(Iterators.<Node>forEnumeration(root.getAllChildren()), new Predicate<Node>() {
                public boolean apply(Node node) {
                    return node instanceof BranchGroup;
                }
            });
        setAllShapesOfTo(root, defaultAppearance, true);
        PickMouseBehavior pickMouseBehavior = new PickMouseBehavior(canvas, root, null) {
            @Override
            public void updateScene(int x, int y) {
                pickCanvas.setShapeLocation(x, y);
                PickResult pickResult = pickCanvas.pickClosest();
                selectedNode = (pickResult != null) ? (Shape3D) pickResult.getObject() : null;
                if (selectedNode != null) {
                    BranchGroup grandParent = (BranchGroup) selectedNode.getParent().getParent();
                    if (grandParent != null) {
                        setAllShapesOfTo(grandParent, noAppearance, false);
                    }
                    setAllShapesOfTo((BranchGroup) selectedNode.getParent(), contextAppearance, false);
                    selectedNode.setAppearance(noAppearance);
                    selectedNode.setPickable(false);
                    Enumeration<Node> siblings = ((BranchGroup) selectedNode.getParent()).getAllChildren();
                    while (siblings.hasMoreElements()) {
                        Node node = siblings.nextElement();
                        if (node instanceof BranchGroup) setAllShapesOfTo((BranchGroup) node, defaultAppearance, true);
                    }
                }
            }
        };
        pickMouseBehavior.setSchedulingBounds(new BoundingSphere(new Point3d(0, 0, 0), Double.MAX_VALUE));
        mainScene.addChild(pickMouseBehavior);
    }


    private void setAllShapesOfTo(BranchGroup groupNode, Appearance appearance, boolean pickable) {
        Enumeration<Node> children = groupNode.getAllChildren();
        while (children.hasMoreElements()) {
            Node child = children.nextElement();
            if (child instanceof Shape3D) {
                ((Shape3D) child).setAppearance(appearance);
                child.setPickable(pickable);
            }
        }
    }

    private void setAllShapesOfTo(BranchGroup groupNode, Appearance appearance) {
        setAllShapesOfTo(groupNode, appearance, true);
    }
}
