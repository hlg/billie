package de.tudresden.cib.vis.sampleApps;

import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.Loader;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import de.tudresden.cib.vis.data.DataAccessException;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.filter.Condition;
import de.tudresden.cib.vis.mapping.Configuration;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.TargetCreationException;
import de.tudresden.cib.vis.runtime.java3d.loaders.IfcScene;
import de.tudresden.cib.vis.scene.SceneManager;
import de.tudresden.cib.vis.scene.java3d.Java3dBuilder;
import de.tudresden.cib.vis.scene.java3d.Java3dFactory;

import javax.media.j3d.BranchGroup;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;

public class MappedJ3DLoader<E> implements Loader {
    protected Mapper<E, Condition<E>, Java3dFactory.Java3DGraphObject, BranchGroup> mapper;
    protected DataAccessor<E, Condition<E>> data;
    private Configuration<E, Condition<E>> configuration;

    public MappedJ3DLoader(DataAccessor<E, Condition<E>> data, Configuration<E, Condition<E>> config) {
        this.data = data;
        this.mapper = Java3dBuilder.createMapper(data);
        this.configuration = config;
    }

    public Scene load(String s) throws FileNotFoundException, IncorrectFormatException, ParsingErrorException {
        try {
            data.read(new File(s).toURI().toURL());
        } catch (IOException e) {
            throw new FileNotFoundException(e.getMessage());
        } catch (DataAccessException e) {
            throw new ParsingErrorException(e.getMessage());
        }
        return loadScene();
    }

    private Scene loadScene() throws FileNotFoundException {
        IfcScene result;
        try {
            result = new IfcScene();
            SceneManager<E,BranchGroup> sceneManager = mapper.map(configuration);
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
            data.read(url);
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

    public Mapper<E, Condition<E>, Java3dFactory.Java3DGraphObject, BranchGroup> getMapper() {
        return mapper;
    }
}
