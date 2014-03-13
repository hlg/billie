package de.tudresden.cib.vis.data.bimserver;

import de.tudresden.cib.vis.data.DataAccessException;
import de.tudresden.cib.vis.data.IndexedDataAccessor;
import de.tudresden.cib.vis.filter.Condition;
import de.tudresden.cib.vis.filter.ConditionFilter;
import org.bimserver.models.ifc2x3tc1.IfcRoot;
import org.bimserver.plugins.PluginManager;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EMFIfcGeometricAccessor extends IndexedDataAccessor<EMFIfcParser.EngineEObject, Condition<EMFIfcParser.EngineEObject>> {

    private final ConditionFilter<EMFIfcParser.EngineEObject> filter;
    Map<String,EMFIfcParser.EngineEObject> indexedData;
    private EMFIfcParser parser;

    public EMFIfcGeometricAccessor(PluginManager pluginManager, boolean forkInput) throws DataAccessException {
        parser = new EMFIfcParser(pluginManager, forkInput);
        filter = new ConditionFilter<EMFIfcParser.EngineEObject>();
    }

    public EMFIfcGeometricAccessor(PluginManager pluginManager, URL url) throws IOException, DataAccessException {
        this(pluginManager, true);
        read(url);
    }

    public void read(URL url) throws IOException, DataAccessException {
        URLConnection connection = url.openConnection();
        parser.read(connection.getInputStream(), connection.getContentLength());
    }

    @Override
    public Iterable<? extends EMFIfcParser.EngineEObject> filter(Condition<EMFIfcParser.EngineEObject> condition) {
        return filter.filter(condition, this);
    }

    @Override
    public Condition<EMFIfcParser.EngineEObject> getDefaultCondition() {
        return new Condition<EMFIfcParser.EngineEObject>() {
            @Override
            public boolean matches(EMFIfcParser.EngineEObject data) {
                return true;
            }
        };
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
