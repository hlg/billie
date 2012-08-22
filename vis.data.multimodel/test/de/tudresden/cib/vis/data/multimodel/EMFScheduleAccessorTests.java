package de.tudresden.cib.vis.data.multimodel;

import cib.mf.schedule.model.activity.ActivitySection;
import cib.mf.schedule.model.activity.Cpixml;
import cib.mf.schedule.model.activity.Schedule;
import cib.mf.schedule.model.activity.ScheduleData;
import de.tudresden.cib.vis.data.DataAccessor;
import org.eclipse.emf.ecore.EObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

public class EMFScheduleAccessorTests {

    URL resourceUrl;

    @Before
    public void setup(){
        resourceUrl = this.getClass().getResource("/resources/carport/Activity/xml/Vorgangsmodell_1.xml");
    }

    @Test
    public void testAccessEMF() throws IOException {
        DataAccessor<EObject> data = new EMFScheduleAccessor(resourceUrl);
        check(data);
    }

    @Test
    public void testPreparsedEMF() throws IOException {
        EMFGenericAccessor baseAccessor = new EMFScheduleAccessor(resourceUrl);
        DataAccessor<EObject> accessor = new EMFScheduleAccessor(baseAccessor.data);
        check(accessor);
    }

    private void check(DataAccessor<EObject> data) {
        Iterator<? extends EObject> iterator = data.iterator();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertTrue(iterator.next() instanceof Cpixml);
        Assert.assertTrue(iterator.next() instanceof ActivitySection);
        Assert.assertTrue(iterator.next() instanceof Schedule);
        Assert.assertTrue(iterator.next() instanceof ScheduleData);
    }
}
