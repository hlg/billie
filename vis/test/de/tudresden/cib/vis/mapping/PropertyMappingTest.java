package de.tudresden.cib.vis.mapping;

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
                graphObject.with = data.a;
                graphObject.label = data.b;
                // factory.createLabel(0,0,data.b);
                // factory.createRectangle(0,0,data.a,10);
                // this would create more than one element per mapping ???
            }
        };
    }

    @Test
    public void testPreconstructed() {
        VisElement result = new VisElement();
        propertyMap.map(d, result, 0);
        assertEquals(5, result.with);
        assertEquals("hello", result.label);
    }

    @Test
    public void testReflection() throws InstantiationException, IllegalAccessException {
        VisElement result = propertyMap.map(d, VisElement.class, 0);
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
        VisElement result = propertyMap.map(d, 0);
        assertEquals(5, result.with);
        assertEquals("hello", result.label);
    }

    @Test
    public void testProviderMissing() {
        try {
            propertyMap.map(d, 0);
        } catch (TargetCreationException e) {
            return;
        }
        fail();
    }

}
