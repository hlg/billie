package de.tudresden.cib.vis.data.bimserver;

import de.tudresden.cib.vis.data.DataAccessException;
import org.bimserver.models.ifc2x3tc1.*;
import org.bimserver.plugins.PluginException;
import org.bimserver.plugins.PluginManager;
import org.bimserver.plugins.deserializers.DeserializeException;
import org.bimserver.plugins.deserializers.EmfDeserializer;
import org.bimserver.plugins.serializers.IfcModelInterface;

import java.io.InputStream;

public class EMFIfcPlainParser {
    EmfDeserializer deserializer;
    IfcModelInterface data;

    public EMFIfcPlainParser(PluginManager pluginManager) throws DataAccessException {
        try {
            deserializer = pluginManager.getFirstDeserializer("ifc", true).createDeserializer();
            deserializer.init(pluginManager.requireSchemaDefinition());
        } catch (PluginException e) {
            throw new DataAccessException(e);
        }
    }

    public void read(InputStream inputStream, final long size) throws DataAccessException {
        try {
            data = deserializer.read(inputStream, "?", true, 16 * 58);
        } catch (DeserializeException e) {
            throw new DataAccessException(e);
        }
    }

    void adjustRelations() {
        // due to http://code.google.com/p/bimserver/wiki/Known_issues
        for (IfcRelContainedInSpatialStructure relation : data.getAllWithSubTypes(IfcRelContainedInSpatialStructure.class)) {
            for (IfcProduct product : relation.getRelatedElements()) {
                if (product instanceof IfcElement) ((IfcElement) product).getContainedInStructure().add(relation);
            }
        }

        for(IfcRelAssociates relation : data.getAllWithSubTypes(IfcRelAssociates.class)){
            for (IfcRoot root: relation.getRelatedObjects()){
                if(root instanceof IfcObjectDefinition) ((IfcObjectDefinition)(root)).getHasAssociations().add(relation);
            }
        }
    }

}
