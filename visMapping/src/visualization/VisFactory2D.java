package visualization;

import mapping.PropertyMap;

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
    }

    protected abstract PropertyMap.Provider<Rectangle> setRectangleProvider();
    protected abstract PropertyMap.Provider<Label> setLabelProvider();
    protected abstract PropertyMap.Provider<Polyline> setPolylineProvider();

    public <G extends GraphObject> PropertyMap.Provider<G> getProvider(Class<G> elementClass) {
        return provMap.get(elementClass);
    }

    public interface GraphObject {
        // TODO: separation of builder and actual object (problem of incompleteness during build)
    }
    public interface Rectangle extends GraphObject {
        void setLeft(int X);
        void setTop(int Y);
        void setHeight(int height);
        void setWidth(int width);
    }

    public interface Label extends GraphObject {
        void setLeft(int X);
        void setTop(int Y);
        void setText(String text);
    }

    public interface Polyline extends GraphObject {
        void addLine(int x1, int y1, int x2, int y2);
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
