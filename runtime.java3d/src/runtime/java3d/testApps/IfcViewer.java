package runtime.java3d.testApps;

import runtime.java3d.loaders.BimserverJava3dLoader;
import runtime.java3d.viewers.SimpleViewer;

import java.io.File;
import java.io.FileReader;

public class IfcViewer {

    public static void main(String[] args) throws Exception {
        SimpleViewer ifcViewer = new SimpleViewer(new BimserverJava3dLoader());
        File file = ifcViewer.chooseFile(args.length > 0 ? args[0] : null, "ifc");
        ifcViewer.run(new FileReader(file));
    }
}
