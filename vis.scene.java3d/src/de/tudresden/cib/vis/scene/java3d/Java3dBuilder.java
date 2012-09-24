package de.tudresden.cib.vis.scene.java3d;

import de.tudresden.cib.vis.scene.ChangeMap;
import de.tudresden.cib.vis.scene.UIContext;
import de.tudresden.cib.vis.scene.VisBuilder;

import javax.media.j3d.*;
import javax.vecmath.Point3d;
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
                    if(scheduledChanges.containsKey(frame)) {
                        scheduledChanges.get(frame).changeAll();
                    }
                    frame = (frame+1 == maxFrame) ? 0 : frame+1;
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
}
