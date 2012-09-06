package de.tudresden.cib.vis.sampleApps;

import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.Loader;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.mapping.TargetCreationException;
import de.tudresden.cib.vis.runtime.java3d.loaders.IfcScene;
import de.tudresden.cib.vis.scene.VisFactory2D;
import de.tudresden.cib.vis.scene.java3d.Java3dBuilder;
import de.tudresden.cib.vis.scene.java3d.Java3dFactory;
import org.apache.commons.io.input.ReaderInputStream;

import javax.media.j3d.BranchGroup;
import java.io.*;
import java.net.URL;

public class MappedJ3DLoader<E> implements Loader {
    protected Mapper<E, Java3dFactory.Java3DGraphObject, BranchGroup> mapper;
    protected DataAccessor<E> data;

    public MappedJ3DLoader(DataAccessor<E> data) {
        this.data = data;
        this.mapper = new Mapper<E, Java3dFactory.Java3DGraphObject, BranchGroup>(data, new Java3dFactory(), new Java3dBuilder());
    }

    public <S extends E, T extends VisFactory2D.GraphObject> void addMapping(PropertyMap<S, T> propertyMap) {
        mapper.addMapping(propertyMap);
    }

    public Scene load(String s) throws FileNotFoundException, IncorrectFormatException, ParsingErrorException {
        return loadScene(new FileInputStream(s));
    }

    private Scene loadScene(InputStream inputStream) throws FileNotFoundException {
        IfcScene result = null;
        try {
            data.read(inputStream);
            result = new IfcScene();
            result.setSceneGroup(mapper.map());
            mapper.getSceneManager().animate();
        } catch (IOException e) {
            throw new FileNotFoundException(e.getMessage());
        } catch (TargetCreationException e) {
            throw new ParsingErrorException(e.getMessage());
        }
        return result;
    }

    public Scene load(URL url) throws FileNotFoundException, IncorrectFormatException, ParsingErrorException {
        try {
            return loadScene(url.openStream());  //To change body of implemented methods use File | Settings | File Templates.
        } catch (IOException e) {
            throw new FileNotFoundException(e.getMessage());
        }
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

    public Mapper<E, Java3dFactory.Java3DGraphObject, BranchGroup> getMapper() {
        return mapper;
    }
}
