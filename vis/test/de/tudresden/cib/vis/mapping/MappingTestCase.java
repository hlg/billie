package de.tudresden.cib.vis.mapping;

import de.tudresden.cib.vis.scene.VisFactory2D;

public class MappingTestCase {
    DataElement d;

    public void setUp() {
        d = new DataElement();
        d.a = 5;
        d.b = "hello";
    }

    static class DataElement {
        int a;
        String b;
    }

    public static class VisElement implements VisFactory2D.GraphObject {
        public VisElement(){}
        int with;
        String label;
    }
}
