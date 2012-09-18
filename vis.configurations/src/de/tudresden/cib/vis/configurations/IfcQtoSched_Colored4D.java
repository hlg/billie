package de.tudresden.cib.vis.configurations;

import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser;
import de.tudresden.cib.vis.data.multimodel.LinkedObject;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.scene.java3d.Java3dBuilder;
import de.tudresden.cib.vis.scene.java3d.Java3dFactory;

import javax.media.j3d.BranchGroup;

public class IfcQtoSched_Colored4D extends Configuration<LinkedObject<EMFIfcParser.EngineEObject>, Java3dFactory.Java3DGraphObject, BranchGroup> {

    public IfcQtoSched_Colored4D(DataAccessor<LinkedObject<EMFIfcParser.EngineEObject>> data){
        super(data, new Java3dFactory(), new Java3dBuilder());
    }

    public IfcQtoSched_Colored4D(Mapper<LinkedObject<EMFIfcParser.EngineEObject>, Java3dFactory.Java3DGraphObject, BranchGroup> mapper){
        super(mapper);
    }

    @Override
    public void config() {
        // todo
    }
}
