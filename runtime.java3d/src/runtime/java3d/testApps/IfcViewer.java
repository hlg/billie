package runtime.java3d.testApps;

import runtime.java3d.loaders.BimserverJava3dLoader;
import runtime.java3d.viewers.SimpleViewer;

import java.io.Reader;

public class IfcViewer {

    public static void main(String[] args) throws Exception {
        SimpleViewer ifcViewer = new SimpleViewer(new BimserverJava3dLoader());
        Reader file = ifcViewer.chooseFile(args.length > 0 ? args[0] : null);
        ifcViewer.run(file);
    }
}
