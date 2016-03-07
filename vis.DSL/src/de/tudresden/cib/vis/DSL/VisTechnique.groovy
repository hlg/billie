package de.tudresden.cib.vis.DSL

import de.tudresden.cib.vis.filter.Condition
import de.tudresden.cib.vis.mapping.ClassMap
import de.tudresden.cib.vis.mapping.Configuration
import de.tudresden.cib.vis.mapping.PropertyMap
import de.tudresden.cib.vis.scene.Change
import de.tudresden.cib.vis.scene.VisFactory2D

class VisTechnique {
    // TODO: consistent naming - VisTechnique is currently the equivalent of Configuration, with DSL instead of subclassing

    View view = new View()
    List<Rule> rules = new ArrayList<Rule>()

    String accessor = 'de.tudresden.cib.vis.data.bimserver.EMFIfcGeometricAccessor'

    void rule(Class source, Class target, Closure closure){   // "constructor" of the vis technique?
        Rule rule = new Rule()
        rule.source = source
        rule.target = target
        rule.with(closure)
        rules.add(rule)
    }

    void view(Closure closure){
        view.with(closure)
    }

}

class View {
    Dimension dimension = Dimension.D3D
    Projection projection = Projection.PERSPEKTIVE
}

class Rule<S,T extends VisFactory2D.GraphObject> {
    Condition condition
    Class<S> source
    Class<T> target
    Configuration config = new Configuration()

    void condition(Closure closure){
        condition = new Condition<S>(){
            def data;
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

    void update(int time, Closure closure){
        Change<T> change = new Change<T>() {
            @Override
            protected void configure() {
                this.with(closure)
            }
        }
        config.getPropertyMapsByConditions().each { condition, ClassMap classMap ->
            classMap.each { Class c, List<PropertyMap<S,T>> pms ->
                pms.each{ pm -> pm.addChange(time, change) }
            }
        }
    }


}
enum Dimension {
    D2D, D3D
}

enum Projection {
    PERSPEKTIVE, ISOMETRIC
}
