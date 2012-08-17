package de.tudresden.cib.vis.runtime.java3d.loaders;

import com.sun.j3d.loaders.SceneBase;
import org.bimserver.models.ifc2x3tc1.IfcRoot;

import javax.media.j3d.Shape3D;
import java.util.Hashtable;

/**
 * @author helga
 */
public class IfcScene extends SceneBase {
    Hashtable<IfcRoot, Shape3D> visMap = new Hashtable<IfcRoot, Shape3D>();

    public void addNamedObject(IfcRoot name, Shape3D object) {
        visMap.put(name, object);
    }

    @Override
    public Hashtable<IfcRoot, Shape3D> getNamedObjects() {
        return visMap;
    }
}
