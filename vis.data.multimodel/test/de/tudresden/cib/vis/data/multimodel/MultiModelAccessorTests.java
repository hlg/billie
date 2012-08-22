package de.tudresden.cib.vis.data.multimodel;

import de.mefisto.model.container.ElementaryModelType;
import de.mefisto.model.linkModel.LinkModel;
import de.mefisto.model.parser.LinkModelParser;
import de.tudresden.cib.vis.data.bimserver.EMFIfcAccessor;
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser;
import de.tudresden.cib.vis.data.bimserver.SimplePluginManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class MultiModelAccessorTests {

    MultiModelAccessor<EMFIfcParser.EngineEObject> mma;

    @Before
    public void setup(){
        mma = new MultiModelAccessor<EMFIfcParser.EngineEObject>(new SimplePluginManager());
    }

    @Test
    public void testFolderAccess() {
        URL testResource = this.getClass().getResource("/resources/carport");
        mma.read(testResource);
        ElementaryModelType groupingModel = ElementaryModelType.OBJECT;
        check(mma);
    }

    @Test
    public void testZipAccess() throws IOException {
        InputStream testFile = this.getClass().getResourceAsStream("/resources/carport.zip");
        mma.read(testFile);
        check(mma);
    }

    @Test
    public void testPreparsedAccess() throws IOException {
        EMFScheduleAccessor schedule = new EMFScheduleAccessor(this.getClass().getResourceAsStream("/resources/carport/Activity/xml/Vorgangsmodell_1.xml"), "Activity1");
        final EMFGaebAccessor gaeb = new EMFGaebAccessor(this.getClass().getResourceAsStream("/resources/carport/BoQ/gaebxml/LV_1.X81"), "BoQ1");
        EMFIfcAccessor ifc = new EMFIfcAccessor(new SimplePluginManager());
        ifc.read(this.getClass().getResourceAsStream("/resources/carport/Object/ifc/carport2.ifc"));
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
        for (MultiModelAccessor.LinkedObject linking : mma) {
            linkSize++;
            Collection<MultiModelAccessor.ResolvedLink> linkedItems = linking.getResolvedLinks();
            Assert.assertFalse(linkedItems.isEmpty());
            MultiModelAccessor.ResolvedLink theLink = linkedItems.iterator().next();
            Assert.assertTrue(theLink.getLinkedObject().isEmpty());
            assertEquals(1, theLink.getLinkedBoQ().size());
            assertEquals(1, theLink.getScheduleObjects().size());
            assertNotNull(theLink.getLinkedBoQ().values().iterator().next());
            assertNotNull(theLink.getScheduleObjects().values().iterator().next());
        }
        Assert.assertEquals(5, linkSize);
    }


}
