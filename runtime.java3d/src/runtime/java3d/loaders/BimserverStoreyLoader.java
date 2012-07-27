package runtime.java3d.loaders;

import org.bimserver.models.ifc2x3tc1.*;
import org.eclipse.emf.common.util.EList;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Group;
import javax.media.j3d.ViewSpecificGroup;
import java.util.*;

/**
 * @author helga
 */

public class BimserverStoreyLoader extends BimserverJava3dLoader implements MultiLoader {
    private Map<IfcBuildingStorey, Set<IfcBuildingElement>> storyMap = new HashMap<IfcBuildingStorey, Set<IfcBuildingElement>>();
    private Map<IfcBuildingStorey, ViewSpecificGroup> storeyNodes = new HashMap<IfcBuildingStorey, ViewSpecificGroup>();

    public Map<IfcBuildingStorey, Set<IfcBuildingElement>> getStoryMap() {
        return storyMap;
    }

    @Override
    protected BranchGroup createSceneGraph() {
        BranchGroup buildingBranchGroup = new BranchGroup();
        Map<IfcBuildingElement, ViewSpecificGroup> storeyGroupsByElement = new HashMap<IfcBuildingElement, ViewSpecificGroup>();
        for (IfcBuildingStorey ifcBuildingStorey : model.getAll(IfcBuildingStorey.class)) {
            EList<IfcRelContainedInSpatialStructure> containsElements = ifcBuildingStorey.getContainsElements();
            if (!containsElements.isEmpty()) {
                HashSet<IfcBuildingElement> elements = new HashSet<IfcBuildingElement>();
                storyMap.put(ifcBuildingStorey, elements);
                ViewSpecificGroup storeyGroup = new ViewSpecificGroup();
                storeyGroup.setCapability(ViewSpecificGroup.ALLOW_VIEW_WRITE);
                storeyNodes.put(ifcBuildingStorey, storeyGroup);
                buildingBranchGroup.addChild(storeyGroup);
                for (IfcRelContainedInSpatialStructure containment : containsElements) {
                    for (IfcProduct product : containment.getRelatedElements()) {
                        if (product instanceof IfcBuildingElement) {
                            IfcBuildingElement element = (IfcBuildingElement) product;
                            storeyGroupsByElement.put(element, storeyGroup);
                            elements.add(element);
                        }
                    }
                }
            }
        }
        ViewSpecificGroup defaultStoreyGroup = null;
        for (IfcBuildingElement ifcBuildingElement : model.getAllWithSubTypes(IfcBuildingElement.class)) {
            /* IfcBuildingStorey storey = findStorey(ifcBuildingElement);
            addToStoreyMap(storey, ifcBuildingElement);
            ViewSpecificGroup storeyGroup = createStoreyNode(storey, buildingBranchGroup); */
            ViewSpecificGroup storeyGroup = storeyGroupsByElement.get(ifcBuildingElement);
            if (storeyGroup == null) {
                if (defaultStoreyGroup == null) defaultStoreyGroup = createDefaultStoreyGroup(buildingBranchGroup);
                storeyGroup = defaultStoreyGroup;
            }

            createAndAddShapes(ifcBuildingElement, storeyGroup);
        }
        return buildingBranchGroup;
    }

    private ViewSpecificGroup createDefaultStoreyGroup(BranchGroup buildingBranchGroup) {
        ViewSpecificGroup defaultStoreyGroup = new ViewSpecificGroup();
        defaultStoreyGroup.setCapability(ViewSpecificGroup.ALLOW_VIEW_WRITE);
        buildingBranchGroup.addChild(defaultStoreyGroup);
        storeyNodes.put(null, defaultStoreyGroup);
        return defaultStoreyGroup;
    }

    private ViewSpecificGroup createStoreyNode(IfcBuildingStorey storey, Group branchGroup) {
        ViewSpecificGroup storeyGroup = storeyNodes.get(storey);
        if (storeyGroup == null) {
            storeyGroup = new ViewSpecificGroup();
            storeyGroup.setCapability(ViewSpecificGroup.ALLOW_VIEW_WRITE);
            storeyNodes.put(storey, storeyGroup);
            branchGroup.addChild(storeyGroup);
        }
        return storeyGroup;
    }

    private void addToStoreyMap(IfcBuildingStorey structure, IfcBuildingElement buildingElement) {
        Set<IfcBuildingElement> elements = storyMap.get(structure);
        if (elements == null) {
            elements = new HashSet<IfcBuildingElement>();
            storyMap.put(structure, elements);
        }
        elements.add(buildingElement);

    }

    private IfcBuildingStorey findStorey(IfcBuildingElement ifcBuildingElement) {
        EList<IfcRelContainedInSpatialStructure> parents = ifcBuildingElement.getContainedInStructure();
        IfcSpatialStructureElement structure = null;
        if (parents.size() > 0) structure = parents.get(0).getRelatingStructure();
        while (structure != null && !isRegularBuildingStorey(structure)) {
            structure = getParentStructure(structure);
        }
        return (IfcBuildingStorey) structure;
    }

    private boolean isRegularBuildingStorey(IfcSpatialStructureElement structure) {
        return !(structure instanceof IfcBuildingStorey) && structure.getCompositionType() != IfcElementCompositionEnum.ELEMENT;
    }

    private IfcSpatialStructureElement getParentStructure(IfcSpatialStructureElement structure) {
        EList<IfcRelDecomposes> parentStructures = structure.getDecomposes();
        return (parentStructures.size() > 0) ? (IfcSpatialStructureElement) parentStructures.get(0).getRelatingObject() : null;
    }

    public Map<IfcBuildingStorey, ViewSpecificGroup> getStoreyNodes() {
        return storeyNodes;
    }

    public Collection<ViewSpecificGroup> getSubScenes() {
        return storeyNodes.values();
    }
}
