package de.tudresden.cib.vis.data.bimserver;

import de.tudresden.cib.vis.data.DataAccessException;
import de.tudresden.cib.vis.data.IndexedDataAccessor;
import org.bimserver.plugins.PluginManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EMFIfcGeometricAccessor extends IndexedDataAccessor<EMFIfcParser.EngineEObject> {

    Map<String,EMFIfcParser.EngineEObject> wrappedData;
    private EMFIfcParser parser;

    public EMFIfcGeometricAccessor(PluginManager pluginManager) throws DataAccessException {
        parser = new EMFIfcParser(pluginManager);
    }

    public EMFIfcGeometricAccessor(SimplePluginManager simplePluginManager, InputStream input, long size) throws IOException, DataAccessException {
        this(simplePluginManager);
        read(input, size);
    }

    public void read(InputStream inputStream, long size) throws IOException, DataAccessException {
        parser.read(inputStream, size);
    }

    @Override
    public void readFromFolder(File directory) {
        throw new UnsupportedOperationException();
    }

    public Iterator<EMFIfcParser.EngineEObject> iterator() {
        return parser.getIterator();
    }

    public void index() {
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
