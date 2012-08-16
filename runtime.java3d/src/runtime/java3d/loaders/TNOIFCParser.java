package runtime.java3d.loaders;

import org.apache.commons.io.IOUtils;
import org.bimserver.plugins.PluginException;
import org.bimserver.plugins.PluginManager;
import org.bimserver.plugins.deserializers.DeserializeException;
import org.bimserver.plugins.deserializers.EmfDeserializer;
import org.bimserver.plugins.ifcengine.*;
import org.bimserver.plugins.serializers.IfcModelInterface;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TNOIFCParser {
    private IfcEngine ifcEngine;
    private IfcEnginePlugin ifcEnginePlugin;
    private IfcEngineModel ifcEngineModel;
    private EmfDeserializer deserializer;

    public TNOIFCParser(PluginManager pluginManager) throws PluginException, IOException {
        pluginManager.initAllLoadedPlugins();
        ifcEnginePlugin = pluginManager.requireIfcEngine();
        deserializer = pluginManager.getFirstDeserializer("ifc", true).createDeserializer();
        deserializer.init(pluginManager.requireSchemaDefinition());
    }

    TNOIfcModel loadData(InputStream inputStream) throws IOException, DeserializeException, IfcEngineException {
        if (ifcEngine == null) {
            ifcEngine = ifcEnginePlugin.createIfcEngine();
            ifcEngine.init();
        }
        // LOGGER.info(new Date(System.currentTimeMillis()) + " starting building scene graph");
        byte[] bytes;
        bytes = IOUtils.toByteArray(inputStream);
        // LOGGER.info(new Date(System.currentTimeMillis()) + " finished deserializing");
        ifcEngineModel = ifcEngine.openModel(bytes);
        ifcEngineModel.setPostProcessing(true);
        return new TNOIfcModel(
                ifcEngineModel.finalizeModelling(ifcEngineModel.initializeModelling()),
                deserializer.read(new ByteArrayInputStream(bytes), "?", true, 16 * 58),
                ifcEngineModel
        );
    }

    public void dispose() throws IfcEngineException {
        ifcEngineModel.close();
        ifcEngine.close();
    }

    class TNOIfcModel {
        IfcEngineGeometry geometry;
        IfcModelInterface model;
        IfcEngineModel geomModel;

        public TNOIfcModel(IfcEngineGeometry ifcEngineGeometry, IfcModelInterface ifcModel, IfcEngineModel ifcEngineModel) {
            geometry = ifcEngineGeometry;
            model = ifcModel;
            geomModel = ifcEngineModel;
        }
    }
}
