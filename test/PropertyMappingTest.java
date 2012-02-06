import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

public class PropertyMappingTest extends MappingTestCase {
    private PropertyMap<DataElement, VisElement> propertyMap;

    @Before
    public void setUp() {
        super.setUp();
        propertyMap = new PropertyMap<DataElement, VisElement>() {
            @Override
            protected void configure() {
                target.with = source.a;
                target.label = source.b;
                // builder.createLabel(0,0,source.b);
                // builder.createRectangle(0,0,source.a,10);
                // this would create more than one element per mapping ???
            }
        };
    }

    @Test
    public void testPreconstructed() {
        VisElement result = new VisElement();
        propertyMap.map(d, result);
        assertEquals(5, result.with);
        assertEquals("hello", result.label);
    }

    @Test
    public void testReflection() throws InstantiationException, IllegalAccessException {
        propertyMap.map(d, VisElement.class);
        VisElement result = propertyMap.target;
        assertEquals(5, result.with);
        assertEquals("hello", result.label);
    }

    @Test
    public void testProvide() throws TargetCreationException {
        propertyMap.with(new PropertyMap.Provider<VisElement>() {
            public VisElement create() {
                return new VisElement();
            }
        });
        propertyMap.map(d);
        VisElement result = propertyMap.target;
        assertEquals(5, result.with);
        assertEquals("hello", result.label);
    }

    @Test
    public void testProviderMissing() {
        try {
            propertyMap.map(d);
        } catch (TargetCreationException e) {
            return;
        }
        fail();
    }

}
