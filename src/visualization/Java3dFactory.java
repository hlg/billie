package visualization;

import cib.lib.bimserverViewer.colorTime.TypeAppearance;
import mapping.PropertyMap;
import org.apache.commons.lang.ArrayUtils;
import org.eclipse.draw2d.IFigure;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import java.util.List;

public class Java3dFactory extends VisFactory3D {

    Appearance defaultAppearance;

    public Java3dFactory(){
        defaultAppearance = TypeAppearance.INACTIVE.createAppearance();
    }

    @Override
    protected PropertyMap.Provider<Rectangle> setRectangleProvider() {
        return null;  //TODO: rectangle on projection area
    }

    @Override
    protected PropertyMap.Provider<Label> setLabelProvider() {
        return null;  //TODO: label on projection area
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

        public void setColor(float R, float G, float B) {
            Appearance appearance = new Appearance();
            Color3f color3f = new Color3f(R,G,B);
            Material material = new Material(color3f, new Color3f(0f, 0f, 0f), color3f, color3f, 10f);
            material.setLightingEnable(true);
            appearance.setMaterial(material);
            PolygonAttributes pa = new PolygonAttributes();
            pa.setCullFace(PolygonAttributes.CULL_NONE);
            appearance.setPolygonAttributes(pa);
            setAppearance(appearance);
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
