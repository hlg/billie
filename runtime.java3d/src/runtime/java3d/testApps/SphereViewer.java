package runtime.java3d.testApps;

import com.sun.j3d.loaders.*;
import com.sun.j3d.utils.geometry.Sphere;
import runtime.java3d.viewers.SimpleViewer;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Shape3D;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;

public class SphereViewer {

    public static void main(String[] args) throws Exception {
        SimpleViewer viewer = new SimpleViewer(new SphereLoader());
        viewer.run(new DummyReader());
    }

    private static class DummyReader extends Reader {
        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            return 0;
        }

        @Override
        public void close() throws IOException {
        }
    }

    private static class SphereLoader implements Loader {
        public Scene load(String s) throws FileNotFoundException, IncorrectFormatException, ParsingErrorException {
            SceneBase dummy = new SceneBase();
            BranchGroup main = new BranchGroup();
            Sphere sphere = new Sphere();
            sphere.getShape().setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
            main.addChild(sphere);
            dummy.setSceneGroup(main);
            return dummy;
        }

        public Scene load(URL url) throws FileNotFoundException, IncorrectFormatException, ParsingErrorException {
            return load("");
        }

        public Scene load(Reader reader) throws FileNotFoundException, IncorrectFormatException, ParsingErrorException {
            return load("");
        }

        public void setBaseUrl(URL url) {
        }

        public void setBasePath(String s) {
        }

        public URL getBaseUrl() {
            return null;
        }

        public String getBasePath() {
            return null;
        }

        public void setFlags(int i) {
        }

        public int getFlags() {
            return 0;
        }
    }
}
