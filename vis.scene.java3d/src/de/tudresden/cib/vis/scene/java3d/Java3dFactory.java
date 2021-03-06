package de.tudresden.cib.vis.scene.java3d;

import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.runtime.java3d.colorTime.TypeAppearance;
import de.tudresden.cib.vis.scene.VisFactory3D;
import org.apache.commons.lang.ArrayUtils;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import java.util.Collections;
import java.util.List;

public class Java3dFactory extends VisFactory3D {

    Appearance defaultAppearance;

    public Java3dFactory(){
        defaultAppearance = TypeAppearance.INACTIVE.getAppearance();
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
    protected PropertyMap.Provider<Bezier> setBezierProvider() {
        return null;  //TODO: bezier on projection area
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
                    int[] indizesPrimitive = ArrayUtils.toPrimitive(indizes.toArray(new Integer[indizes.size()]));
                    geometry.setCoordinateIndices(0, indizesPrimitive);
                    geometry.setNormalIndices(0, indizesPrimitive);
                    toBuild.setGeometry(geometry);
                }
            }

            private IndexedTriangleArray getGeometryWithDefault(int vertSize, int indexSize) {
                return new IndexedTriangleArray(vertSize, GeometryArray.NORMALS | GeometryArray.COORDINATES, indexSize);
            }

            private boolean validSizes() {
                return !indizes.isEmpty() && vertizes.size() == normals.size() && ((Collections.max(indizes)+1)*3 <= vertizes.size());
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

        public void setColor(int R, int G, int B) {
            Appearance appearance = createAppearance(R, G, B);
            appearance.setTransparencyAttributes(getAppearance().getTransparencyAttributes());
            setAppearance(appearance);
        }

        private Appearance createAppearance(float R, float G, float B) {
            Appearance appearance = new Appearance();
            Color3f color3f = new Color3f(R /255, G /255, B /255);
            Color3f ambient = new Color3f(color3f);
            ambient.scale(0.5f);
            Color3f noColor = new Color3f(0f, 0f, 0f);
            Material material = new Material(noColor, noColor, color3f, noColor, 1);  //  ambient, emissive, diffuse, specular, shininess
            material.setLightingEnable(true);
            appearance.setMaterial(material);
            appearance.setPolygonAttributes(getAppearance().getPolygonAttributes());
            return appearance;
        }

        @Override
        public void setColor(int R, int G, int B, int alpha) {
            Appearance appearance = createAppearance(R,G,B);
            if(alpha>0) appearance.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NICEST, (float)alpha/255));
            setAppearance(appearance);
        }
    }

}
