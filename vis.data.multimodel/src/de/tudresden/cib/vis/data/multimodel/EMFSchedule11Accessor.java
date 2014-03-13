package de.tudresden.cib.vis.data.multimodel;

import cib.mf.schedule.model.activity11.Activity;
import cib.mf.schedule.model.activity11.Activity11Package;
import cib.mf.schedule.model.activity11.util.Activity11ResourceFactoryImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class EMFSchedule11Accessor extends EMFScheduleAccessor<Activity> {

    public EMFSchedule11Accessor(){ super();}

    public EMFSchedule11Accessor(URL url) throws IOException {
        super(url);
    }

    public EMFSchedule11Accessor(URL url, String namespace) throws IOException {
        super(url, namespace);
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
