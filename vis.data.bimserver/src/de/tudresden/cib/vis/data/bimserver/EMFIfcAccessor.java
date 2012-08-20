package de.tudresden.cib.vis.data.bimserver;

import de.tudresden.cib.vis.data.IndexedDataAccessor;
import org.bimserver.plugins.PluginManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EMFIfcAccessor extends IndexedDataAccessor<EMFIfcParser.EngineEObject> {

    Map<String,EMFIfcParser.EngineEObject> wrappedData;
    private String namespace = "";

    private EMFIfcParser parser;


    public EMFIfcAccessor(PluginManager pluginManager) {
        parser = new EMFIfcParser(pluginManager);
    }

    public void setInput(InputStream inputStream) throws IOException {
        parser.setInput(inputStream);
    }

    public void setInput(InputStream inputStream, String namespace) throws IOException {
        setInput(inputStream);
        this.namespace = namespace + "::";
    }

    public Iterator<EMFIfcParser.EngineEObject> iterator() {
        parser.lazyLoad();
        return parser.getIterator();
    }

    public void index() {
        parser.lazyLoad();
        parser.data.indexGuids();
        wrappedData = new HashMap<String, EMFIfcParser.EngineEObject>();
    }

    public EMFIfcParser.EngineEObject getIndexed(String objectID) {
        if (objectID.contains("::")) {
            String[] idParts = objectID.split("::");
            assert idParts[0].equals(namespace);
            objectID = idParts[1];
        }
        if (wrappedData.containsKey(objectID))
            return wrappedData.get(objectID);
        else {
            EMFIfcParser.EngineEObject wrapped = parser.getWrappedObject(objectID);
            wrappedData.put(objectID, wrapped);
            return wrapped;
        }
    }

}
