package de.tudresden.cib.vis.data.bimserver;

import de.tudresden.cib.vis.data.Geometric;
import de.tudresden.cib.vis.data.Geometry;
import org.apache.commons.io.input.TeeInputStream;
import org.bimserver.emf.IdEObject;
import org.bimserver.models.ifc2x3tc1.IfcProduct;
import org.bimserver.models.ifc2x3tc1.IfcRoot;
import org.bimserver.plugins.PluginException;
import org.bimserver.plugins.PluginManager;
import org.bimserver.plugins.deserializers.DeserializeException;
import org.bimserver.plugins.ifcengine.*;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class EMFIfcParser extends EMFIfcPlainParser {

    private IfcEngineModel engineModel;
    private IfcEnginePlugin enginePlugin;
    private IfcEngine engine;
    private IfcEngineGeometry geometry;

    public EMFIfcParser(PluginManager pluginManager) {
        super(pluginManager);
        enginePlugin = pluginManager.getAllIfcEnginePlugins(true).iterator().next();
        try {
            engine = enginePlugin.createIfcEngine();
            engine.init();
        } catch (PluginException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void read(InputStream inputStream, final long size) {
        try {
            final PipedInputStream piped = new PipedInputStream();
            OutputStream out = new PipedOutputStream(piped);
            final InputStream tee = new TeeInputStream(inputStream, out); // in read in new thread as it puts
            Thread dataRead = new Thread(new Runnable() {
                public void run() {
                    try {
                        data = deserializer.read(tee, "?", true, 16 * 58);
                    } catch (DeserializeException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            });
            dataRead.start();
            engineModel = engine.openModel(piped, (int) size);
            engineModel.setPostProcessing(true);
            geometry = engineModel.finalizeModelling(engineModel.initializeModelling());
            dataRead.join();
            adjustRelations();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IfcEngineException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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

    public static class EngineEObject implements Geometric<IdEObject> {
        private IdEObject idEObject;
        private IfcEngineGeometry geometry;
        private IfcEngineInstanceVisualisationProperties visProps;

        private EngineEObject(IdEObject object, IfcEngineModel ifcEngineModel, IfcEngineGeometry geometry) {
            this.idEObject = object;
            try {
                this.visProps = ifcEngineModel.getInstanceFromExpressId((int) idEObject.getOid()).getVisualisationProperties();
            } catch (IfcEngineException e) {
                LoggerFactory.getLogger(getClass()).error("unable to retrieve engine object");
            }
            this.geometry = geometry;
        }

        public IdEObject getObject() {
            return idEObject;
        }

        public Geometry getGeometry() {
            if (!(idEObject instanceof IfcProduct)) return null;
            Geometry objectGeometry = new Geometry();
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
            return objectGeometry;
        }
    }

}
