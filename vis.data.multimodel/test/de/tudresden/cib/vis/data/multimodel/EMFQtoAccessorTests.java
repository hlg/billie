package de.tudresden.cib.vis.data.multimodel;

import cib.mf.qto.model.*;
import de.tudresden.cib.vis.data.DataAccessor;
import org.eclipse.emf.ecore.EObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

public class EMFQtoAccessorTests {

    URL resourceUrl;

    @Before
    public void setup(){
        resourceUrl = this.getClass().getResource("/resources/carport/QTO/xml/1_LV_VA.xml");
    }

    @Test
    public void testAccessEMF() throws IOException {
        DataAccessor<EObject> data = new EMFQtoAccessor(resourceUrl);
        check(data);
    }

    @Test
    public void testPreparsedEMF() throws IOException {
        EMFGenericAccessor baseAccessor = new EMFQtoAccessor(resourceUrl);
        DataAccessor<EObject> accessor = new EMFQtoAccessor(baseAccessor.data);
        check(accessor);
    }

    private void check(DataAccessor<EObject> data) {
        Iterator<? extends EObject> iterator = data.iterator();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertTrue(iterator.next() instanceof AufmassType);
        while (!(iterator.next() instanceof AnsaetzeType)){}
        Assert.assertTrue(iterator.next() instanceof AnsatzType);
        Assert.assertTrue(iterator.next() instanceof AnsatzZeilenType);
        Assert.assertTrue(iterator.next() instanceof ZeileType);
    }
}
