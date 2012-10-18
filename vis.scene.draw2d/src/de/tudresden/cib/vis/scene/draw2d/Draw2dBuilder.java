package de.tudresden.cib.vis.scene.draw2d;


import de.tudresden.cib.vis.scene.ChangeMap;
import de.tudresden.cib.vis.scene.UIContext;
import de.tudresden.cib.vis.scene.VisBuilder;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.PolylineShape;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.widgets.Display;

import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class Draw2dBuilder implements VisBuilder<Draw2dFactory.Draw2dObject, Panel> {
    private Panel chart;
    private XYLayout manager;
    private UIContext uiContext  = new UIContext() {

        private Timer animationThread = new Timer();

        @Override
        public void runInUIContext(Runnable runnable) {
            Display.getDefault().syncExec(runnable);
        }

        @Override
        public void animate(final TreeMap<Integer, ChangeMap> scheduledChanges) {
           TimerTask animation = new TimerTask(){
                int frame = 0;
                int maxFrame = scheduledChanges.lastKey();

               private int advanceFrame(final int current, int maxFrame) {
                   if(scheduledChanges.containsKey(current)) {
                       uiContext.runInUIContext(new Runnable() {
                           public void run() {
                               scheduledChanges.get(current).changeAll();
                           }
                       });
                   }
                   return  (current+1 == maxFrame) ? 0 : current+1;
               }

               @Override
                public void run() {
                    frame = advanceFrame(frame, maxFrame);
                }
            };
            animationThread.schedule(animation, 2000, 40);   // 1 frame = 1 hour schedule, 1 frame = 40 ms animation -> 1 s animation = 1 day schedule time
        }

        @Override
        public void dispose() {
            animationThread.cancel();

        }
    };

    public void init() {
        chart = new Panel();
        chart.setBackgroundColor(ColorConstants.white);
        manager = new XYLayout();
        chart.setLayoutManager(manager);
        // chart.setBorder(new MarginBorder(20));
    }

    public void addPart(Draw2dFactory.Draw2dObject graphicalObject) {
        Rectangle bounds = graphicalObject instanceof PolylineShape
                ? new Rectangle(new Point(), ((PolylineShape) graphicalObject).getPoints().getBounds().getBottomRight())
                : graphicalObject.getBounds();
        chart.add(graphicalObject, bounds.getCopy());
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
