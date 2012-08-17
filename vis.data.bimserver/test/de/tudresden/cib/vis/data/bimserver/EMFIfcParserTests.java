package de.tudresden.cib.vis.data.bimserver;

import org.bimserver.models.ifc2x3tc1.IfcBuildingElement;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

public class EMFIfcParserTests {

    private EMFIfcParser parser;

    @Before
    public void setUp(){
        parser = new EMFIfcParser();
    }

    @Test
    public void testLazyLoad() {
        parser.setInput(getClass().getResourceAsStream("/resources/carport2.ifc"));
        assertNull(parser.data);
        assertNull(parser.getIterator());
        parser.lazyLoad();
        assertNotNull(parser.data);
        assertEquals(993, parser.data.getSize());  // size was 99x - why has it changed?
        assertEquals(5, parser.data.getAllWithSubTypes(IfcBuildingElement.class).size());
        Iterator<EMFIfcParser.EngineEObject> iterator = parser.getIterator();
        assertNotNull(iterator);
        assertTrue(iterator.hasNext());
        assertNotNull(iterator.next().getObject());
    }
}
