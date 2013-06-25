package de.tudresden.cib.vis.data.multimodel;

import org.junit.Test;

public class GenericMultiModelTests {
    @Test
    public void testLoadFile() throws Exception {
        GenericMultiModelAccessor data = new GenericMultiModelAccessor();
        data.read(getClass().getResourceAsStream("/resources/carport.mmaa"),0);

    }
}
