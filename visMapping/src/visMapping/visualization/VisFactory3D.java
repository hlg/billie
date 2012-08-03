package visMapping.visualization;

import visMapping.mapping.PropertyMap;

import java.util.List;

public abstract class VisFactory3D extends VisFactory2D {
    @Override
    void addProviders() {
        super.addProviders();
        provMap.put(Polyeder.class, setPolyederProvider());
    }

    protected abstract PropertyMap.Provider<Polyeder> setPolyederProvider();

    public interface Polyeder extends GraphObject {
        void setVertizes(List<Float> vertizes);

        void setNormals(List<Float> normals);

        void setIndizes(List<Integer> indizes);

        void setColor(float R, float G, float B);
    }

}
