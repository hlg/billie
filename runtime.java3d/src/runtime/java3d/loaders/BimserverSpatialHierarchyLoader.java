package runtime.java3d.loaders;

import org.bimserver.models.ifc2x3tc1.IfcObjectDefinition;
import org.bimserver.models.ifc2x3tc1.IfcProduct;
import org.bimserver.models.ifc2x3tc1.IfcProject;
import org.bimserver.models.ifc2x3tc1.IfcRelDecomposes;
import org.bimserver.plugins.PluginManager;

import javax.media.j3d.BranchGroup;

/**
 * @author helga
 */
public class BimserverSpatialHierarchyLoader extends BimserverJava3dLoader {

    public BimserverSpatialHierarchyLoader(PluginManager pm) {
        super(pm);
    }

    @Override
    protected BranchGroup createSceneGraph() {
        BranchGroup sceneRoot = new BranchGroup();
        IfcProject spatialRoot = (IfcProject) model.get(IfcProject.class);
        buildSceneGraphFor(spatialRoot, sceneRoot);
        return sceneRoot;
    }

    private void buildSceneGraphFor(IfcObjectDefinition spatialParent, BranchGroup sceneParent) {
        for (IfcRelDecomposes relation : spatialParent.getIsDecomposedBy()) {
            for (IfcObjectDefinition spatialChild : relation.getRelatedObjects()) {
                BranchGroup sceneChild = createdBranchGroupFor(spatialChild);
                sceneParent.addChild(sceneChild);
                buildSceneGraphFor(spatialChild, sceneChild);
            }
        }
    }

    private BranchGroup createdBranchGroupFor(IfcObjectDefinition spatialNode) {
        final BranchGroup sceneNode = new BranchGroup();
        assert spatialNode instanceof IfcProduct;
        createAndAddShapes((IfcProduct) spatialNode, sceneNode);
        return sceneNode;
    }
}
