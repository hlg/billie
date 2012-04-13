package visualization;

import mapping.PropertyMap;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
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
    }
    
    class Draw2dLabel extends org.eclipse.draw2d.Label implements Label, Draw2dObject {
        Draw2dLabel(){
            setFont(defaultFont);
        }
        public void setLeft(int X) {
            setLocation(getLocation().setX(X));
        }
        public void setTop(int Y) {
            setLocation(getLocation().setY(Y));
        }
        public void setText(String text){
            super.setText(text);
            setLabelAlignment(LEFT);
            setSize(getTextSize());
        }
   }
}
