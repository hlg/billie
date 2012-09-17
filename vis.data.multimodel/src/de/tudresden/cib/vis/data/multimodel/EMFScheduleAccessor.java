package de.tudresden.cib.vis.data.multimodel;

import org.eclipse.emf.ecore.EObject;

import java.io.IOException;
import java.io.InputStream;

public abstract class EMFScheduleAccessor<T extends EObject> extends EMFGenericAccessor<T> {

    public EMFScheduleAccessor(){ }

    public EMFScheduleAccessor(InputStream stream) throws IOException {
        super(stream);
    }

    public EMFScheduleAccessor(InputStream stream, String namespace) throws IOException {
        super(stream, namespace);
    }

    public EMFScheduleAccessor(EObject data) {
        super(data);
    }

}
