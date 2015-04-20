package de.tudresden.cib.vis.mapping;

import de.tudresden.cib.vis.scene.VisFactory2D;
import org.junit.Before;

public abstract class MappingTestCase {
    DataElement d;

    void createData() {
        d = new DataElement();
        d.a = 5;
        d.b = "hello";
    }

    static class DataElement {
        int a;
        String b;
    }

    static class VisElement implements VisFactory2D.GraphObject {
        public VisElement(){}
        int with;
        String label;

        public void setColor(int r, int g, int b) {

        }
    }
}
