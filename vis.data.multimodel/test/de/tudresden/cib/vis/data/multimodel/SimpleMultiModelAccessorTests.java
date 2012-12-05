package de.tudresden.cib.vis.data.multimodel;

import de.tudresden.cib.vis.data.DataAccessException;
import de.tudresden.cib.vis.data.bimserver.SimplePluginManager;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

public class SimpleMultiModelAccessorTests {
    SimpleMultiModelAccessor mma;
    File testResource;
    private static SimplePluginManager pm = new SimplePluginManager();
    static {
        pm.loadPluginsFromCurrentClassloader();
        pm.initAllLoadedPlugins();
    }

    @Before
    public void setUp(){
        mma = new SimpleMultiModelAccessor(pm);
        testResource = new File(getClass().getResource("/resources/carport").getFile());
    }

    @Test
    public void testFullRead() throws DataAccessException {
        mma.readFromFolder(testResource);
        checkFullRead();
    }

    @Test
    public void testPartialAccess() throws MalformedURLException, DataAccessException {
        List<String> modelIds = mma.readFromFolder(testResource, new EMTypeCondition(EMTypes.GAEB), new EMTypeCondition(EMTypes.QTO));
        Assert.assertEquals(2, modelIds.size());
        checkPartialRead();
    }

    @Test
    public void testSpecificLinkModel() throws MalformedURLException, DataAccessException {
        List<String> modelIds = mma.readFromFolder(testResource, "L2", new EMTypeCondition(EMTypes.GAEB), new EMTypeCondition(EMTypes.QTO));
        Assert.assertEquals(2, modelIds.size());
        checkOtherLinkModel();
    }

    private void checkOtherLinkModel() {
        check(false, 8);
    }

    private void checkPartialRead() {
        check(false, 15);
    }

    private void checkFullRead(){
        check(true, 15);
    }

    private void check(boolean full, int expectedSize) {
        int linkSize = 0;
        for(LinkedObject.ResolvedLink link: mma){
            linkSize++;
            if (full) Assert.assertEquals(1, link.getScheduleObjects().size());
            Assert.assertEquals(1, link.getLinkedBoQ().size());
            if (full) Assert.assertEquals(1, link.getLinkedObject().size());
            Assert.assertEquals(1, link.getLinkedQto().size());
        }
        Assert.assertEquals(linkSize, expectedSize);
    }
}
