package de.tudresden.cib.vis.scene.java3d;

import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.behaviors.PickMouseBehavior;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.scene.*;

import javax.media.j3d.*;
import javax.vecmath.Point3d;
import java.util.Collection;
import java.util.Enumeration;
import java.util.TreeMap;

public class Java3dBuilder implements VisBuilder<Java3dFactory.Java3DGraphObject, BranchGroup> {
    private BranchGroup buildingBranchGroup;
    private TransformGroup buildingTransformGroup;
    private UIContext uiContext = new UIContext() {
        public void runInUIContext(Runnable runnable) {
            runnable.run();
        }

        @Override
        public void animate(final TreeMap<Integer, ChangeMap> scheduledChanges) {
            Behavior animation = new Behavior() {
                WakeupOnElapsedTime frameInterval = new WakeupOnElapsedTime(40);
                int maxFrame = scheduledChanges.lastKey();
                int frame = 0;

                @Override
                public void initialize() {
                    wakeupOn(frameInterval);
                }

                @Override
                public void processStimulus(Enumeration enumeration) {
                    frame = (frame == maxFrame) ? 0 : frame+1;
                    if(scheduledChanges.containsKey(frame)) {
                        scheduledChanges.get(frame).changeAll();
                    }
                    wakeupOn(frameInterval);
                }
            };
            animation.setSchedulingBounds(new BoundingSphere(new Point3d(), Double.MAX_VALUE));
            buildingBranchGroup.addChild(animation);
        }

        @Override
        public void dispose() {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    };

    public void init() {
        buildingTransformGroup = new TransformGroup();
        buildingBranchGroup = new BranchGroup();
        buildingBranchGroup.addChild(buildingTransformGroup);
    }

    public void addPart(Java3dFactory.Java3DGraphObject graphicalObject) {
        buildingTransformGroup.addChild((Node) graphicalObject);
    }

    public void finish() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public BranchGroup getScene() {
        return buildingBranchGroup;
    }

    public UIContext getUiContext() {
        return uiContext;
    }

    @Override
    public void addTriggers(final Event event, final Collection<VisFactory2D.GraphObject> triggers, final SceneManager<?, BranchGroup> sceneManager) {
        if(event.equals(DefaultEvent.CLICK)){
            Canvas3D canvas = null; // TODO: get the canvas in here somehow (constructor, UIcontext?), or create MouseListener and add to canvas later
            PickMouseBehavior pickMouseBehavior = new PickMouseBehavior(canvas, sceneManager.getScene(), null) {
                @Override
                public void updateScene(int x, int y) {
                    pickCanvas.setShapeLocation(x, y);
                    PickResult pickInfo = pickCanvas.pickClosest();
                    if (pickInfo != null) {
                        Object picked = pickInfo.getObject();
                        assert picked instanceof Java3dFactory.Java3DGraphObject;
                        Java3dFactory.Java3DGraphObject selected = (Java3dFactory.Java3DGraphObject) picked;
                        if(triggers.contains(selected)) sceneManager.fire(event, selected);
                    }
                }
            };
            pickMouseBehavior.setSchedulingBounds(new BoundingSphere(new Point3d(0, 0, 0), Double.MAX_VALUE));
            sceneManager.getScene().addChild(pickMouseBehavior);
        }
    }

    public static <E,C> Mapper<E, C, Java3dFactory.Java3DGraphObject, BranchGroup> createMapper(DataAccessor<E, C> data){
        return new Mapper<E, C, Java3dFactory.Java3DGraphObject, BranchGroup>(data, new Java3dFactory(), new Java3dBuilder());
    }
}
