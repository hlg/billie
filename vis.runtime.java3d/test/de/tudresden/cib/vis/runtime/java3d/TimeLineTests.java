package de.tudresden.cib.vis.runtime.java3d;

import de.tudresden.cib.vis.runtime.java3d.colorTime.TimeLine;
import org.bimserver.models.ifc2x3tc1.Ifc2x3tc1Package;
import org.bimserver.models.ifc2x3tc1.IfcColumn;
import org.bimserver.models.ifc2x3tc1.IfcWall;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * @author helga
 */
public class TimeLineTests {

    TimeLine timeLine;
    private IfcWall ifcWall;
    private IfcColumn ifcColumn;

    @Before
    public void setUp() {
        timeLine = new TimeLine();
        Set<TimeLine.Activity> activities = new HashSet<TimeLine.Activity>();
        ifcWall = Ifc2x3tc1Package.eINSTANCE.getIfc2x3tc1Factory().createIfcWall();
        activities.add(new TimeLine.Activity(1, 4, ifcWall));
        ifcColumn = Ifc2x3tc1Package.eINSTANCE.getIfc2x3tc1Factory().createIfcColumn();
        activities.add(new TimeLine.Activity(3, 5, ifcColumn));
        timeLine.addToTimeLine(activities);
    }

    @Test
    public void testGetChanges() {
        assert timeLine.getChanges(2) == null;
        assert timeLine.getChanges(3).get(ifcColumn).equals(TimeLine.Change.ACTIVATE);
        assert timeLine.getChanges(4).get(ifcWall).equals(TimeLine.Change.DEACTIVATE);
    }
}
