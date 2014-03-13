package de.tudresden.cib.vis.data.multimodel;

import de.tudresden.cib.vis.data.bimserver.SimplePluginManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GenericMultiModelTests {

    private GenericMultiModelAccessor data;

    @Before
    public void setUp(){
        SimplePluginManager pm = new SimplePluginManager();
        pm.loadPluginsFromCurrentClassloader();
        pm.initAllLoadedPlugins();
        data = new GenericMultiModelAccessor(pm);
    }

    @Test
    public void testKeyModelOnly() throws Exception {
        assertNotNull(data);
        data.read(getClass().getResource("/resources/carport.mmaa"), new GenericMultiModelAccessor.EMTypeCondition(EMTypes.IFC));
        int objectCount = 0;
        for(Object object : data) objectCount++;
        assertEquals(5, objectCount);
    }
}
