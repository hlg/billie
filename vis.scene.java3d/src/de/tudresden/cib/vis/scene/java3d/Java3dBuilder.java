package de.tudresden.cib.vis.scene.java3d;

import de.tudresden.cib.vis.scene.VisBuilder;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Node;
import javax.media.j3d.TransformGroup;

public class Java3dBuilder implements VisBuilder<Java3dFactory.Java3DGraphObject, BranchGroup> {
    private BranchGroup buildingBranchGroup;
    private TransformGroup buildingTransformGroup;

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
}
