package de.tudresden.cib.vis.data.multimodel;

import cib.mf.schedule.model.activity11.Activity11Package;
import cib.mf.schedule.model.activity11.util.Activity11ResourceFactoryImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import java.io.IOException;
import java.io.InputStream;

public class EMFSchedule11Accessor extends EMFScheduleAccessor {

    public EMFSchedule11Accessor(){ }

    public EMFSchedule11Accessor(InputStream stream) throws IOException {
        super(stream);
    }

    public EMFSchedule11Accessor(InputStream stream, String namespace) throws IOException {
        super(stream, namespace);
    }

    public EMFSchedule11Accessor(EObject data) {
        super(data);
    }

    @Override
    protected Resource createResource(URI uri) {
        Activity11Package.eINSTANCE.eClass();
        Activity11ResourceFactoryImpl resourceFactory = new Activity11ResourceFactoryImpl();
        return resourceFactory.createResource(uri);
    }
}