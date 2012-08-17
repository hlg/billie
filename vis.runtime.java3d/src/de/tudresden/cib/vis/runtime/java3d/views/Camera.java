package de.tudresden.cib.vis.runtime.java3d.views;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Group;
import javax.media.j3d.View;

/**
 * @author helga
 */
public interface Camera {

    BranchGroup getViewBranch();
    void zoomToExtent(Group scene, float scale);
   View getView();

}
