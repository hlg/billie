package data;

import com.google.common.base.Strings;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.bimserver.emf.IdEObject;
import org.bimserver.models.ifc2x3.IfcElement;
import org.bimserver.models.ifc2x3.IfcProduct;
import org.bimserver.models.ifc2x3.IfcRelContainedInSpatialStructure;
import org.bimserver.plugins.PluginDescriptor;
import org.bimserver.plugins.PluginException;
import org.bimserver.plugins.PluginManager;
import org.bimserver.plugins.deserializers.DeserializeException;
import org.bimserver.plugins.deserializers.EmfDeserializer;
import org.bimserver.plugins.ifcengine.*;
import org.bimserver.plugins.serializers.IfcModelInterface;
import org.eclipse.emf.ecore.EObject;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class EMFIfcAccessor extends IndexedDataAccessor<EMFIfcAccessor.EngineEObject> {

    IfcModelInterface data;
    Map<String, EngineEObject> wrappedData;
    private IfcEngineModel engineModel;
    private IfcEngineGeometry geometry;
    private IfcEnginePlugin enginePlugin;
    private EmfDeserializer deserializer;
    private InputStream inputStream;
    private IfcEngine engine;
    private String namespace = "";


    public EMFIfcAccessor() {
        init();
    }

    @Override
    protected void finalize() throws Throwable {
        engineModel.close();
        engine.close();
    }

    public void setInput(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    private void init() {
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

    public Iterator<EngineEObject> iterator() {
        layzLoad();
        return new EngineIterator(data.getValues().iterator());
    }

    private void layzLoad() {
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

    public void setInput(File file) throws IOException {
        this.inputStream = new FileInputStream(file);
    }

    public void index() {
        layzLoad();
        data.indexGuids();
        wrappedData = new HashMap<String, EngineEObject>();
    }

    public EngineEObject getIndexed(String objectID) {
        layzLoad();
        if (objectID.contains("::")) {
            String[] idParts = objectID.split("::");
            assert idParts[0].equals(namespace);
            objectID = idParts[1];
        }
        if (wrappedData.containsKey(objectID))
            return wrappedData.get(objectID);
        else {
            EngineEObject wrapped = new EngineEObject(data.get(objectID));
            wrappedData.put(objectID, wrapped);
            return wrapped;
        }
    }

    public void setInput(File file, String namespace) throws IOException {
        setInput(file);
        this.namespace = namespace + "::";
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
            return new EngineEObject(baseIterator.next());
        }

        public void remove() {
            baseIterator.remove();
        }
    }

    public class EngineEObject {
        private IdEObject idEObject;

        EngineEObject(IdEObject object) {
            this.idEObject = object;
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

    public class Geometry {
        public List<Float> vertizes;
        public List<Float> normals;
    }

    public class PluginM extends PluginManager {
        @Override
        public String getCompleteClassPath() {
            URL[] allUrls = ((URLClassLoader)getClass().getClassLoader()).getURLs();
            String[] allPAths = new String[allUrls.length];
            for(int i=0; i<allUrls.length; i++){
                allPAths[i] = allUrls[i].getPath();
            }

            return StringUtils.join(allPAths, ";");
        }
    }
}
