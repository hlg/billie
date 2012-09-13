package de.tudresden.cib.vis.runtime.java3d.testApps;

import de.tudresden.cib.vis.data.bimserver.SimplePluginManager;
import de.tudresden.cib.vis.runtime.java3d.loaders.BimserverJava3dLoader;
import de.tudresden.cib.vis.runtime.java3d.viewers.SimpleViewer;

import java.io.File;

public class IfcViewer {

    public static void main(String[] args) throws Exception {
        SimplePluginManager pm = new SimplePluginManager();
        pm.loadPluginsFromCurrentClassloader();
        SimpleViewer ifcViewer = new SimpleViewer(new BimserverJava3dLoader(pm));
        File file = ifcViewer.chooseFile(args.length > 0 ? args[0] : null, "ifc");
        ifcViewer.run(file.getPath());
    }
}
