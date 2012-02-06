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

    static class VisElement {
        public VisElement(){}
        int with;
        String label;
    }
}
