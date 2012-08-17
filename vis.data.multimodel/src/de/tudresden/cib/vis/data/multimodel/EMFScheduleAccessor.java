package de.tudresden.cib.vis.data.multimodel;

import cib.mf.schedule.model.activity.Activity;
import cib.mf.schedule.model.activity.ActivityPackage;
import cib.mf.schedule.model.activity.util.ActivityResourceFactoryImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;

import java.util.HashMap;
import java.util.Map;

public class EMFScheduleAccessor extends EMFGenericAccessor<Activity> {
    @Override
    protected Resource createResource(URI uri) {
        ActivityPackage.eINSTANCE.eClass();
        ResourceFactoryImpl activityResourceFactory = new ActivityResourceFactoryImpl();
        return activityResourceFactory.createResource(uri);
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
