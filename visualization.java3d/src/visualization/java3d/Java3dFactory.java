package visualization.java3d;

import cib.lib.bimserverViewer.colorTime.TypeAppearance;
import com.google.common.collect.Collections2;
import mapping.PropertyMap;
import org.apache.commons.lang.ArrayUtils;
import visualization.VisFactory2D;
import visualization.VisFactory3D;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import java.util.Collections;
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
    protected PropertyMap.Provider<Polyline> setPolylineProvider() {
        return null; //TODO: polyline on projection area
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
            setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        }

        Builder builder = new Builder();

        private  class Builder {
            List<Float> vertizes;
            List<Float> normals;
            List<Integer> indizes;

            void build(Java3DPolyeder toBuild){
                if(paramsFilled() && validSizes()){
                    IndexedTriangleArray geometry = getGeometryWithDefault(vertizes.size() / 3, indizes.size());
                    geometry.setCoordinates(0, ArrayUtils.toPrimitive(vertizes.toArray(new Float[vertizes.size()])));
                    geometry.setNormals(0, ArrayUtils.toPrimitive(normals.toArray(new Float[normals.size()])));
                    geometry.setCoordinateIndices(0, ArrayUtils.toPrimitive(indizes.toArray(new Integer[indizes.size()])));
                    toBuild.setGeometry(geometry);
                }
            }

            private IndexedTriangleArray getGeometryWithDefault(int vertSize, int indexSize) {
                return new IndexedTriangleArray(vertSize, GeometryArray.NORMALS | GeometryArray.COORDINATES, indexSize);
            }

            private boolean validSizes() {
                return vertizes.size() == normals.size() && ((Collections.max(indizes)+1)*3 <= vertizes.size());
            }

            private boolean paramsFilled() {
                return vertizes!=null &&  normals!=null && indizes!=null;
            }
        }

        // TODO: how to make sure Java3DGraphObjects implement GraphObject and Java3D Node?
        public void setVertizes(List<Float> vertizes) {
            builder.vertizes = vertizes;
            builder.build(this);
        }

        public void setNormals(List<Float> normals) {
            builder.normals = normals;
            builder.build(this);
        }

        public void setIndizes(List<Integer> indizes){
            builder.indizes = indizes;
            builder.build(this);
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


    }

}
