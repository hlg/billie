package data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EMFIfcAccessor extends IndexedDataAccessor<EMFIfcParser.EngineEObject> {

    Map<String,EMFIfcParser.EngineEObject> wrappedData;
    private String namespace = "";

    private EMFIfcParser parser;


    public EMFIfcAccessor() {
        parser = new EMFIfcParser();
    }

    public Iterator<EMFIfcParser.EngineEObject> iterator() {
        parser.lazyLoad();
        return parser.getIterator();
    }

    public void setInput(File file) throws IOException {
        parser.inputStream = new FileInputStream(file);
    }

    public void index() {
        parser.lazyLoad();
        parser.data.indexGuids();
        wrappedData = new HashMap<String, EMFIfcParser.EngineEObject>();
    }

    public EMFIfcParser.EngineEObject getIndexed(String objectID) {
        parser.lazyLoad();
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

    public void setInput(File file, String namespace) throws IOException {
        setInput(file);
        this.namespace = namespace + "::";
    }

}
