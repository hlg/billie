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

public abstract class MappedBimserverViewer<T> extends BimserverViewer {
    protected Mapper<T> mapper;
    protected DataAccessor<T> data;

    void run() throws TargetCreationException {
        setupViews();
        loadFile();
        initMapper();
        configMapping();
        executeMapping();
        showScene();
    }

    abstract void configMapping();

    abstract void loadFile();

    private void executeMapping() throws TargetCreationException {
        scene = new IfcScene();
        scene.setSceneGroup((BranchGroup) mapper.map());
    }

    protected void initMapper() {
        VisBuilder builder = new Java3dBuilder();
        VisFactory2D factory = new Java3dFactory();
        mapper = new Mapper<T>(data, factory, builder);
    }
}
