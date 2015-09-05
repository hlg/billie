package de.tudresden.cib.vis.configurations;

import de.mefisto.model.container.Content;
import de.mefisto.model.container.I;
import de.tudresden.cib.vis.data.DataAccessException;
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser;
import de.tudresden.cib.vis.data.bimserver.SimplePluginManager;
import de.tudresden.cib.vis.data.multimodel.EMTypeCondition;
import de.tudresden.cib.vis.data.multimodel.EMTypes;
import de.tudresden.cib.vis.data.multimodel.LinkedObject;
import de.tudresden.cib.vis.data.multimodel.MultiModelAccessor;
import de.tudresden.cib.vis.filter.Condition;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.mapping.TargetCreationException;
import de.tudresden.cib.vis.scene.*;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.Object;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class IfcGaeb_Colored3D_Tests {
    @Test
    public void testConfiguration() throws TargetCreationException, IOException, DataAccessException {

        MultiModelAccessor<EMFIfcParser.EngineEObject> mmAccessor = new MultiModelAccessor<EMFIfcParser.EngineEObject>(createPluginManager());
        List<String> ids = mmAccessor.read(new File(this.getClass().getResource("/resources/carport").getFile()),new EMTypeCondition(EMTypes.IFC), new EMTypeCondition(EMTypes.GAEB){
            @Override
            public boolean isValidFor(Content alternative) {
                for (I option : alternative.getContentOptions().getI()){
                    if(option.getK().equals("extension") && option.getV().equals("DA84")) return true;
                }
                return false;
            }
        }, new EMTypeCondition(EMTypes.QTO));

        VisFactory3D visFactory = new VisFactory3D() {
            @Override
            protected PropertyMap.Provider<Polyeder> setPolyederProvider() {
                return new PropertyMap.Provider<Polyeder>() {
                    @Override
                    public Polyeder create() {
                        return new FakePolyeder();
                    }
                };  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            protected PropertyMap.Provider<Rectangle> setRectangleProvider() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            protected PropertyMap.Provider<Label> setLabelProvider() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            protected PropertyMap.Provider<Polyline> setPolylineProvider() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            protected PropertyMap.Provider<Bezier> setBezierProvider() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        };
        FakeVisBuilder<FakePolyeder> visBuilder = new FakeVisBuilder<FakePolyeder>();
        IfcGaeb_Colored3D config = new IfcGaeb_Colored3D<Object>();
        config.gaebX84Id = ids.get(1);
        config.gaebX83Id = ids.get(1);
        config.config();
        new Mapper<LinkedObject<EMFIfcParser.EngineEObject>, Condition<LinkedObject<EMFIfcParser.EngineEObject>>, FakePolyeder, Object>(
                mmAccessor,
                visFactory,
                visBuilder
        ).map(config);
        assertEquals(5, visBuilder.parts.size());
    }

    private SimplePluginManager createPluginManager(){
        SimplePluginManager pluginManager = new SimplePluginManager();
        pluginManager.loadPluginsFromCurrentClassloader();
        pluginManager.initAllLoadedPlugins();
        return pluginManager;
    }

    private class FakePolyeder implements VisFactory3D.Polyeder {
        @Override
        public void setColor(int r, int g, int b) {
            // TODO: check the colors
        }

        @Override
        public void setVertizes(List<Float> vertizes) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void setNormals(List<Float> normals) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void setIndizes(List<Integer> indizes) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void setColor(int R, int G, int B, int alpha) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    private class FakeVisBuilder<T extends VisFactory2D.GraphObject> implements VisBuilder<T,Object> {
        List<T> parts = new ArrayList<T>();

        @Override
        public void init() {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void addPart(T graphicalObject) {
            parts.add(graphicalObject);
        }

        @Override
        public void finish() {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Object getScene() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public UIContext getUiContext() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void addTriggers(Event event, Collection<VisFactory2D.GraphObject> triggers, SceneManager<?, Object> sceneManager) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
