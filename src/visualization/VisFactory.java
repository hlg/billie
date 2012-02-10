package visualization;

import mapping.PropertyMap;

import java.util.HashMap;

public abstract class VisFactory {

    public TypeMap provMap = new TypeMap();

    protected VisFactory(){
        addProviders();
    }

    private void addProviders(){
        provMap.put(Rectangle.class, setRectangleProvider());
        provMap.put(Label.class, setLabelProvider());
    }

    protected abstract PropertyMap.Provider<Rectangle> setRectangleProvider();
    protected abstract PropertyMap.Provider<Label> setLabelProvider();

    public <G extends GraphObject> PropertyMap.Provider<G> getProvider(Class<G> elementClass) {
        return provMap.get(elementClass);
    }

    public interface GraphObject {}
    public interface Rectangle extends GraphObject {
        void setWidth(int a);
    }

    protected interface Label{}

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
