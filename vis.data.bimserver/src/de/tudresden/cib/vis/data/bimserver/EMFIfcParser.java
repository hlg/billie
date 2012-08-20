package de.tudresden.cib.vis.data.bimserver;

import org.apache.commons.io.IOUtils;
import org.bimserver.emf.IdEObject;
import org.bimserver.models.ifc2x3tc1.IfcElement;
import org.bimserver.models.ifc2x3tc1.IfcProduct;
import org.bimserver.models.ifc2x3tc1.IfcRelContainedInSpatialStructure;
import org.bimserver.models.ifc2x3tc1.IfcRoot;
import org.bimserver.plugins.PluginException;
import org.bimserver.plugins.PluginManager;
import org.bimserver.plugins.deserializers.DeserializeException;
import org.bimserver.plugins.deserializers.EmfDeserializer;
import org.bimserver.plugins.ifcengine.*;
import org.bimserver.plugins.serializers.IfcModelInterface;
import org.eclipse.emf.ecore.EObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


public class EMFIfcParser {

    private IfcEngineModel engineModel;
    private IfcEnginePlugin enginePlugin;
    private IfcEngine engine;
    private IfcEngineGeometry geometry;

    private EmfDeserializer deserializer;

    IfcModelInterface data;

    public EMFIfcParser(PluginManager pluginManager) {
        pluginManager.loadPluginsFromCurrentClassloader();
        pluginManager.initAllLoadedPlugins();
        enginePlugin = pluginManager.getAllIfcEnginePlugins(true).iterator().next();
        try {
            deserializer = pluginManager.getFirstDeserializer("ifc", true).createDeserializer();
            deserializer.init(pluginManager.requireSchemaDefinition());
            engine = enginePlugin.createIfcEngine();
            engine.init();
        } catch (PluginException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void read(InputStream inputStream) {
        try {
            byte[] bytes = IOUtils.toByteArray(inputStream); // todo: save memory by branching the stream with TeeInputStream
            engineModel = engine.openModel(bytes);
            engineModel.setPostProcessing(true);
            geometry = engineModel.finalizeModelling(engineModel.initializeModelling());
            data = deserializer.read(new ByteArrayInputStream(bytes), "?", true, 16 * 58);
            adjustRelations();
        } catch (IfcEngineException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (DeserializeException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void adjustRelations() {
        // due to http://code.google.com/p/bimserver/wiki/Known_issues
        for (EObject eObject : data.getAllWithSubTypes(IfcRelContainedInSpatialStructure.class)) {
            IfcRelContainedInSpatialStructure relation = (IfcRelContainedInSpatialStructure) eObject;
            for (IfcProduct product : relation.getRelatedElements()) {
                if (product instanceof IfcElement) {
                    ((IfcElement) product).getContainedInStructure().add(relation);
                }
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        engineModel.close();
        engine.close();
    }

    public EngineEObject getWrappedObject(String objectID) {
        return getWrappedObject(data.get(objectID));
    }

    public EngineEObject getWrappedObject(IfcRoot ifcRoot) {
        return new EngineEObject(ifcRoot, engineModel, geometry);
    }

    public Iterator<EngineEObject> getIterator() {
        return data != null ? new EngineIterator(data.getValues().iterator()) : null;
    }

    class EngineIterator implements Iterator<EngineEObject> {

        private Iterator<IdEObject> baseIterator;

        EngineIterator(Iterator<IdEObject> baseIterator) {
            this.baseIterator = baseIterator;
        }

        public boolean hasNext() {
            return baseIterator.hasNext();
        }

        public EngineEObject next() {
            return new EngineEObject(baseIterator.next(), engineModel, geometry);
        }

        public void remove() {
            baseIterator.remove();
        }
    }

    public static class EngineEObject {
        private IdEObject idEObject;
        private IfcEngineModel engineModel;
        private IfcEngineGeometry geometry;

        private EngineEObject(IdEObject object, IfcEngineModel ifcEngineModel, IfcEngineGeometry geometry) {
            this.idEObject = object;
            this.engineModel = ifcEngineModel;
            this.geometry = geometry;
        }

        public EObject getObject() {
            return idEObject;
        }

        public Geometry getGeometry() {
            if (!(idEObject instanceof IfcProduct)) return null;
            Geometry objectGeometry = new Geometry();
            try {
                IfcEngineInstance instance = engineModel.getInstanceFromExpressId((int) idEObject.getOid());
                IfcEngineInstanceVisualisationProperties visProps = instance.getVisualisationProperties();
                objectGeometry.vertizes = new ArrayList<Float>();
                objectGeometry.normals = new ArrayList<Float>();
                objectGeometry.indizes = new ArrayList<Integer>(visProps.getPrimitiveCount());
                Map<Integer, Integer> reindex = new HashMap<Integer, Integer>();
                int newIndex = -1;
                for (int i = 0; i < visProps.getPrimitiveCount() * 3; i++) {
                    int oldIndex = geometry.getIndex(i + visProps.getStartIndex());
                    if (reindex.containsKey(oldIndex)) {
                        objectGeometry.indizes.add(reindex.get(oldIndex));
                    } else {
                        newIndex++;
                        reindex.put(oldIndex, newIndex);
                        objectGeometry.indizes.add(newIndex);
                        int oldCoordIndex = oldIndex * 3;
                        objectGeometry.normals.add(geometry.getNormal(oldCoordIndex));
                        objectGeometry.vertizes.add(geometry.getVertex(oldCoordIndex));
                        objectGeometry.normals.add(geometry.getNormal(oldCoordIndex + 1));
                        objectGeometry.vertizes.add(geometry.getVertex(oldCoordIndex + 1));
                        objectGeometry.normals.add(geometry.getNormal(oldCoordIndex + 2));
                        objectGeometry.vertizes.add(geometry.getVertex(oldCoordIndex + 2));
                    }
                }
            } catch (IfcEngineException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            return objectGeometry;
        }
    }

    public static class Geometry {
        public List<Float> vertizes;
        public List<Float> normals;
        public List<Integer> indizes;
    }


}
