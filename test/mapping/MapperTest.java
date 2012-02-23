package mapping;

import data.CollectionAccessor;
import data.DataAccessor;
import org.junit.Before;
import org.junit.Test;
import visualization.VisBuilder;
import visualization.VisFactory2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class MapperTest extends MappingTestCase {
    @Before
    public void setUp(){
         super.setUp();
    }

    @Test
    public void testMapping() throws TargetCreationException {
        DataAccessor data = new CollectionAccessor(Collections.singletonList(d));
        FakeVisBuilder builder = new FakeVisBuilder();
        VisFactory2D factory = new VisFactory2D(){
            @Override
            protected PropertyMap.Provider<VisFactory2D.Rectangle> setRectangleProvider() {
                return new PropertyMap.Provider<Rectangle>() {
                    public Rectangle create() {
                        return new FakeRectangle();
                    }
                };
            }
            @Override
            protected PropertyMap.Provider<VisFactory2D.Label> setLabelProvider() {
                return null;
            }
        };
        Mapper test = new Mapper(data, factory, builder);
        test.addMapping(
                new PropertyMap<DataElement, VisFactory2D.Rectangle>(){
                    @Override
                    protected void configure() {
                        graphObject.setWidth(data.a);
                    }
                }
        );
        test.map();
        assertEquals(1, builder.parts.size());
        assertEquals(d.a, ((FakeRectangle)builder.parts.get(0)).a);
    }
    
    public static class FakeVisBuilder implements VisBuilder<VisFactory2D.GraphObject,Object> {
        List<VisFactory2D.GraphObject> parts = new ArrayList<VisFactory2D.GraphObject>();
        
        public void init() {}
        public void addPart(VisFactory2D.GraphObject graphicalObject) {
            parts.add(graphicalObject);
        }
        public void finish() {}

        public Object getScene() {
             return null;
        }

    }

    public static class FakeRectangle implements VisFactory2D.Rectangle {
        protected int a;

        public void setLeft(int X) {}
        public void setTop(int Y) {}
        public void setHeight(int height) {}

        public void setWidth(int width) {
            this.a = width;
        }
    }
}
