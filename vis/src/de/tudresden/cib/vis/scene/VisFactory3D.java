package de.tudresden.cib.vis.scene;

import de.tudresden.cib.vis.mapping.PropertyMap;

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
        void setColor(int R, int G, int B, int alpha);
    }

}
