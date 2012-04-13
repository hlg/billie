package data;

import cib.lib.gaeb.model.gaeb.TgItem;
import cib.mmaa.qto.elementaryModel.Qto.AnsatzType;
import de.mefisto.model.container.ElementaryModelType;
import org.junit.Test;

import java.net.URL;
import java.util.Collection;

import static junit.framework.Assert.*;

public class MultiModelAccessorTests {
    @Test
    public void testAccess() {
        URL testResource = this.getClass().getResource("/carport");
        MultiModelAccessor<EMFIfcAccessor.EngineEObject> mma = new MultiModelAccessor<EMFIfcAccessor.EngineEObject>(testResource);
        int linkSize = 0;
        ElementaryModelType groupingModel = ElementaryModelType.OBJECT;
        for (MultiModelAccessor.LinkedObject linking : mma) {
            linkSize++;
            Collection<MultiModelAccessor.ResolvedLink> linkedItems = linking.getResolvedLinks();
            assertFalse(linkedItems.isEmpty());
            MultiModelAccessor.ResolvedLink theLink = linkedItems.iterator().next();
            assertTrue(theLink.getLinkedObject().isEmpty());
            assertEquals(1, theLink.getLinkedBoQ().size());
            assertEquals(1, theLink.getScheduleObjects().size());
            TgItem theItem = theLink.getLinkedBoQ().values().iterator().next();
            AnsatzType theAnsatz = theLink.getLinkedQto().values().iterator().next();
        }
        assertEquals(5, linkSize);
    }
}
