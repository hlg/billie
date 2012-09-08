package de.tudresden.cib.vis.data.multimodel;

import cib.mf.schedule.model.activity10.ActivitySection;
import cib.mf.schedule.model.activity10.Cpixml;
import cib.mf.schedule.model.activity10.Schedule;
import cib.mf.schedule.model.activity10.ScheduleData;
import org.eclipse.emf.ecore.EObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class EMFScheduleAccessorTests {

    private InputStream resource10;
    private InputStream resource11; // TODO test data

    @Before
    public void setup(){
        resource10 = this.getClass().getResourceAsStream("/resources/carport/Activity/xml/Vorgangsmodell_1.xml");
    }

    @Test
    public void testAccessEMF10() throws IOException {
        EMFSchedule10Accessor data = new EMFSchedule10Accessor(resource10);
        check(data);
    }

    @Test
    public void XXtestAccessEMF11() throws IOException {
        EMFSchedule11Accessor data = new EMFSchedule11Accessor(resource11);
        check(data);
    }

    @Test
    public void testPreparsedEMF10() throws IOException {
        EMFGenericAccessor baseAccessor = new EMFSchedule10Accessor(resource10);
        EMFSchedule10Accessor accessor = new EMFSchedule10Accessor(baseAccessor.data);
        check(accessor);
    }

    @Test
    public void XXtestPreparsedEMF11() throws IOException {
        EMFGenericAccessor baseAccessor = new EMFSchedule11Accessor(resource11);
        EMFSchedule11Accessor accessor = new EMFSchedule11Accessor(baseAccessor.data);
        check(accessor);
    }

    private void check(EMFSchedule10Accessor data) {
        Iterator<? extends EObject> iterator = data.iterator();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertTrue(iterator.next() instanceof Cpixml);
        Assert.assertTrue(iterator.next() instanceof ActivitySection);
        Assert.assertTrue(iterator.next() instanceof Schedule);
        Assert.assertTrue(iterator.next() instanceof ScheduleData);
    }

    private void check(EMFSchedule11Accessor data) {
        Iterator<? extends EObject> iterator = data.iterator();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertTrue(iterator.next() instanceof cib.mf.schedule.model.activity11.Cpixml);
        Assert.assertTrue(iterator.next() instanceof cib.mf.schedule.model.activity11.ActivitySection);
        Assert.assertTrue(iterator.next() instanceof cib.mf.schedule.model.activity11.Schedule);
        Assert.assertTrue(iterator.next() instanceof cib.mf.schedule.model.activity11.ScheduleData);
    }
}
