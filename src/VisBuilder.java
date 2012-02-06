import com.sun.deploy.util.Property;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import java.util.Map;

public class VisBuilder<T> {

    private Map<Class, PropertyMap.Provider> providerMap = new HashMap<Class, PropertyMap.Provider>(){{
        put(Rectangle.class, new PropertyMap.Provider<Rectangle>(){
            public Rectangle create() {
                return new Rectangle();
            }
        });
        put(Ellipse2D.class,  new PropertyMap.Provider<Ellipse2D>(){
            public Ellipse2D create() {
                return new Ellipse2D.Double();
            }
        });
    }};

    public PropertyMap.Provider getProvider(Class elementClass) {
        return providerMap.get(elementClass);
    }
}
