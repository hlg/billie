package de.tudresden.cib.vis.scene.draw2d;


import de.tudresden.cib.vis.scene.UIContext;
import de.tudresden.cib.vis.scene.VisBuilder;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.widgets.Display;

public class Draw2dBuilder implements VisBuilder<Draw2dFactory.Draw2dObject, Panel> {
    private Panel chart;
    private XYLayout manager;
    private UIContext uiContext  = new UIContext() {
        @Override
        public void runInUIContext(Runnable runnable) {
            Display.getDefault().syncExec(runnable);
        }
    };

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

    @Override
    public UIContext getUiContext() {
        return uiContext;
    }
}
