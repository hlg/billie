package de.tudresden.cib.vis.scene.draw2d;

import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.VisFactory2D;
import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.Point;
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

    class Draw2dPolyline extends org.eclipse.draw2d.Polyline implements Polyline, Draw2dObject {

        public void addLine(int x1, int y1, int x2, int y2) {
        }

        public void addPoint(int x, int y) {
            addPoint(new Point(x, y));
        }

        public void setColor(int r, int g, int b) {
            setForegroundColor(new Color(null, r, g, b));
        }
    }
}
