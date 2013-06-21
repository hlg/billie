package de.tudresden.cib.vis.data.multimodel;

import cib.lib.gaeb.model.gaeb.TgAward;
import cib.lib.gaeb.model.gaeb.TgGAEB;
import cib.lib.gaeb.model.gaeb.TgGAEBInfo;
import cib.lib.gaeb.model.gaeb.TgPrjInfo;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.filter.Condition;
import org.eclipse.emf.ecore.EObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Iterator;

public class EMFGaebAccessorTests {

    InputStream resourceStream;

    @Before
    public void setup(){
        resourceStream = this.getClass().getResourceAsStream("/resources/carport/BoQ/gaebxml/LV_1.X81");
    }

    @Test
    public void testAccessEMF() throws URISyntaxException, IOException {
        DataAccessor<EObject, Condition<EObject>> data = new EMFGaebAccessor(resourceStream);
        check(data);
    }

    @Test
    public void testPreparsedEMF() throws IOException {
        EMFGenericAccessor baseAccessor = new EMFGaebAccessor(resourceStream);
        DataAccessor<EObject, Condition<EObject>> accessor = new EMFGaebAccessor(baseAccessor.data);
        check(accessor);
    }

    private void check(DataAccessor<EObject, Condition<EObject>> data) {
        Iterator<? extends EObject> iterator = data.iterator();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertTrue(iterator.next() instanceof TgGAEB);
        Assert.assertTrue(iterator.next() instanceof TgGAEBInfo);
        Assert.assertTrue(iterator.next() instanceof TgPrjInfo);
        Assert.assertTrue(iterator.next() instanceof TgAward);
    }
}
