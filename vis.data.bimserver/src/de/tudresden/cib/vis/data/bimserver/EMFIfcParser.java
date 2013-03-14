package de.tudresden.cib.vis.data.bimserver;

import de.tudresden.cib.vis.data.DataAccessException;
import de.tudresden.cib.vis.data.Geometric;
import de.tudresden.cib.vis.data.Geometry;
import org.apache.commons.io.input.TeeInputStream;
import org.bimserver.emf.IdEObject;
import org.bimserver.models.ifc2x3tc1.IfcProduct;
import org.bimserver.plugins.PluginException;
import org.bimserver.plugins.PluginManager;
import org.bimserver.plugins.deserializers.DeserializeException;
import org.bimserver.plugins.ifcengine.*;
import org.bimserver.plugins.serializers.EmfSerializer;
import org.bimserver.plugins.serializers.SerializerException;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;


public class EMFIfcParser extends EMFIfcPlainParser {

    private IfcEngineModel engineModel;
    private IfcEngine engine;
    private IfcEngineGeometry geometry;
    private EmfSerializer serializer;
    private PluginManager pluginManager;
    private boolean forkInput;
    Collection<EngineEObject> wrapped;

    public EMFIfcParser(PluginManager pluginManager, boolean forkInput) throws DataAccessException {
        super(pluginManager);
        this.pluginManager = pluginManager;
        this.forkInput = forkInput;
        try {
            IfcEnginePlugin enginePlugin = pluginManager.requireIfcEngine();
            serializer = pluginManager.requireIfcStepSerializer();
            engine = enginePlugin.createIfcEngine();
            // if (engine instanceof FailSafeIfcEngine) ((FailSafeIfcEngine)engine).setUseSecondJvm(false);
            engine.init();
        } catch (SerializerException e) {
            throw new DataAccessException(e);
        } catch (PluginException e) {
            throw new DataAccessException(e);
        }
    }

    public void read(InputStream inputStream, final long size) throws DataAccessException {
        if (forkInput) readPiped(inputStream, size);
        else readMemCopy(inputStream, size);
    }
        public void readMemCopy(InputStream inputStream, final long size) throws DataAccessException {
        try {
            data = deserializer.read(inputStream, "?", true, size);
            serializer.init(data, null, pluginManager, engine);
            engineModel = engine.openModel(serializer.getBytes());
            engineModel.setPostProcessing(true);
            geometry = engineModel.finalizeModelling(engineModel.initializeModelling());
            adjustRelations();
            wrapData();
        } catch (DeserializeException e) {
            throw new DataAccessException(e);
        } catch (SerializerException e) {
            throw new DataAccessException(e);
        } catch (IfcEngineException e) {
            throw new DataAccessException(e);
        }
    }

    private void wrapData() {
        wrapped = new HashSet<EngineEObject>();
        for(IdEObject emfObject : data) wrapped.add(new EngineEObject(emfObject, engineModel, geometry));
    }

    public void readPiped(InputStream inputStream, final long size) throws DataAccessException {
        try {
            final PipedInputStream piped = new PipedInputStream();
            OutputStream out = new PipedOutputStream(piped);
            final InputStream tee = new TeeInputStream(inputStream, out); // in read in new thread as it puts
            Thread dataRead = new Thread(new Runnable() {
                public void run() {
                    try {
                        data = deserializer.read(tee, "?", true, size);
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
            wrapData();
        } catch (IOException e) {
            throw new DataAccessException(e);
        } catch (InterruptedException e) {
            throw new DataAccessException(e);
        } catch (IfcEngineException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        engineModel.close();
        engine.close();
    }

    public Iterator<EngineEObject> getIterator() {
        return wrapped != null ? wrapped.iterator() : null;
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
