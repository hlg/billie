package de.tudresden.cib.vis.data.bimserver;

import de.tudresden.cib.vis.data.Hierarchic;
import de.tudresden.cib.vis.data.IndexedDataAccessor;
import org.bimserver.emf.IdEObject;
import org.bimserver.models.ifc2x3tc1.*;
import org.bimserver.plugins.PluginManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class EMFIfcHierarchicAcessor extends IndexedDataAccessor<Hierarchic<IdEObject>> {

    private String namespace = "";
    private EMFIfcPlainParser parser;
    private HashMap<String, Hierarchic<IdEObject>> wrappedData;

    public EMFIfcHierarchicAcessor(PluginManager pluginManager) {
        parser = new EMFIfcPlainParser(pluginManager);
    }

    public EMFIfcHierarchicAcessor(SimplePluginManager simplePluginManager, InputStream input, long size) throws IOException {
        this(simplePluginManager);
        read(input, size);
    }

    public void read(InputStream inputStream, long size) throws IOException {
        parser.read(inputStream, size);
    }

    @Override
    public void readFromFolder(File directory) {
        throw new UnsupportedOperationException();
    }

    public void read(InputStream inputStream, String namespace, long size) throws IOException {
        read(inputStream, size);
        this.namespace = namespace + "::";
    }

    @Override
    public void index() {
        parser.data.indexGuids();
        wrappedData = new HashMap<String, Hierarchic<IdEObject>>();
        IfcProject project = (IfcProject) parser.data.get(IfcProject.class);
        if(project.isSetIsDecomposedBy()){
            IfcSpatialStructureElement root = findSpatialRoot(project);
            if(root!=null) stepIn(root, new ArrayList<Integer>(), 0, 0, null);
        }
    }

    private IfcSpatialStructureElement findSpatialRoot(IfcProject project) {
        for(IfcRelDecomposes decomposition: project.getIsDecomposedBy()){
            IfcObjectDefinition firstChild = decomposition.getRelatedObjects().iterator().next();
            if(firstChild instanceof IfcSpatialStructureElement){
                return (IfcSpatialStructureElement) firstChild;
            }
        }
        return null;
    }

    private Hierarchic<IdEObject> stepIn(final IfcSpatialStructureElement object, final List<Integer> path, final int nodesBefore, final int depth, final Hierarchic<IdEObject> parentNode) {
        Hierarchic<IdEObject> node = new Hierarchic<IdEObject>() {
            public int nodeSize;
            private Collection<Hierarchic<? extends IdEObject>> children = new HashSet<Hierarchic<? extends IdEObject>>();
            private Hierarchic<IdEObject> parent = parentNode;

            public Hierarchic<IdEObject> getParent() {
                return parent;
            }

            public Collection<Hierarchic<? extends IdEObject>> getChildren() {
                return children;
            }
            public void addChild(Hierarchic<IdEObject> child){
                children.add(child);
            }
            public List<Integer> getPath() {
                return path;
            }
            public void setNodeSize(int nodeSize){
                this.nodeSize = nodeSize;
            }

            public int getNodesBefore() {
                return nodesBefore;
            }

            public int getDepth() {
                return depth;
            }

            public int getNodeSize() {
                return nodeSize;
            }

            public IdEObject getObject() {
                return object;
            }
        };
        wrappedData.put(object.getGlobalId().getWrappedValue(), node);
        int size = 0;
        int pos = 0;
        if (object.isSetIsDecomposedBy()){
            for(IfcObjectDefinition child: object.getIsDecomposedBy().iterator().next().getRelatedObjects()){
                List<Integer> newPath = new ArrayList<Integer>(path);
                newPath.add(pos);
                Hierarchic<IdEObject> childNode = stepIn((IfcSpatialStructureElement) child, newPath, nodesBefore + size, depth + 1, node);
                node.addChild(childNode);
                size += childNode.getNodeSize();
                pos++;
            }
        }
        if (object.isSetContainsElements()){
            for(IfcProduct contained: object.getContainsElements().iterator().next().getRelatedElements()){
                List<Integer> newPath = new ArrayList<Integer>(path);
                newPath.add(pos);
                Hierarchic<IdEObject> child = stepIn((IfcElement) contained, newPath, nodesBefore + size, depth + 1, node);
                node.addChild(child);
                size++;
                pos++;
            }
        }
        if(size==0) size=1;
        node.setNodeSize(size);
        return node;
    }

    private Hierarchic<IdEObject> stepIn(final IfcElement contained, final List<Integer> path, final int nodesBefore, final int depth, final Hierarchic<IdEObject> parent) {
        Hierarchic<IdEObject> node = new Hierarchic<IdEObject>() {
            public int nodeSize = 1;

            public Hierarchic<IdEObject> getParent() {
                return parent;
            }

            public Collection<Hierarchic<? extends IdEObject>> getChildren() {
                return Collections.emptySet();
            }

            public void addChild(Hierarchic<IdEObject> child) {
            }

            public List<Integer> getPath() {
                return path;
            }
            public void setNodeSize(int nodeSize){
                this.nodeSize = nodeSize;
            }

            public int getNodesBefore() {
                return nodesBefore;
            }

            public int getDepth() {
                return depth;
            }

            public int getNodeSize() {
                return nodeSize;
            }

            public IdEObject getObject() {
                return contained;
            }
        };
        wrappedData.put(contained.getGlobalId().getWrappedValue(), node);
        return node;
    }

    @Override
    public Hierarchic<IdEObject> getIndexed(String objectID) {
        return wrappedData.get(objectID);
    }

    public Iterator<Hierarchic<IdEObject>> iterator() {
        return wrappedData.values().iterator();
    }
}
