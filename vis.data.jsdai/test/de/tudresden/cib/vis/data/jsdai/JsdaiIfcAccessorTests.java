package de.tudresden.cib.vis.data.jsdai;

import de.tudresden.cib.vis.data.DataAccessException;
import jsdai.SIfc2x3.EIfcbuildingelement;
import jsdai.SIfc2x3.EIfcslab;
import jsdai.lang.SdaiException;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

public class JsdaiIfcAccessorTests {

    private URL resource;
    private JsdaiIfcAccessor accessor;

    @Before
    public void setup(){
        resource = this.getClass().getResource("/resources/carport2.ifc");
        accessor = new JsdaiIfcAccessor();
    }

    @Test
    public void testAccessIfc() throws IOException, DataAccessException, SdaiException {
        accessor.read(resource);
        Assert.assertEquals(5,accessor.data.getEntitiesOf(EIfcbuildingelement.class).length);
        Assert.assertEquals(145,accessor.data.getEntities().length);
    }

    @Test
    public void testIndexIfc() throws DataAccessException, IOException {
        accessor.read(resource);
        accessor.index();
        Assert.assertTrue(accessor.getIndexed("1z174j70T5APfwnrpX_BVP") instanceof EIfcslab);
    }

    @After
    public void tearDown() throws SdaiException {
        accessor.dispose();
    }
}
