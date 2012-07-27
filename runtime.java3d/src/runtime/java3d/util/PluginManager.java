package runtime.java3d.util;

import nl.tue.buildingsmart.emf.BuildingSmartLibrarySchemaPlugin;
import org.apache.commons.lang.StringUtils;
import org.bimserver.ifc.step.deserializer.IfcStepDeserializerPlugin;
import org.bimserver.ifc.step.serializer.IfcStepSerializerPlugin;
import org.bimserver.ifcengine.TNOIfcEnginePlugin;
import org.bimserver.plugins.PluginException;
import org.bimserver.plugins.deserializers.DeserializerPlugin;
import org.bimserver.plugins.ifcengine.IfcEnginePlugin;
import org.bimserver.plugins.schema.SchemaPlugin;
import org.bimserver.plugins.serializers.SerializerPlugin;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * PluginMananager which doesn't use the dynamic plugin loading methods of the TNO Bimserver
 *
 * @author Helga Tauscher
 */
public class PluginManager extends org.bimserver.plugins.PluginManager {

    public void staticLoadPlugins() throws PluginException {
        loadPlugin(DeserializerPlugin.class, null, null, new IfcStepDeserializerPlugin());
        loadPlugin(SerializerPlugin.class, null, null, new IfcStepSerializerPlugin());
        loadPlugin(SchemaPlugin.class, null, null, new BuildingSmartLibrarySchemaPlugin());
        loadPlugin(IfcEnginePlugin.class, null, null, new TNOIfcEnginePlugin());
    }

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
