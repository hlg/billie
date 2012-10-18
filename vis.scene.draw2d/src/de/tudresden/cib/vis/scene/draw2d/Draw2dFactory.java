package de.tudresden.cib.vis.scene.draw2d;

import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.VisFactory2D;
import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

public class Draw2dFactory extends VisFactory2D {

    private Font defaultFont;
    private Color defaultColor;

    public Draw2dFactory(Font font){
        defaultFont = font;
        defaultColor = ColorConstants.lightGray;
    }
    
    @Override
    protected PropertyMap.Provider<Rectangle> setRectangleProvider() {
        return new PropertyMap.Provider<Rectangle>() {
            public Rectangle create() {
                return new Draw2dRectangle();
            }
        };
    }

    @Override
    protected PropertyMap.Provider<Label> setLabelProvider() {
        return new PropertyMap.Provider<Label>() {
            public Label create() {
                return new Draw2dLabel();
            }
        };
    }

    @Override
    protected PropertyMap.Provider<Polyline> setPolylineProvider() {
        return new PropertyMap.Provider<Polyline>(){
            public Polyline create() {
                return new Draw2dPolyline();
            }
        };
    }

    @Override
    protected PropertyMap.Provider<Bezier> setBezierProvider() {
        return new PropertyMap.Provider<Bezier>() {
            @Override
            public Bezier create() {
                return new Draw2dBezier();
            }
        };
    }

    public interface Draw2dObject extends GraphObject, IFigure {}  // marker interface

    class Draw2dRectangle extends RectangleFigure implements Rectangle, Draw2dObject {
        
        public Draw2dRectangle(){
           setBackgroundColor(defaultColor);
        }
        public void setLeft(int X){
            setLocation(getLocation().setX(X));
        }
        public void setTop(int Y){
            setLocation(getLocation().setY(Y));
        }
        public void setHeight(int height){
            setSize(getSize().setHeight(height));
        }
        public void setWidth(int width) {
            setSize(getSize().setWidth(width));
        }
        public void setColor(int r, int g, int b){
            setBackgroundColor(new Color(null, r, g, b));
        }
    }
    
    class Draw2dLabel extends org.eclipse.draw2d.Label implements Label, Draw2dObject {
        private float rotation = 0;
        Draw2dLabel(){
            setFont(defaultFont);
        }

        @Override
        protected void paintFigure(Graphics graphics) {
            org.eclipse.draw2d.geometry.Rectangle bounds = getBounds();
            graphics.translate(bounds.x, bounds.y);
            graphics.rotate(rotation);
            graphics.setClip(new org.eclipse.draw2d.geometry.Rectangle(0,0,bounds.width, bounds.height));
            graphics.drawText(getSubStringText(), getTextLocation());
            graphics.translate(-bounds.x, -bounds.y);
        }

        public void setLeft(int X) {
            setLocation(getLocation().setX(X));
        }
        public void setTop(int Y) {
            setLocation(getLocation().setY(Y));
        }
        public void setText(String text){
            super.setText(text);
            setLabelAlignment(PositionConstants.LEFT);
            setSize(getTextSize());
        }

        public void setRotation(int i) {
            this.rotation = i;
        }

        public void setColor(int r, int g, int b) {
            setForegroundColor(new Color(null, r, g, b));
        }
    }

    class Draw2dPolyline extends PolylineShape implements Polyline, Draw2dObject {

        public void addLine(int x1, int y1, int x2, int y2) {
        }

        public void addPoint(int x, int y) {
            addPoint(new Point(x, y));
        }

        public void setColor(int r, int g, int b) {
            setForegroundColor(new Color(null, r, g, b));
        }
    }

    class Draw2dBezier extends org.eclipse.draw2d.Polyline implements Bezier, Draw2dObject{

        @Override
        public void addPoint(int x, int y) {
            addPoint(new Point(x, y));
        }

        @Override
        public void setColor(int r, int g, int b) {
            setForegroundColor(new Color(null, r, g, b));
        }

        @Override
        protected void outlineShape(Graphics g) {
            PointList pointList = getPoints();

            Point prevCtrl = pointList.getPoint(0);
            Point currCtrl = pointList.getPoint(1);
            Point pt1 = new Point((prevCtrl.x+currCtrl.x)/2, (prevCtrl.y+currCtrl.y)/2);
            g.drawLine(prevCtrl, pt1);
            if (pointList.size()>2) for (int i = 2; i < pointList.size(); i++) {
                Point nextCtrl = pointList.getPoint(i);
                Point pt2 = new Point((currCtrl.x+nextCtrl.x)/2, (currCtrl.y+nextCtrl.y)/2);
                drawBezier(g, pt1, currCtrl, pt2, currCtrl, 0.1);
                currCtrl = nextCtrl;
                pt1 = pt2;
            }
            g.drawLine(pt1, currCtrl);
        }
    }

    static class BezierDimension {
        double a, b, c, d;
        /* based on: http://www.moshplant.com/direct-or/bezier/math.html
           *
           * given (in dimension):
           *  p0 - start point
           *  p1 - start control point
           *  p2 - end control point
           *  p3 - end point
           *
           * based on the spec:
           *  value(t) = a.t.t.t + b.t.t + c.t + d
           *  p0 = d
           *  p1 = p0+c/3
           *  p2 = p1+(c+b)/3
           *  p3 = p0+c+b+a
           */
        public BezierDimension(int p0, int p1, int p2, int p3) {
            d = p0;
            c = 3 * (p1 - p0);
            b = 3 * (p2 - p1) - c;
            a = p3 - p0 - c - b;
        }
        public int getValue(double t) {
            // added 0.5 so that floor is actually rounding
            return (int) (a*t*t*t + b*t*t + c*t + d + 0.5);
        }

    }

    static private void drawBezier(Graphics g,
                                   Point startPt,
                                   Point startCtrlPt,
                                   Point endPt,
                                   Point endCtrlPt,
                                   double step) {
        BezierDimension x = new BezierDimension(startPt.x, startCtrlPt.x, endCtrlPt.x, endPt.x);
        BezierDimension y = new BezierDimension(startPt.y, startCtrlPt.y, endCtrlPt.y, endPt.y);
        double t = 0;
        Point midPt = startPt;
        while (t < 1) {
            Point nextPt = new Point();
            nextPt.setLocation(x.getValue(t), y.getValue(t));
            g.drawLine(midPt, nextPt);
            t += step;
            midPt = nextPt;
        }
        g.drawLine(midPt, endPt);
    }
}
