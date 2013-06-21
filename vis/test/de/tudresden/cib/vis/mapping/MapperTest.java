package de.tudresden.cib.vis.mapping;

import de.tudresden.cib.vis.data.CollectionAccessor;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.scene.*;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

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
        FakeVisBuilder<FakeRectangle> builder = new FakeVisBuilder<FakeRectangle>();
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

    private Mapper<DataElement, FakeRectangle, Object> makeMapper(FakeVisBuilder<FakeRectangle> builder) {
        DataAccessor<DataElement> data = new CollectionAccessor(Collections.singletonList(d));
        VisFactory2D factory = new FakeVisFactoy();
        return new Mapper<DataElement, FakeRectangle, Object>(data, factory, builder);
    }

    @Test
    public void testChange() throws TargetCreationException {
        // TODO: tests to much (integration tests?)
        FakeVisBuilder<FakeRectangle> builder = new FakeVisBuilder<FakeRectangle>();
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
        FakeVisBuilder<FakeRectangle> builder = new FakeVisBuilder<FakeRectangle>();
        Mapper<DataElement, FakeRectangle, Object> test = makeMapper(builder);
        test.addMapping(new PropertyMap<DataElement, VisFactory2D.Rectangle>() {
            @Override
            protected void configure() {
                graphObject.setWidth(10);
                addChange(EventX.CLICK, new Change<VisFactory2D.Rectangle>() {
                    @Override
                    protected void configure() {
                        graph.setWidth(1000);
                    }
                });
                addTrigger(EventX.CLICK);
            }
        });
        SceneManager result = test.map();
        FakeRectangle graph = builder.parts.get(0);
        assertEquals(10, graph.a);
        result.fire(EventX.CLICK, graph);
        assertEquals(1000, graph.a);
    }

    @Test
    public void testTriggerOther() throws TargetCreationException {
        FakeVisBuilder<FakeRectangle> builder = new FakeVisBuilder<FakeRectangle>();
        Mapper<DataElement, FakeRectangle, Object> test = makeMapper(builder);
        test.addMapping(new PropertyMap<DataElement, VisFactory2D.Rectangle>() {
            @Override
            protected void configure() {
                graphObject.setWidth(10);
                addChange(EventX.CLICK, new Change<VisFactory2D.Rectangle>() {
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
                addTrigger(EventX.CLICK);
            }
        });
        SceneManager result = test.map();
        FakeRectangle receiving = builder.parts.get(0);
        FakeRectangle triggering = builder.parts.get(1);
        assertEquals(10, receiving.a);
        result.fire(EventX.CLICK, triggering);
        assertEquals(1000, receiving.a);
    }

    @Test
    public void testEventX() throws TargetCreationException {
        // TODO: tests to much (integration tests?)
       FakeVisBuilder<FakeRectangle> builder = new FakeVisBuilder<FakeRectangle>();
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
                addChange(EventX.CLICK, theChange);
            }
        });
        SceneManager result = test.map();
        VisFactory2D.Rectangle generatedGraph = builder.parts.get(0);
        List<Change> changes = result.getChanges(EventX.CLICK, generatedGraph);
        assertTrue(changes.contains(theChange));
        assertEquals(1, changes.size());
    }

    @Test
    public void testDataEventX() throws TargetCreationException {
        FakeVisBuilder<FakeRectangle> builder = new FakeVisBuilder<FakeRectangle>();
        Mapper<DataElement, FakeRectangle, Object> test = makeMapper(builder);
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
                addChange(EventX.HIGHLIGHT, theChange);
            }
        });
        SceneManager result = test.map();
        FakeRectangle generatedGraph = builder.parts.get(0);
        assertEquals(5, generatedGraph.a);
        result.fire(EventX.HIGHLIGHT, d);
        assertEquals(100,generatedGraph.a);
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

    public static class FakeVisBuilder<F extends VisFactory2D.GraphObject> implements VisBuilder<F, Object> {
        public List<F> parts = new ArrayList<F>();

        public void init() {
        }

        public void addPart(F graphicalObject) {
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

        @Override
        public void addTriggers(Event event, Collection<VisFactory2D.GraphObject> triggers, SceneManager<?, Object> sceneManager) {
            //To change body of implemented methods use File | Settings | File Templates.
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

        @Override
        public void setBackground() {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void setForeground() {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean getBackground() {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean getForeground() {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public enum EventX implements Event  {
        HIGHLIGHT, CLICK
    }
}
