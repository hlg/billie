package data.multimodel;

import cib.lib.gaeb.model.gaeb.TgAward;
import cib.lib.gaeb.model.gaeb.TgGAEB;
import cib.lib.gaeb.model.gaeb.TgGAEBInfo;
import cib.lib.gaeb.model.gaeb.TgPrjInfo;
import org.eclipse.emf.ecore.EObject;
import org.junit.Assert;
import org.junit.Test;
import visMapping.data.DataAccessor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;

public class EMFAccessorTests {
    @Test
    public void testAccessEMF() throws URISyntaxException, IOException {
        URL resourceUrl = this.getClass().getResource("/LV1.X81");
        DataAccessor<EObject> data = new EMFGaebAccessor(resourceUrl);
        Iterator<? extends EObject> iterator = data.iterator();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertTrue(iterator.next() instanceof TgGAEB);
        Assert.assertTrue(iterator.next() instanceof TgGAEBInfo);
        Assert.assertTrue(iterator.next() instanceof TgPrjInfo);
        Assert.assertTrue(iterator.next() instanceof TgAward);
    }
}
