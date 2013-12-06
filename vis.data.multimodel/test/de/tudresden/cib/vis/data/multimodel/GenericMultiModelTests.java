package de.tudresden.cib.vis.data.multimodel;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class GenericMultiModelTests {
    @Test
    public void testLoadFile() throws Exception {
        GenericMultiModelAccessor data = new GenericMultiModelAccessor();
        assertNotNull(data);
        data.read(getClass().getResourceAsStream("/resources/carport.mmaa"),0);
    }
}
