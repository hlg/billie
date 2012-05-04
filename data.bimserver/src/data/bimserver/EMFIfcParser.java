package data.bimserver;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.bimserver.emf.IdEObject;
import org.bimserver.models.ifc2x3.IfcElement;
import org.bimserver.models.ifc2x3.IfcProduct;
import org.bimserver.models.ifc2x3.IfcRelContainedInSpatialStructure;
import org.bimserver.plugins.PluginException;
import org.bimserver.plugins.PluginManager;
import org.bimserver.plugins.deserializers.DeserializeException;
import org.bimserver.plugins.deserializers.EmfDeserializer;
import org.bimserver.plugins.ifcengine.*;
import org.bimserver.plugins.serializers.IfcModelInterface;
import org.eclipse.emf.ecore.EObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class EMFIfcParser {

    private IfcEngineModel engineModel;
    private IfcEnginePlugin enginePlugin;
    private IfcEngine engine;
    private IfcEngineGeometry geometry;

    private EmfDeserializer deserializer;

    IfcModelInterface data;
    InputStream inputStream;

    public EMFIfcParser() {
        File homeDir = new File("bimserverHome");
        File tempDir = new File(homeDir, "tmp");
        if (!tempDir.exists()) tempDir.mkdirs();

        PluginM pluginManager = new PluginM();
        pluginManager.loadPluginsFromCurrentClassloader();
        pluginManager.initAllLoadedPlugins();
        enginePlugin = pluginManager.getAllIfcEnginePlugins(true).iterator().next();
        try {
            deserializer = pluginManager.getFirstDeserializer("ifc", true).createDeserializer();
            deserializer.init(pluginManager.requireSchemaDefinition());
        } catch (PluginException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void setInput(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    void lazyLoad() {
        if (data == null) try {
            readStream(); // lazy engine creation necessare, cause plugin init is not synched
        } catch (IfcEngineException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (DeserializeException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void readStream() throws IfcEngineException, DeserializeException, IOException {
        if (engine == null) {
            engine = enginePlugin.createIfcEngine();
            engine.init();
        }
        byte[] bytes = IOUtils.toByteArray(inputStream); // todo: save memory by branching the stream with TeeInputStream
        engineModel = engine.openModel(bytes);
        engineModel.setPostProcessing(true);
        geometry = engineModel.finalizeModelling(engineModel.initializeModelling());
        data = deserializer.read(new ByteArrayInputStream(bytes), "?", true, 16*58);
        adjustRelations();
    }

    private void adjustRelations() {
        // due to http://code.google.com/p/bimserver/wiki/Known_issues
        for(EObject eObject: data.getAllWithSubTypes(IfcRelContainedInSpatialStructure.class)){
            IfcRelContainedInSpatialStructure relation = (IfcRelContainedInSpatialStructure)eObject;
            for(IfcProduct product :relation.getRelatedElements()){
                if(product instanceof IfcElement){
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
        return new EngineEObject(data.get(objectID), engineModel, geometry);
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
                objectGeometry.vertizes = new ArrayList<Float>(visProps.getPrimitiveCount() * 9); // TODO: optimize memory: retain index!
                objectGeometry.normals = new ArrayList<Float>(visProps.getPrimitiveCount() * 9); // TODO: optimize memory: retain index!
                for (int i = visProps.getStartIndex(); i < visProps.getStartIndex() + visProps.getPrimitiveCount() * 3; i++) {
                    int index = geometry.getIndex(i) * 3;
                    objectGeometry.normals.add(geometry.getNormal(index));
                    objectGeometry.vertizes.add(geometry.getVertex(index));
                    objectGeometry.normals.add(geometry.getNormal(index + 1));
                    objectGeometry.vertizes.add(geometry.getVertex(index + 1));
                    objectGeometry.normals.add(geometry.getNormal(index + 2));
                    objectGeometry.vertizes.add(geometry.getVertex(index + 2));
                }
            } catch (IfcEngineException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            return objectGeometry;
        }
    }

    public class PluginM extends PluginManager {
        @Override
        public String getCompleteClassPath() {
            URL[] allUrls = ((URLClassLoader) getClass().getClassLoader()).getURLs();
            String[] allPAths = new String[allUrls.length];
            for (int i = 0; i < allUrls.length; i++) {
                allPAths[i] = allUrls[i].getPath();
            }

            return StringUtils.join(allPAths, ";");
        }
    }

    public static class Geometry {
        public List<Float> vertizes;
        public List<Float> normals;
    }


}
