package de.tudresden.cib.vis.data.multimodel;

import cib.mf.schedule.model.activity10.Activity;
import cib.mf.schedule.model.activity10.Activity10Package;
import cib.mf.schedule.model.activity10.util.Activity10ResourceFactoryImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class EMFSchedule10Accessor extends EMFScheduleAccessor<Activity> {

    public EMFSchedule10Accessor(){ super();}

    public EMFSchedule10Accessor(URL url) throws IOException {
        super(url);
    }

    public EMFSchedule10Accessor(URL url, String namespace) throws IOException {
        super(url, namespace);
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
