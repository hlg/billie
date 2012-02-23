package visualization;

import cib.lib.bimserverViewer.colorTime.TypeAppearance;
import mapping.PropertyMap;
import org.apache.commons.lang.ArrayUtils;
import org.eclipse.draw2d.IFigure;

import javax.media.j3d.*;
import java.util.List;

public class Java3dFactory extends VisFactory3D {

    Appearance defaultAppearance;

    public Java3dFactory(){
        defaultAppearance = TypeAppearance.INACTIVE.createAppearance();
    }

    @Override
    protected PropertyMap.Provider<Rectangle> setRectangleProvider() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected PropertyMap.Provider<Label> setLabelProvider() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected PropertyMap.Provider<Polyeder> setPolyederProvider() {
        return new PropertyMap.Provider<Polyeder>() {
            public Polyeder create() {
                return new Java3DPolyeder();
            }
        };
    }

    public interface Java3DGraphObject extends GraphObject {} // marker interface

    private class Java3DPolyeder extends Shape3D implements Polyeder, Java3DGraphObject {

        Java3DPolyeder(){
            setAppearance(defaultAppearance);
        }

        // TODO: how to make sure Java3DGraphObjects implement GraphObject and Java3D Node?
        public void setVertizes(List<Float> vertizes) {
            TriangleArray geometry = getGeometryWithDefault(vertizes.size() / 3);
            float[] verts = ArrayUtils.toPrimitive(vertizes.toArray(new Float[vertizes.size()]));
            geometry.setCoordinates(0, verts);
        }

        public void setNormals(List<Float> normals) {
            TriangleArray geometry = getGeometryWithDefault(normals.size() / 3);
            float[] verts = ArrayUtils.toPrimitive(normals.toArray(new Float[normals.size()]));
            geometry.setNormals(0, verts);
        }

        private TriangleArray getGeometryWithDefault(int size) {
            TriangleArray geometry = (TriangleArray) getGeometry();
            if(geometry==null){
                geometry = new TriangleArray(size, GeometryArray.NORMALS | GeometryArray.COORDINATES);
                this.setGeometry(geometry);
            }
            return geometry;
        }
    }

}
