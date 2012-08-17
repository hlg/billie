package de.tudresden.cib.vis.runtime.java3d.colorTime;

import org.bimserver.models.ifc2x3tc1.IfcRoot;

import java.util.*;

/**
 * @author helga
 */
public class TimeLine {

    TreeMap<Integer, Map<IfcRoot, Change>> timeLine = new TreeMap<Integer, Map<IfcRoot, Change>>();

    public void addToTimeLine(Set<Activity> activities) {
        for (Activity activity : activities) {
            addToTimeLine(activity);
        }
    }

    public void addToTimeLine(Activity activity) {
        if (!timeLine.containsKey(activity.start)) timeLine.put(activity.start, new HashMap<IfcRoot, Change>());
        if (!timeLine.containsKey(activity.end)) timeLine.put(activity.end, new HashMap<IfcRoot, Change>());
        for (IfcRoot object : activity.objects) {
            timeLine.get(activity.start).put(object, Change.ACTIVATE);
            timeLine.get(activity.end).put(object, Change.DEACTIVATE);
        }
    }

    public Map<IfcRoot, Change> getChanges(int frame) {
        return timeLine.get(frame);
    }

    Change getState(IfcRoot object, int frame) {
        return timeLine.get(frame).get(object);
    }

    public int getLength() {
        return timeLine.lastKey();
    }

    public enum Change {
        ACTIVATE, DEACTIVATE // start, finish
    }

    public static class Activity {
        int start;
        int end;
        Set<IfcRoot> objects = new HashSet<IfcRoot>(); // TODO: should this be a set or rather a single object ??? (used as single element set only until now)

        public Activity(int start, int end, IfcRoot object) {
            this.start = start;
            this.end = end;
            objects.add(object);
        }
    }
}
