package configurations;

import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.Loader;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import org.apache.commons.io.input.ReaderInputStream;
import runtime.java3d.loaders.IfcScene;
import visMapping.data.DataAccessor;
import visMapping.mapping.Mapper;
import visMapping.mapping.TargetCreationException;
import visMapping.visualization.VisBuilder;
import visMapping.visualization.VisFactory2D;
import visualization.java3d.Java3dBuilder;
import visualization.java3d.Java3dFactory;

import javax.media.j3d.BranchGroup;
import java.io.*;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public abstract class MappedJ3DLoader<T> implements Loader {
    protected Mapper<T> mapper;
    protected DataAccessor<T> data;

    abstract void configMapping();

    abstract void load(InputStream inputStream) throws IOException;

    public Scene load(String s) throws FileNotFoundException, IncorrectFormatException, ParsingErrorException {
        return loadScene(new FileInputStream(s));
    }

    private Scene loadScene(InputStream inputStream) {
        IfcScene result = null;
        try {
            load(inputStream);
            initMapper();
            configMapping();
            result = new IfcScene();
            result.setSceneGroup((BranchGroup) mapper.map());
            mapper.animate();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (TargetCreationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return result;
    }

    public Scene load(URL url) throws FileNotFoundException, IncorrectFormatException, ParsingErrorException {
        try {
            return loadScene(url.openStream());  //To change body of implemented methods use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    public Scene load(Reader reader) throws FileNotFoundException, IncorrectFormatException, ParsingErrorException {
        return loadScene(new ReaderInputStream(reader));
    }

    public void setBaseUrl(URL url) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setBasePath(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public URL getBaseUrl() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getBasePath() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setFlags(int i) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getFlags() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected void initMapper() {
        VisBuilder builder = new Java3dBuilder();
        VisFactory2D factory = new Java3dFactory();
        mapper = new Mapper<T>(data, factory, builder);
    }

    protected File unzip(InputStream inputStream) throws IOException {
        File tmp = new File("tmpunzip");
        tmp.mkdir();
        tmp.deleteOnExit();
        ZipInputStream zip = new ZipInputStream(inputStream);
        ZipEntry zipEntry = zip.getNextEntry();
        while (zipEntry != null) {
            File file = new File(tmp, zipEntry.getName());
            if (zipEntry.isDirectory()) file.mkdirs();
            else {
                file.getParentFile().mkdirs();
                FileOutputStream fos = new FileOutputStream(file);
                for (int c = zip.read(); c != -1; c = zip.read()) {
                    fos.write(c);
                }
            }
            zipEntry = zip.getNextEntry();
        }
        return tmp;
    }
}
