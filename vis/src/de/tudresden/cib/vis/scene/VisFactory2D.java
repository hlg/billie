package de.tudresden.cib.vis.scene;

import de.tudresden.cib.vis.mapping.PropertyMap;

import java.util.HashMap;

public abstract class VisFactory2D {

    public TypeMap provMap = new TypeMap();

    protected VisFactory2D(){
        addProviders();
    }

    void addProviders(){
        provMap.put(Rectangle.class, setRectangleProvider());
        provMap.put(Label.class, setLabelProvider());
        provMap.put(Polyline.class, setPolylineProvider());
        provMap.put(Bezier.class, setBezierProvider());
    }

    protected abstract PropertyMap.Provider<Rectangle> setRectangleProvider();
    protected abstract PropertyMap.Provider<Label> setLabelProvider();
    protected abstract PropertyMap.Provider<Polyline> setPolylineProvider();
    protected abstract PropertyMap.Provider<Bezier> setBezierProvider();

    public <G extends GraphObject> PropertyMap.Provider<G> getProvider(Class<G> elementClass) {
        return provMap.get(elementClass);
    }

    public interface GraphObject {
        // TODO: separation of builder and actual object (problem of incompleteness during build)
        void setColor(int r, int g, int b);
    }

    public interface GraphObject2D extends GraphObject {
        void setBackground();
        void setForeground();
        boolean getBackground();
        boolean getForeground();
    }

    public interface Rectangle extends GraphObject2D {
        void setLeft(int X);
        void setTop(int Y);
        void setHeight(int height);
        void setWidth(int width);
    }

    public interface Label extends GraphObject2D {
        void setLeft(int X);
        void setTop(int Y);
        void setText(String text);
        void setVertical(boolean v);
    }

    public interface Polyline extends GraphObject2D {
        void addLine(int x1, int y1, int x2, int y2);
        void addPoint(int x, int y);
    }

    public interface Bezier extends GraphObject2D {
        void addPoint(int x, int y);
    }

    class TypeMap extends HashMap<Class, PropertyMap.Provider> {

        public <V> void put(Class<V> key, PropertyMap.Provider<V> value){
            super.put(key, value);
        }
        @SuppressWarnings("unchecked")
        public <V> PropertyMap.Provider<V> get(Class<V> key){
            return super.get(key);
        }

    }
}
