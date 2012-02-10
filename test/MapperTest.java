import data.CollectionAccessor;
import mapping.Mapper;
import mapping.PropertyMap;
import mapping.TargetCreationException;
import org.junit.Before;
import org.junit.Test;
import visualization.VisBuilder;
import visualization.VisFactory;

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
        Mapper test = new Mapper();
        test.visFactory = new VisFactory(){

            @Override
            protected PropertyMap.Provider<VisFactory.Rectangle> setRectangleProvider() {
                return new PropertyMap.Provider<Rectangle>() {
                    public Rectangle create() {
                        return new FakeRectangle();
                    }
                };
            }

            @Override
            protected PropertyMap.Provider<VisFactory.Label> setLabelProvider() {
                return null;
            }
        };
        test.dataAccessor = new CollectionAccessor(Collections.singletonList(d));
        FakeVisBuilder scene = new FakeVisBuilder();
        test.visBuilder = scene;
        test.addMapping(
                new PropertyMap<DataElement, VisFactory.Rectangle>(){
                    @Override
                    protected void configure() {
                        graphObject.setWidth(data.a);
                    }
                }
        );
        test.map();
        assertEquals(1, scene.parts.size());
        assertEquals(d.a, ((FakeRectangle)scene.parts.get(0)).a);
    }
    
    public static class FakeVisBuilder implements VisBuilder {
        List<VisFactory.GraphObject> parts = new ArrayList<VisFactory.GraphObject>();
        
        public void init() {}
        public void addPart(VisFactory.GraphObject graphicalObject) {
            parts.add(graphicalObject);
        }
        public void finish() {}
    }

    public static class FakeRectangle implements VisFactory.Rectangle {
        protected int a;
        public void setWidth(int a) {
            this.a = a;
        }
    }
}
