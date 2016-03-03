package de.tudresden.cib.vis.mapping;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class PropertyMappingTests extends MappingTestCase {
    private PropertyMap<DataElement, VisElement> propertyMap;

    @Before
    public void setUp() {
        super.createData();
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
    public void testProvide() throws TargetCreationException {
        PropertyMap.Provider provider = new PropertyMap.Provider<VisElement>() {
            public VisElement create() {
                return new VisElement();
            }
        };
        VisElement result = propertyMap.map(d, provider, 0);
        assertEquals(5, result.with);
        assertEquals("hello", result.label);
    }

}
