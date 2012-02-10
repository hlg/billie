package visualization;

import mapping.PropertyMap;

public class Draw2dFactory extends VisFactory {

    @Override
    protected PropertyMap.Provider<Rectangle> setRectangleProvider() {
        return new PropertyMap.Provider<Rectangle>() {
            public Rectangle create() {
                return new Draw2dRectangle() {
                };
            }
        };
    }

    @Override
    protected PropertyMap.Provider<Label> setLabelProvider() {
        return new PropertyMap.Provider<Label>() {
            public Label create() {
                return new Draw2dLabel() {
                };
            }
        };
    }

    class Draw2dRectangle implements Rectangle {

        public void setWidth(int a) {
        }
    }
    
    class Draw2dLabel implements Label {

    }
}
