package de.tudresden.cib.vis.data.bimserver;

import de.tudresden.cib.vis.data.DataAccessException;
import de.tudresden.cib.vis.data.IndexedDataAccessor;
import org.bimserver.models.ifc2x3tc1.IfcRoot;
import org.bimserver.plugins.PluginManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EMFIfcGeometricAccessor extends IndexedDataAccessor<EMFIfcParser.EngineEObject> {

    Map<String,EMFIfcParser.EngineEObject> indexedData;
    private EMFIfcParser parser;

    public EMFIfcGeometricAccessor(PluginManager pluginManager, boolean forkInput) throws DataAccessException {
        parser = new EMFIfcParser(pluginManager, forkInput);
    }

    public EMFIfcGeometricAccessor(PluginManager pluginManager, InputStream input, long size) throws IOException, DataAccessException {
        this(pluginManager, true);
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
        indexedData = new HashMap<String, EMFIfcParser.EngineEObject>();
        for(EMFIfcParser.EngineEObject wrapped: parser.wrapped) {
            if(wrapped.getObject() instanceof IfcRoot) indexedData.put(((IfcRoot)wrapped.getObject()).getGlobalId().getWrappedValue(), wrapped);
        }
    }

    public EMFIfcParser.EngineEObject getIndexed(String objectID) {
        if (objectID.contains("::")) {
            String[] idParts = objectID.split("::");
            assert idParts[0].equals(namespace);
            objectID = idParts[1];
        }
        return indexedData.get(objectID);
    }

}
