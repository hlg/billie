package de.tudresden.cib.vis.data.bimserver;

import de.tudresden.cib.vis.data.DataAccessException;
import org.bimserver.models.ifc2x3tc1.IfcElement;
import org.bimserver.models.ifc2x3tc1.IfcProduct;
import org.bimserver.models.ifc2x3tc1.IfcRelContainedInSpatialStructure;
import org.bimserver.plugins.PluginException;
import org.bimserver.plugins.PluginManager;
import org.bimserver.plugins.deserializers.DeserializeException;
import org.bimserver.plugins.deserializers.EmfDeserializer;
import org.bimserver.plugins.serializers.IfcModelInterface;
import org.eclipse.emf.ecore.EObject;

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
        for (EObject eObject : data.getAllWithSubTypes(IfcRelContainedInSpatialStructure.class)) {
            IfcRelContainedInSpatialStructure relation = (IfcRelContainedInSpatialStructure) eObject;
            for (IfcProduct product : relation.getRelatedElements()) {
                if (product instanceof IfcElement) {
                    ((IfcElement) product).getContainedInStructure().add(relation);
                }
            }
        }
    }

}
