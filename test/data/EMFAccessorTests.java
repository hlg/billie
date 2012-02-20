package data;

import cib.lib.gaeb.model.gaeb.TgAward;
import cib.lib.gaeb.model.gaeb.TgGAEB;
import cib.lib.gaeb.model.gaeb.TgGAEBInfo;
import cib.lib.gaeb.model.gaeb.TgPrjInfo;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;

import static junit.framework.Assert.assertTrue;

public class EMFAccessorTests {
    @Test
    public void testAccessEMF() throws URISyntaxException, IOException {
        URL resourceUrl = this.getClass().getResource("/LV1.X81");
        DataAccessor<EObject, EObject> data = new EMFAccessor(resourceUrl);
        Iterator<? extends EObject> iterator = data.iterator();
        assertTrue(iterator.hasNext());
        assertTrue(iterator.next() instanceof TgGAEB);
        assertTrue(iterator.next() instanceof TgGAEBInfo);
        assertTrue(iterator.next() instanceof TgPrjInfo);
        assertTrue(iterator.next() instanceof TgAward);
    }
}
