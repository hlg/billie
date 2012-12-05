package de.tudresden.cib.vis.sampleApps;

import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.Loader;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import de.tudresden.cib.vis.data.DataAccessException;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.mapping.TargetCreationException;
import de.tudresden.cib.vis.runtime.java3d.loaders.IfcScene;
import de.tudresden.cib.vis.scene.SceneManager;
import de.tudresden.cib.vis.scene.VisFactory2D;
import de.tudresden.cib.vis.scene.java3d.Java3dBuilder;
import de.tudresden.cib.vis.scene.java3d.Java3dFactory;

import javax.media.j3d.BranchGroup;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
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
        try {
            data.read(new File(s));
        } catch (IOException e) {
            throw new FileNotFoundException(e.getMessage());
        } catch (DataAccessException e) {
            throw new ParsingErrorException(e.getMessage());
        }
        return loadScene();
    }

    private Scene loadScene() throws FileNotFoundException {
        IfcScene result = null;
        try {
            result = new IfcScene();
            SceneManager<E,BranchGroup> sceneManager = mapper.map();
            mapper = null; // release resources
            result.setSceneGroup(sceneManager.getScene());
            sceneManager.animate();
        } catch (TargetCreationException e) {
            throw new ParsingErrorException(e.getMessage());
        }
        return result;
    }

    public Scene load(URL url) throws FileNotFoundException, IncorrectFormatException, ParsingErrorException {
        try {
            data.read(url.openStream(), url.getFile().length());
            return loadScene();
        } catch (IOException e) {
            throw new FileNotFoundException(e.getMessage());
        } catch (DataAccessException e) {
            throw new ParsingErrorException(e.getMessage());
        }
    }

    public Scene load(Reader reader) throws FileNotFoundException, IncorrectFormatException, ParsingErrorException {
        throw new UnsupportedOperationException();
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
