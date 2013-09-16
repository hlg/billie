package de.tudresden.cib.vis.scnen.java2d;

import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.VisFactory2D;

import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.QuadCurve2D;
import java.util.LinkedList;
import java.util.List;

public class Java2dFactory extends VisFactory2D {
    @Override
    protected PropertyMap.Provider<Rectangle> setRectangleProvider() {
        return new PropertyMap.Provider<Rectangle>() {
            @Override
            public Rectangle create() {
                return new Java2DRectangle();
            }
        };
    }

    @Override
    protected PropertyMap.Provider<Label> setLabelProvider() {
        return new PropertyMap.Provider<Label>() {
            @Override
            public Label create() {
                return new Java2dLabel();
            }
        };
    }

    @Override
    protected PropertyMap.Provider<Polyline> setPolylineProvider() {
        return new PropertyMap.Provider<Polyline>() {
            @Override
            public Polyline create() {
                return new Java2dPolyline();
            }
        };
    }

    @Override
    protected PropertyMap.Provider<Bezier> setBezierProvider() {
        return new PropertyMap.Provider<Bezier>() {
            @Override
            public Bezier create() {
                return new Java2dBezier();
            }
        };
    }

    public abstract class Java2DObject implements GraphObject2D {
        Color color = Color.LIGHT_GRAY;
        private boolean background = false;
        private boolean foreground = false;

        @Override
        public void setColor(int r, int g, int b) {
            color = new Color(r, g, b);
        }

        @Override
        public void setBackground() {
            background = true;
        }

        @Override
        public boolean getBackground() {
            return background;
        }

        @Override
        public void setForeground() {
            foreground = true;
        }

        @Override
        public boolean getForeground() {
            return foreground;
        }
        abstract void paint(Graphics2D g);
        abstract java.awt.Rectangle calculateBounds(Graphics2D g);
    }

    public abstract class Java2DShapeObject<T extends Shape> extends Java2DObject {
        T shape;

        void paint(Graphics2D g){
            g.setPaint(color);
            g.fill(shape);
            g.setColor(Color.BLACK);
            g.draw(shape);
        }

        @Override
        java.awt.Rectangle calculateBounds(Graphics2D g) {
            return shape.getBounds();
        }
    }

    private class Java2DRectangle extends Java2DShapeObject<java.awt.Rectangle> implements Rectangle {

        private Java2DRectangle() {
            shape = new java.awt.Rectangle();
        }

        @Override
        public void setLeft(int X) {
            shape.x = X;
        }

        @Override
        public void setTop(int Y) {
            shape.y = Y;
        }

        @Override
        public void setHeight(int height) {
            shape.height = height;
        }

        @Override
        public void setWidth(int width) {
            shape.width = width;
        }

    }

    private class Java2dLabel extends Java2DObject implements Label {
        private int y;
        private int x;
        private String text;
        private boolean vertical;

        private Java2dLabel() {
            color = Color.BLACK;
        }

        @Override
        public void setLeft(int X) {
            this.x = X;
        }

        @Override
        public void setTop(int Y) {
            this.y = Y;
        }

        @Override
        public void setText(String text) {
            this.text = text;
        }

        @Override
        public void setVertical(boolean v) {
            this.vertical = v;
        }

        void paint(Graphics2D g) {
            g.setColor(color);
            g.translate(x, y);
            if(vertical) g.rotate(90);
            g.drawString(text, 0, (int) g.getFont().getStringBounds(text, g.getFontRenderContext()).getHeight());
            g.translate(-x, -y);
        }

        @Override
        java.awt.Rectangle calculateBounds(Graphics2D g) {
            AffineTransform rot = AffineTransform.getRotateInstance(vertical?90:0, x, y);
            GlyphVector glyphVector = g.getFont().createGlyphVector(g.getFontRenderContext(), text);
            return rot.createTransformedShape(glyphVector.getOutline()).getBounds();
        }
    }

    private class Java2dPolyline extends Java2DShapeObject<Polygon> implements Polyline {
        private Java2dPolyline() {
            shape = new Polygon();
            color = Color.BLACK;
        }

        @Override
        public void addLine(int x1, int y1, int x2, int y2) {
            shape.addPoint(x1, y1);
            shape.addPoint(x2, y2);
        }

        @Override
        public void addPoint(int x, int y) {
            shape.addPoint(x, y);
        }
    }

    private class Java2dBezier extends Java2DObject implements Bezier {

        List<Point> pointList = new LinkedList<Point>();

        @Override
        public void addPoint(int x, int y) {
            pointList.add(new Point(x, y));
        }

        @Override
        void paint(Graphics2D g) {
            Point prevCtrl = pointList.get(0);
            Point currCtrl = pointList.get(1);
            Point pt1 = new Point((prevCtrl.x+currCtrl.x)/2, (prevCtrl.y+currCtrl.y)/2);
            g.drawLine(prevCtrl.x, prevCtrl.y, pt1.x, pt1.y);
            if (pointList.size()>2) for (int i = 2; i < pointList.size(); i++) {
                Point nextCtrl = pointList.get(i);
                Point pt2 = new Point((currCtrl.x+nextCtrl.x)/2, (currCtrl.y+nextCtrl.y)/2);
                QuadCurve2D.Double s = new QuadCurve2D.Double(pt1.x, pt1.y, currCtrl.x, currCtrl.y, pt2.x, pt2.y);
                g.draw(s);
                currCtrl = nextCtrl;
                pt1 = pt2;
            }
            g.drawLine(pt1.x, pt1.y, currCtrl.x, currCtrl.y);
        }

        @Override
        java.awt.Rectangle calculateBounds(Graphics2D g) {
            Point prevCtrl = pointList.get(0);
            Point currCtrl = pointList.get(1);
            Point pt1 = new Point((prevCtrl.x+currCtrl.x)/2, (prevCtrl.y+currCtrl.y)/2);
            java.awt.Rectangle bounds = new java.awt.Rectangle();
            bounds.add(prevCtrl);
            bounds.add(currCtrl);
            if (pointList.size()>2) for (int i = 2; i < pointList.size(); i++) {
                Point nextCtrl = pointList.get(i);
                Point pt2 = new Point((currCtrl.x+nextCtrl.x)/2, (currCtrl.y+nextCtrl.y)/2);
                QuadCurve2D.Double s = new QuadCurve2D.Double(pt1.x, pt1.y, currCtrl.x, currCtrl.y, pt2.x, pt2.y);
                bounds = bounds.union(s.getBounds());
                currCtrl = nextCtrl;
                pt1 = pt2;
            }
            bounds.add(pt1);
            bounds.add(currCtrl);
            return bounds;
        }
    }
}
