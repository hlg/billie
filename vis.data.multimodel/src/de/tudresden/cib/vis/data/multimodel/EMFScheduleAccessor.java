package de.tudresden.cib.vis.data.multimodel;

import org.eclipse.emf.ecore.EObject;

import java.io.IOException;
import java.net.URL;

public abstract class EMFScheduleAccessor<T extends EObject> extends EMFGenericAccessor<T> {

    public EMFScheduleAccessor(){ super(); }

    public EMFScheduleAccessor(URL url) throws IOException {
        super(url);
    }

    public EMFScheduleAccessor(URL url, String namespace) throws IOException {
        super(url, namespace);
    }

    public EMFScheduleAccessor(EObject data) {
        super(data);
    }

}
