package de.tudresden.cib.vis.data.multimodel;

import cib.mf.qto.model.AnsatzType;
import de.mefisto.model.container.ElementaryModelType;
import de.mefisto.model.linkModel.LinkModel;
import de.mefisto.model.parser.LinkModelParser;
import de.tudresden.cib.vis.data.DataAccessException;
import de.tudresden.cib.vis.data.bimserver.EMFIfcGeometricAccessor;
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser;
import de.tudresden.cib.vis.data.bimserver.SimplePluginManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

public class MultiModelAccessorTests {

    MultiModelAccessor<EMFIfcParser.EngineEObject> mma;
    private static SimplePluginManager pm = null;
    static {
        if(pm==null){
            pm = new SimplePluginManager();
            pm.loadPluginsFromCurrentClassloader();
            pm.initAllLoadedPlugins();
        }
    }

    @Before
    public void setup(){
        mma = new MultiModelAccessor<EMFIfcParser.EngineEObject>(pm);
    }

    @Test
    public void testFolderAccess() throws DataAccessException {
        URL testResource = this.getClass().getResource("/resources/carport");
        mma.read(testResource);
        ElementaryModelType groupingModel = ElementaryModelType.OBJECT;
        check(mma);
    }

    @Test
    public void testZipAccess() throws IOException, DataAccessException {
        String fileName = "/resources/carport.zip";
        InputStream testFile = this.getClass().getResourceAsStream(fileName);
        mma.read(testFile, getClass().getResource(fileName).getFile().length());
        check(mma);
    }

    @Test
    public void testPreparsedAccess() throws IOException, DataAccessException {
        EMFScheduleAccessor schedule = new EMFSchedule10Accessor(this.getClass().getResourceAsStream("/resources/carport/Activity/xml/Vorgangsmodell_1.xml"), "Activity1");
        final EMFGaebAccessor gaeb = new EMFGaebAccessor(this.getClass().getResourceAsStream("/resources/carport/BoQ/gaebxml/LV_1.X81"), "BoQ1");
        EMFIfcGeometricAccessor ifc = new EMFIfcGeometricAccessor(pm, true);

        String fileName = "/resources/carport/Object/ifc/carport2.ifc";
        long size = new File(getClass().getResource(fileName).getFile()).length();
        ifc.read(getClass().getResourceAsStream(fileName), size);
        mma.addAcessor("M1", gaeb);
        mma.addAcessor("M3",ifc);
        mma.addAcessor("M4",schedule);
        File linkModelFile = new File(this.getClass().getResource("/resources/carport/links/links.xml").getFile());
        LinkModel linkModel = LinkModelParser.readLinkModel(linkModelFile).getLinkModel();
        mma.groupBy("M3", linkModel);
        check(mma);
    }

    private void check(MultiModelAccessor<EMFIfcParser.EngineEObject> mma) {
        int linkSize = 0;
        for (LinkedObject linking : mma) {
            linkSize++;
            Collection<LinkedObject.ResolvedLink> linkedItems = linking.getResolvedLinks();
            Assert.assertFalse(linkedItems.isEmpty());
            LinkedObject.ResolvedLink theLink = linkedItems.iterator().next();
            Assert.assertTrue(theLink.getLinkedObject().isEmpty());
            assertEquals(1, theLink.getLinkedBoQ().size());
            // assertEquals(1, theLink.getScheduleObjects().size()); // TODO: read old schedule models
            // assertFalse(theLink.getScheduleObjects().values().isEmpty());
            assertFalse(theLink.getLinkedBoQ().values().isEmpty());
        }
        Assert.assertEquals(5, linkSize);
    }

    @Test
    public void testHierarchic() throws DataAccessException, MalformedURLException {
        MultiModelAccessor<AnsatzType> multiModelAccessor = new MultiModelAccessor<AnsatzType>(pm);
        File input = new File(this.getClass().getResource("/resources/carport").getFile());
        multiModelAccessor.readFromFolder(input, new EMTypeCondition(EMTypes.QTO), new EMTypeCondition(EMTypes.IFCHIERARCHIC), new EMTypeCondition(EMTypes.GAEBHIERARCHIC));
        for(LinkedObject linkedObject : multiModelAccessor){
            Collection<LinkedObject.ResolvedLink> links = linkedObject.getResolvedLinks();
            assertFalse(links.isEmpty());
            LinkedObject.ResolvedLink theLink = links.iterator().next();
            assertFalse(theLink.getLinkedHierarchicGaeb().isEmpty());
            assertFalse(theLink.getLinkedHierarchicIfc().isEmpty());
        }
    }
}
