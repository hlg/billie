package data;

import cib.lib.bimserverViewer.util.PluginManager;
import nl.tue.buildingsmart.emf.BuildingSmartLibrarySchemaPlugin;
import org.bimserver.emf.IdEObject;
import org.bimserver.ifc.step.deserializer.IfcStepDeserializerPlugin;
import org.bimserver.ifcengine.CppIfcEnginePlugin;
import org.bimserver.models.ifc2x3.IfcProduct;
import org.bimserver.plugins.PluginException;
import org.bimserver.plugins.deserializers.DeserializeException;
import org.bimserver.plugins.deserializers.DeserializerPlugin;
import org.bimserver.plugins.deserializers.EmfDeserializer;
import org.bimserver.plugins.ifcengine.*;
import org.bimserver.plugins.schema.SchemaPlugin;
import org.bimserver.plugins.serializers.IfcModelInterface;
import org.eclipse.emf.ecore.EObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EMFIfcAccessor implements DataAccessor<EMFIfcAccessor.EngineEObject> {

    IfcModelInterface data;
    private IfcEngineModel engineModel;
    private IfcEngineGeometry geometry;
    private IfcEnginePlugin enginePlugin;
    private EmfDeserializer deserializer;
    private InputStream inputStream;
    private long inputSize;
    private IfcEngine engine;


    public EMFIfcAccessor(){
        init();
    }

    @Override
    protected void finalize() throws Throwable {
        engineModel.close();
        engine.close();
    }

    public void setInput(InputStream inputStream, long size) {
        this.inputStream = inputStream;
        this.inputSize = size;
    }

    private void init(){
        File homeDir = new File("bimserverHome");
        File tempDir = new File(homeDir, "tmp");
        if (!tempDir.exists()) tempDir.mkdirs();

        PluginManager pluginManager = new PluginManager(homeDir, ".","D:\\Nutzer\\helga\\dev\\BimserverViewer\\lib\\bimserver-client-lib-1.1.0-2012-02-20\\dep", "D:\\Nutzer\\helga\\dev\\BimserverViewer\\lib\\bimserver-client-lib-1.1.0-2012-02-20\\lib");
        try {
            // pluginManager.staticLoadPlugins();
            pluginManager.loadPlugin(DeserializerPlugin.class, null, null, new IfcStepDeserializerPlugin());
            pluginManager.loadPlugin(SchemaPlugin.class, null, null, new BuildingSmartLibrarySchemaPlugin());
            pluginManager.loadPlugin(IfcEnginePlugin.class, null, null, new CppIfcEnginePlugin());

            pluginManager.initAllLoadedPlugins();
            enginePlugin = pluginManager.getAllIfcEnginePlugins(true).iterator().next();
            deserializer = pluginManager.getFirstDeserializer("ifc", true).createDeserializer();
            deserializer.init(pluginManager.requireSchemaDefinition());
        } catch (PluginException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void readStream() throws IfcEngineException, DeserializeException, IOException {
        if(engine == null) engine = enginePlugin.createIfcEngine();
        byte[] bytes = new byte[(int)inputSize];
        inputStream.read(bytes);
        engineModel = engine.openModel(new ByteArrayInputStream(bytes), (int)inputSize);
        engineModel.setPostProcessing(true);
        geometry = engineModel.finalizeModelling(engineModel.initializeModelling());
        data = deserializer.read(new ByteArrayInputStream(bytes), "?", true, 16);
    }

    public Iterator<EngineEObject> iterator() {
        if(data==null) try {
            readStream(); // lazy engine creation necessare, cause plugin init is not synched
        } catch (IfcEngineException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (DeserializeException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return new EngineIterator(data.getValues().iterator());
    }

    public void setInput(File file) throws IOException {
        this.inputStream = new FileInputStream(file);
        this.inputSize = file.length();
    }

    class EngineIterator implements Iterator<EngineEObject> {

        private Iterator<IdEObject> baseIterator;

        EngineIterator(Iterator<IdEObject> baseIterator){
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

        EngineEObject(IdEObject object){
            this.idEObject = object;
        }

        public EObject getObject(){
            return idEObject;
        }

        public Geometry getGeometry() {
            if (!(idEObject instanceof IfcProduct)) return null;
            IfcEngineInstance instance = null;
            Geometry objectGeometry = new Geometry();
            try {
                instance = engineModel.getInstanceFromExpressId((int) idEObject.getOid());
                IfcEngineInstanceVisualisationProperties visProps = instance.getVisualisationProperties();
                objectGeometry.vertizes = new ArrayList<Float>(visProps.getPrimitiveCount()*9); // TODO: optimize memory: retain index!
                objectGeometry.normals = new ArrayList<Float>(visProps.getPrimitiveCount()*9); // TODO: optimize memory: retain index!
                for(int i = visProps.getStartIndex(); i < visProps.getStartIndex() + visProps.getPrimitiveCount()*3; i++){
                    int index = geometry.getIndex(i)*3;
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
}
