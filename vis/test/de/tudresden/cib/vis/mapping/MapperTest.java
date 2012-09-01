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
        FakeVisBuilder builder = new FakeVisBuilder();
        Mapper test = makeMapper(builder);
        test.addMapping(
                new PropertyMap<DataElement, VisFactory2D.Rectangle>() {
                    @Override
                    protected void configure() {
                        graphObject.setWidth(data.a);
                    }
                }
        );
        test.map();
        assertEquals(1, builder.parts.size());
        VisFactory2D.GraphObject expected = builder.parts.get(0);
        assertEquals(d.a, ((FakeRectangle) expected).a);
        assertEquals(expected, test.getGraph(d));
        assertEquals(d, test.getData(expected));
    }

    private Mapper makeMapper(FakeVisBuilder builder) {
        DataAccessor data = new CollectionAccessor(Collections.singletonList(d));
        VisFactory2D factory = new FakeVisFactoy();
        return new Mapper(data, factory, builder);
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
        test.map();
        VisFactory2D.Rectangle generatedGraph = (VisFactory2D.Rectangle) builder.parts.get(0);
        TimeLine<? extends VisFactory2D.Rectangle> generatedTimeline = test.getSceneManager().getTimeLine(VisFactory2D.Rectangle.class);
        Set<? extends VisFactory2D.Rectangle> toBeChanged = generatedTimeline.get(0).get(theChange);
        assertTrue(toBeChanged.contains(generatedGraph));
        assertEquals(1,toBeChanged.size());
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
        test.map();
        VisFactory2D.Rectangle generatedGraph = (VisFactory2D.Rectangle) builder.parts.get(0);
        List<Change> changes = test.getSceneManager().getEvents(Event.CLICK).get(generatedGraph);
        assertTrue(changes.contains(theChange));
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
    }

    public static class FakeVisBuilder implements VisBuilder<VisFactory2D.GraphObject, Object> {
        List<VisFactory2D.GraphObject> parts = new ArrayList<VisFactory2D.GraphObject>();

        public void init() {
        }

        public void addPart(VisFactory2D.GraphObject graphicalObject) {
            parts.add(graphicalObject);
        }

        public void finish() {
        }

        public Object getScene() {
            return null;
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
    }
}
