package configurations;

import cib.lib.bimserverViewer.BimserverViewer;
import cib.lib.bimserverViewer.loaders.IfcScene;
import data.DataAccessor;
import mapping.Mapper;
import mapping.TargetCreationException;
import visualization.Java3dBuilder;
import visualization.Java3dFactory;
import visualization.VisBuilder;
import visualization.VisFactory2D;

import javax.media.j3d.BranchGroup;
import java.io.IOException;

public abstract class MappedBimserverViewer<T> extends BimserverViewer {
    protected Mapper<T> mapper;
    protected DataAccessor<T> data;

    void run() throws TargetCreationException, IOException {
        setupViews();
        loadFile();
        initMapper();
        configMapping();
        executeMapping();
        showScene();
    }

    abstract void configMapping();

    abstract void loadFile() throws IOException;

    private void executeMapping() throws TargetCreationException {
        scene = new IfcScene();
        scene.setSceneGroup((BranchGroup) mapper.map());
        mapper.animate();
    }

    protected void initMapper() {
        VisBuilder builder = new Java3dBuilder();
        VisFactory2D factory = new Java3dFactory();
        mapper = new Mapper<T>(data, factory, builder);
    }
}
