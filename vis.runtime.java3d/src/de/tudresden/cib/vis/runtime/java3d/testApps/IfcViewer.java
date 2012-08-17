package de.tudresden.cib.vis.runtime.java3d.testApps;

import de.tudresden.cib.vis.runtime.java3d.loaders.BimserverJava3dLoader;
import de.tudresden.cib.vis.runtime.java3d.util.PluginManager;
import de.tudresden.cib.vis.runtime.java3d.viewers.SimpleViewer;

import java.io.File;
import java.io.FileReader;

public class IfcViewer {

    public static void main(String[] args) throws Exception {
        PluginManager pm = new PluginManager();
        pm.loadPluginsFromCurrentClassloader();
        SimpleViewer ifcViewer = new SimpleViewer(new BimserverJava3dLoader(pm));
        File file = ifcViewer.chooseFile(args.length > 0 ? args[0] : null, "ifc");
        ifcViewer.run(new FileReader(file));
    }
}
