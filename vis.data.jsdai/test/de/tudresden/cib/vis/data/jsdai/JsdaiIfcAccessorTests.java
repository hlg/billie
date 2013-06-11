package de.tudresden.cib.vis.data.jsdai;

import de.tudresden.cib.vis.data.DataAccessException;
import jsdai.SIfc2x3.EIfcproduct;
import jsdai.lang.SdaiException;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class JsdaiIfcAccessorTests {

    InputStream resourceStream;

    @Before
    public void setup(){
        resourceStream = this.getClass().getResourceAsStream("/resources/carport2.ifc");
    }

    @Test
    public void testAccessIfc() throws IOException, DataAccessException, SdaiException {
        JsdaiIfcAccessor accessor = new JsdaiIfcAccessor();
        accessor.read(resourceStream,0);
        Assert.assertEquals(6,accessor.data.getEntitiesOf(EIfcproduct.class).length);
        Assert.assertEquals(145,accessor.data.getEntities().length);
    }
}
