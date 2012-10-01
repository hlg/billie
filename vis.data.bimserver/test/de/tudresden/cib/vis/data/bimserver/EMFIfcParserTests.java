package de.tudresden.cib.vis.data.bimserver;

import org.bimserver.models.ifc2x3tc1.IfcBuildingElement;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import static org.junit.Assert.*;

public class EMFIfcParserTests {

    private EMFIfcParser parser;
    private EMFIfcGeometricAccessor accessor;

    @Before
    public void setUp(){
        SimplePluginManager pluginManager = new SimplePluginManager();
        parser = new EMFIfcParser(pluginManager);
        accessor = new EMFIfcGeometricAccessor(pluginManager);
    }

    @Test
    public void testLazyLoad() {
        String fileName = "/resources/carport2.ifc";
        long size = new File(getClass().getResource(fileName).getFile()).length();
        parser.read(getClass().getResourceAsStream(fileName), size);
//        assertNull(parser.data);
//        assertNull(parser.getIterator());
//        parser.lazyLoad();
        assertNotNull(parser.data);
        assertEquals(993, parser.data.getSize());  // size was 99x - why has it changed?
        assertEquals(5, parser.data.getAllWithSubTypes(IfcBuildingElement.class).size());
        Iterator<EMFIfcParser.EngineEObject> iterator = parser.getIterator();
        assertNotNull(iterator);
        assertTrue(iterator.hasNext());
        assertNotNull(iterator.next().getObject());
    }

    @Test
    public void testAccessor() throws IOException {
        String fileName = "/resources/carport2.ifc";
        long size = new File(getClass().getResource(fileName).getFile()).length();
        accessor.read(getClass().getResourceAsStream(fileName), size);
        accessor.index();
        assertNotNull(accessor.iterator());
        assertTrue(accessor.iterator().hasNext());
        assertNotNull(accessor.iterator().next().getObject());

    }
}
