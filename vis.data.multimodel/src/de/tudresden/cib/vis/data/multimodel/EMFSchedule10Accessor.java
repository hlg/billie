package de.tudresden.cib.vis.data.multimodel;

import cib.mf.schedule.model.activity10.Activity10Package;
import cib.mf.schedule.model.activity10.util.Activity10ResourceFactoryImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import java.io.IOException;
import java.io.InputStream;

public class EMFSchedule10Accessor extends EMFScheduleAccessor {

    public EMFSchedule10Accessor(){ }

    public EMFSchedule10Accessor(InputStream stream) throws IOException {
        super(stream);
    }

    public EMFSchedule10Accessor(InputStream stream, String namespace) throws IOException {
        super(stream, namespace);
    }

    public EMFSchedule10Accessor(EObject data) {
        super(data);
    }

    @Override
    protected Resource createResource(URI uri) {
        Activity10Package.eINSTANCE.eClass();
        Activity10ResourceFactoryImpl resourceFactory = new Activity10ResourceFactoryImpl();
        return resourceFactory.createResource(uri);
    }
}
