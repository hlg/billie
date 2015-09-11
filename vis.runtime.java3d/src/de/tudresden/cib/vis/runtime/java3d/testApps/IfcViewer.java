package de.tudresden.cib.vis.runtime.java3d.testApps;

import de.tudresden.cib.vis.data.bimserver.SimplePluginManager;
import de.tudresden.cib.vis.runtime.java3d.loaders.BimserverJava3dLoader;
import de.tudresden.cib.vis.runtime.java3d.viewers.SimpleViewer;

import java.io.FileNotFoundException;

public class IfcViewer {

    public static void main(String[] args) throws FileNotFoundException {
        SimplePluginManager pm = new SimplePluginManager();
        SimpleViewer ifcViewer = new SimpleViewer(new BimserverJava3dLoader(pm));
        ifcViewer.chooseAndRun(args.length > 0 ? args[0] : null, "ifc", false);
    }
}
