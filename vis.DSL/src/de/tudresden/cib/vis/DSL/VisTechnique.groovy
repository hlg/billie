package de.tudresden.cib.vis.DSL

import de.tudresden.cib.vis.data.bimserver.EMFIfcGeometricAccessor
import de.tudresden.cib.vis.data.bimserver.SimplePluginManager
import de.tudresden.cib.vis.filter.Condition
import de.tudresden.cib.vis.mapping.Configuration
import de.tudresden.cib.vis.mapping.Mapper
import de.tudresden.cib.vis.mapping.PropertyMap
import de.tudresden.cib.vis.scene.VisFactory2D
import de.tudresden.cib.vis.scene.java3d.Java3dBuilder

class VisTechnique<S,T extends VisFactory2D.GraphObject> {
    DSLConfiguration config

    VisTechnique rule(Class<S> source, Class<T> target, Closure closure){
        closure.delegate = this
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        Mapper mapper = Java3dBuilder.createMapper(new EMFIfcGeometricAccessor(createPluginManager(), true));
        config = new DSLConfiguration(mapper)
        config.source = source
        config.target = target
        closure()
        return this;
    }

    void condition(Closure closure){
        config.condition = [matches: closure] as Condition<S>
    }

    void initial(Closure closure){
        config.mapping = new PropertyMap(config.source, config.target){
            @Override
            protected void configure() {

            }
        }
        config.mapping.metaClass.configure = closure
    }

    SimplePluginManager createPluginManager(){
        SimplePluginManager pluginManager = new SimplePluginManager();
        pluginManager.loadPluginsFromCurrentClassloader();
        pluginManager.initAllLoadedPlugins();
        return pluginManager;
    }


    class DSLConfiguration extends Configuration {

        Condition condition
        PropertyMap mapping
        Class<S> source
        Class<T> target

        DSLConfiguration(Mapper mapper) {
            super(mapper)
        }

        @Override
        void config() {
            mapper.addMapping(condition, mapping)
        }
    }

}
