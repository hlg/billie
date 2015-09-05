package de.tudresden.cib.vis.DSL

import de.tudresden.cib.vis.filter.Condition
import de.tudresden.cib.vis.mapping.Configuration
import de.tudresden.cib.vis.mapping.PropertyMap
import de.tudresden.cib.vis.scene.VisFactory2D

class VisTechnique<S,T extends VisFactory2D.GraphObject> {   // TODO: consistent naming - VisTechnique is currently the equivalent of Configuration, with DSL instead of subclassing
    Condition condition
    Class<S> source
    Class<T> target
    Configuration config

    VisTechnique(Configuration config){
        this.config = config
    }

    VisTechnique rule(Class<S> source, Class<T> target, Closure closure){   // "constructor" of the vis technique?
        this.source = source
        this.target = target
        this.with(closure)
        return this
    }

    void condition(Closure closure){
        condition = new Condition<S>(){
            S data
            @Override
            boolean matches(S data) {
                this.data = data
                this.with(closure)
            }
        }
    }

    void initial(Closure closure){
        PropertyMap mapping = new PropertyMap(source, target){
            @Override
            protected void configure() {
                this.with(closure)
            }
        }
        config.addMapping(condition, mapping)
    }

}
