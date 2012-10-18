package de.tudresden.cib.vis.mapping;

import de.tudresden.cib.vis.data.CollectionAccessor;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.scene.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class MapperTest extends MappingTestCase {
    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testFold() {
        DataAccessor.Folding<String, String> folder = new DataAccessor.Folding<String, String>("go") {
            @Override
            public String function(String aggregator, String element) {
                return aggregator + "." + element;
            }
        };
        folder.fold(Arrays.asList("a", "b", "c"));
        assertEquals("go.a.b.c", folder.getResult());
    }

    @Test
    public void testMapping() throws TargetCreationException {
        FakeVisBuilder builder = new FakeVisBuilder();
        Mapper<DataElement, FakeRectangle, Object> test = makeMapper(builder);
        test.addMapping(
                new PropertyMap<DataElement, VisFactory2D.Rectangle>() {
                    @Override
                    protected void configure() {
                        graphObject.setWidth(data.a);
                    }
                }
        );
        SceneManager<DataElement,?> result = test.map();
        assertEquals(1, builder.parts.size());
        VisFactory2D.GraphObject expected = builder.parts.get(0);
        assertEquals(d.a, ((FakeRectangle) expected).a);
        assertEquals(expected, result.getFirstGraph(d));
        assertEquals(d, result.getData(expected));
    }

    private Mapper<DataElement, FakeRectangle, Object> makeMapper(FakeVisBuilder builder) {
        DataAccessor<DataElement> data = new CollectionAccessor(Collections.singletonList(d));
        VisFactory2D factory = new FakeVisFactoy();
        return new Mapper<DataElement, FakeRectangle, Object>(data, factory, builder);
    }

    @Test
    public void testChange() throws TargetCreationException {
        // TODO: tests to much (integration tests?)
        FakeVisBuilder builder = new FakeVisBuilder();
        Mapper test = makeMapper(builder);
        final Change<VisFactory2D.Rectangle> theChange = new Change<VisFactory2D.Rectangle>(){
            @Override
            protected void configure() {
                graph.setWidth(100);
            }
        };
        test.addMapping(new PropertyMap<DataElement, VisFactory2D.Rectangle>() {
            @Override
            protected void configure() {
                graphObject.setWidth(data.a);
                addChange(0, theChange);
           }
        });
        SceneManager result = test.map();
        VisFactory2D.Rectangle generatedGraph = builder.parts.get(0);
        List<Change> changes = result.getChanges(0, generatedGraph);
        assertTrue(changes.contains(theChange));
        assertEquals(1, changes.size());
    }

    @Test
    public void testTriggerSelf() throws TargetCreationException {
        FakeVisBuilder builder = new FakeVisBuilder();
        Mapper<DataElement, FakeRectangle, Object> test = makeMapper(builder);
        test.addMapping(new PropertyMap<DataElement, VisFactory2D.Rectangle>() {
            @Override
            protected void configure() {
                graphObject.setWidth(10);
                addChange(Event.CLICK, new Change<VisFactory2D.Rectangle>() {
                    @Override
                    protected void configure() {
                        graph.setWidth(1000);
                    }
                });
            }
        });
        SceneManager result = test.map();
        FakeRectangle graph = builder.parts.get(0);
        assertEquals(10, graph.a);
        result.fire(Event.CLICK, graph);
        assertEquals(1000, graph.a);
    }

    @Test
    public void testTriggerOther() throws TargetCreationException {
        FakeVisBuilder builder = new FakeVisBuilder();
        Mapper<DataElement, FakeRectangle, Object> test = makeMapper(builder);
        test.addMapping(new PropertyMap<DataElement, VisFactory2D.Rectangle>() {
            @Override
            protected void configure() {
                graphObject.setWidth(10);
                addChange(Event.CLICK, new Change<VisFactory2D.Rectangle>() {
                    @Override
                    protected void configure() {
                        graph.setWidth(1000);
                    }
                });
            }
        });
        test.addMapping(new PropertyMap<DataElement, VisFactory2D.Rectangle>() {
            @Override
            protected void configure() {
                addTrigger(Event.CLICK);
            }
        });
        SceneManager result = test.map();
        FakeRectangle receiving = builder.parts.get(0);
        FakeRectangle triggering = builder.parts.get(1);
        assertEquals(10, receiving.a);
        result.fire(Event.CLICK, triggering);
        assertEquals(1000, receiving.a);
    }

    @Test
    public void testEvent() throws TargetCreationException {
        // TODO: tests to much (integration tests?)
       FakeVisBuilder builder = new FakeVisBuilder();
       Mapper test = makeMapper(builder);
       final Change<VisFactory2D.Rectangle> theChange = new Change<VisFactory2D.Rectangle>() {
           @Override
           protected void configure() {
               graph.setWidth(100);
           }
       };
        test.addMapping(new PropertyMap<DataElement, VisFactory2D.Rectangle>() {
            @Override
            protected void configure() {
                graphObject.setWidth(data.a);
                addChange(Event.CLICK, theChange);
            }
        });
        SceneManager result = test.map();
        VisFactory2D.Rectangle generatedGraph = builder.parts.get(0);
        List<Change> changes = result.getChanges(Event.CLICK, generatedGraph);
        assertTrue(changes.contains(theChange));
        assertEquals(1, changes.size());
    }

    public static class FakeVisFactoy extends VisFactory2D {
            @Override
            protected PropertyMap.Provider<VisFactory2D.Rectangle> setRectangleProvider() {
                return new PropertyMap.Provider<Rectangle>() {
                    public Rectangle create() {
                        return new FakeRectangle();
                    }
                };
            }

            @Override
            protected PropertyMap.Provider<VisFactory2D.Label> setLabelProvider() {
                return null;
            }

            @Override
            protected PropertyMap.Provider<Polyline> setPolylineProvider() {
                return null;
            }

        @Override
        protected PropertyMap.Provider<Bezier> setBezierProvider() {
            return null;
        }
    }

    public static class FakeVisBuilder implements VisBuilder<FakeRectangle, Object> {
        List<FakeRectangle> parts = new ArrayList<FakeRectangle>();

        public void init() {
        }

        public void addPart(FakeRectangle graphicalObject) {
            parts.add(graphicalObject);
        }

        public void finish() {
        }

        public Object getScene() {
            return null;
        }
        public UIContext getUiContext() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

    }

    public static class FakeRectangle implements VisFactory2D.Rectangle {
        protected int a;

        public void setLeft(int X) {
        }

        public void setTop(int Y) {
        }

        public void setHeight(int height) {
        }

        public void setWidth(int width) {
            this.a = width;
        }

        public void setColor(int r, int g, int b) {
        }
    }
}
