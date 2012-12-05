package de.tudresden.cib.vis.scnen.java2d;

import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.scene.ChangeMap;
import de.tudresden.cib.vis.scene.UIContext;
import de.tudresden.cib.vis.scene.VisBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

public class Java2DBuilder implements VisBuilder<Java2dFactory.Java2DObject, JPanel> {

    private Canvas2D canvas;

    private UIContext uiContext = new UIContext() {
        @Override
        public void runInUIContext(Runnable runnable) {
            runnable.run(); // Timer runs already in UI context
        }

        @Override
        public void animate(final TreeMap<Integer, ChangeMap> scheduledChanges) {
            Timer animationThread = new Timer(40, new ActionListener() {
                int frame = 0;
                int maxFrame = scheduledChanges.lastKey();

                private int advanceFrame(final int current, int maxFrame) {
                    if(scheduledChanges.containsKey(current)) {
                        uiContext.runInUIContext(new Runnable() {
                            public void run() {
                                scheduledChanges.get(current).changeAll();
                                canvas.repaint();
                            }
                        });
                    }
                    return  (current+1 == maxFrame) ? 0 : current+1;
                }
                @Override
                public void actionPerformed(ActionEvent e) {
                    frame = advanceFrame(frame, maxFrame);
                }
            });
            animationThread.setInitialDelay(2000);   // 1 frame = 1 hour schedule, 1 frame = 40 ms animation -> 1 s animation = 1 day schedule time
            animationThread.start();
        }

        @Override
        public void dispose() {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    };

    @Override
    public void init() {
        canvas = new Canvas2D();
        canvas.setBackground(Color.WHITE);
    }

    @Override
    public void addPart(Java2dFactory.Java2DObject graphicalObject) {
        canvas.addPart(graphicalObject);
    }

    @Override
    public void finish() {
    }

    @Override
    public JPanel getScene() {
        return canvas;
    }

    @Override
    public UIContext getUiContext() {
        return uiContext;
    }


    private class Canvas2D extends JPanel {
        private List<Java2dFactory.Java2DObject> parts = new LinkedList<Java2dFactory.Java2DObject>();

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if(g instanceof Graphics2D){
                Graphics2D graphics2D = (Graphics2D) g;
                for(Java2dFactory.Java2DObject object: parts) object.paint(graphics2D);
            }
        }

        public void addPart(Java2dFactory.Java2DObject part) {
            parts.add(part);
        }

        @Override
        public Dimension getPreferredSize() {
            Rectangle bounds = new Rectangle();
            for(Java2dFactory.Java2DObject part: parts) {
                bounds = bounds.union(part.calculateBounds((Graphics2D) getGraphics()));
            }
            return bounds.getSize();
        }
    }

    public static <E> Mapper<E, Java2dFactory.Java2DObject, JPanel> createMapper(DataAccessor<E> data){
        return new Mapper<E, Java2dFactory.Java2DObject, JPanel>(data, new Java2dFactory(), new Java2DBuilder());
    }
}
