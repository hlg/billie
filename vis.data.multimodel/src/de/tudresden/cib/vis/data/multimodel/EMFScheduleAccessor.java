package de.tudresden.cib.vis.data.multimodel;

import cib.mf.schedule.model.activity10.Activity;
import org.eclipse.emf.ecore.EObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class EMFScheduleAccessor extends EMFGenericAccessor<Activity> {

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
