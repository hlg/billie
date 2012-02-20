package visualization;


import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.Rectangle;

public class Draw2dBuilder implements VisBuilder<Draw2dFactory.Draw2dObject, Panel> {
    private Panel chart;
    private XYLayout manager;

    public void init() {
        chart = new Panel();
        chart.setBackgroundColor(ColorConstants.white);
        manager = new XYLayout();
        chart.setLayoutManager(manager);
        chart.setBorder(new MarginBorder(20));
    }

    public void addPart(Draw2dFactory.Draw2dObject graphicalObject) {
        chart.add(graphicalObject);
        manager.setConstraint(graphicalObject, new Rectangle(graphicalObject.getBounds()));
    }

    public void finish() {
    }

    public Panel getScene() {
        return chart;
    }
}
