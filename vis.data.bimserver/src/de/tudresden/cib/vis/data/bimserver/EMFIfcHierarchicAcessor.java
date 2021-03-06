package de.tudresden.cib.vis.data.bimserver;

import de.tudresden.cib.vis.data.DataAccessException;
import de.tudresden.cib.vis.data.Hierarchic;
import de.tudresden.cib.vis.data.HierarchicBase;
import de.tudresden.cib.vis.data.IndexedDataAccessor;
import de.tudresden.cib.vis.filter.Condition;
import de.tudresden.cib.vis.filter.ConditionFilter;
import org.bimserver.emf.IdEObject;
import org.bimserver.models.ifc2x3tc1.*;
import org.bimserver.plugins.PluginManager;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;

public class EMFIfcHierarchicAcessor extends IndexedDataAccessor<Hierarchic<IdEObject>, Condition<Hierarchic<IdEObject>>> {

    private final ConditionFilter<Hierarchic<IdEObject>> filter;
    private EMFIfcPlainParser parser;
    private HashMap<String, Hierarchic<IdEObject>> wrappedData;
    public static boolean SKIP_LAST_LEVEL = true;

    public EMFIfcHierarchicAcessor() throws DataAccessException{
        this(new SimplePluginManager());
    }

    public EMFIfcHierarchicAcessor(PluginManager pluginManager) throws DataAccessException {
        parser = new EMFIfcPlainParser(pluginManager);
        filter = new ConditionFilter<Hierarchic<IdEObject>>();
    }

    public EMFIfcHierarchicAcessor(PluginManager simplePluginManager, URL url) throws IOException, DataAccessException {
        this(simplePluginManager);
        read(url);
    }

    public void read(URL url) throws IOException, DataAccessException {
        URLConnection connection = url.openConnection();
        parser.read(connection.getInputStream(), connection.getContentLength());
    }

    @Override
    public Iterable<? extends Hierarchic<IdEObject>> filter(Condition<Hierarchic<IdEObject>> condition) {
        return filter.filter(condition, this);
    }

    @Override
    public Condition<Hierarchic<IdEObject>> getDefaultCondition() {
        return new Condition<Hierarchic<IdEObject>>(){
            @Override
            public boolean matches(Hierarchic<IdEObject> data) {
                return true;
            }
        };
    }

    @Override
    public void index() {
        parser.data.indexGuids();
        wrappedData = new HashMap<String, Hierarchic<IdEObject>>();
        IfcProject project = (IfcProject) parser.data.get(IfcProject.class);
        if(project.isSetIsDecomposedBy()){
            IfcSpatialStructureElement root = findSpatialRoot(project);
            if(root!=null) stepIn(root, 0, 0, null);
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

    private Hierarchic<IdEObject> stepIn(final IfcSpatialStructureElement object, final int nodesBefore, final int depth, final Hierarchic<IdEObject> parentNode) {
        Hierarchic<IdEObject> node = new HierarchicIfc(parentNode, nodesBefore, depth, object);
        wrappedData.put(object.getGlobalId().getWrappedValue(), node);
        int size = 0;
        if (object.isSetIsDecomposedBy()){
            for(IfcObjectDefinition child: object.getIsDecomposedBy().iterator().next().getRelatedObjects()){
                Hierarchic<IdEObject> childNode = stepIn((IfcSpatialStructureElement) child, nodesBefore + size, depth + 1, node);
                node.addChild(childNode);
                size += childNode.getNodeSize();
            }
        }
        if (object.isSetContainsElements()){
            for(IfcProduct contained: object.getContainsElements().iterator().next().getRelatedElements()){
                if(contained instanceof IfcElement) {
                    Hierarchic<IdEObject> child = stepIn((IfcElement) contained, nodesBefore + size, depth + 1, node);
                    node.addChild(child);
                    if (!SKIP_LAST_LEVEL) size++;
                }
            }
        }
        if(size==0) size=1;
        node.setNodeSize(size);
        return node;
    }

    private Hierarchic<IdEObject> stepIn(final IfcElement contained, final int nodesBefore, final int depth, final Hierarchic<IdEObject> parent) {
        Hierarchic<IdEObject> node = new HierarchicIfc(parent, nodesBefore, depth, contained);
        node.setNodeSize(1);
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

    public static class HierarchicIfc extends HierarchicBase<IdEObject> {

        public HierarchicIfc(Hierarchic<IdEObject> parentNode, int nodesBefore, int depth, IdEObject object) {
            super(parentNode, nodesBefore, depth, object);
        }
    }
}
