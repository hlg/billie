package de.tudresden.cib.vis.scene.text;

import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.VisFactory2D;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class TextFactory extends VisFactory2D {

    interface TextGraphObject extends GraphObject {
    }

    @Override
    protected PropertyMap.Provider<Rectangle> setRectangleProvider() {
        return null; // TODO: default Rectangle?
    }

    @Override
    protected PropertyMap.Provider<Label> setLabelProvider() {
        return new PropertyMap.Provider<Label>() {
            public Label create() {
                return new TextLabel();
            }
        };
    }

    @Override
    protected PropertyMap.Provider<Polyline> setPolylineProvider() {
        return null;  // TODO: default Polyline?
    }

    @Override
    protected PropertyMap.Provider<Bezier> setBezierProvider() {
        return null;  // TODO: default Bezier?
    }

    public class TextLabel implements Label, TextGraphObject { // TODO default?
        int x = 0;
        int y = 0;

        public String text;

        public void setLeft(int X) {
            this.x = X;
        }
        public void setTop(int Y) {
            this.y = Y;
        }
        public void setText(String text) {
            this.text = text;
        }

        public void setRotation(int i) {
            // ignore
        }

        public void setColor(int r, int g, int b) {
            throw new NotImplementedException();
        }
    }
}
