package de.tudresden.cib.vis.data.multimodel;

import cib.mf.schedule.model.activity.Activity;
import cib.mf.schedule.model.activity.ActivityPackage;
import cib.mf.schedule.model.activity.util.ActivityResourceImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class EMFScheduleAccessor extends EMFGenericAccessor<Activity> {

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

    public EMFScheduleAccessor(URL resourceUrl) throws IOException {
        super(resourceUrl);
    }

    @Override
    protected Resource createResource(URI uri) {
        ActivityPackage.eINSTANCE.eClass();
        return new ActivityResourceImpl(uri);
    }

    @Override
    protected Map<String, Activity> collectLookUp() {
        Map<String, Activity> lookUp = new HashMap<String, Activity>();
        for (EObject object : this) {
            if (object instanceof Activity) {
                Activity activity = (Activity) object;
                lookUp.put(namespace + activity.getID(), activity);
            }
        }
        return lookUp;
    }
}
