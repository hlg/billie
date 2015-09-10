package de.tudresden.cib.vis.DSL

import de.tudresden.cib.vis.mapping.ClassMap
import de.tudresden.cib.vis.mapping.Configuration
import de.tudresden.cib.vis.mapping.PropertyMap

public class VisDSLTests extends GroovyTestCase {

    public void setUp(){

    }

    public void testSimpleMapping(){
        VisTechnique vt = new VisTechnique()
        vt.rule(Number, Object) {
                condition {
                    data == 3
                }
                initial {
                    graphObject.setColor(data,0,0)
                }
        }
        Configuration configuration = vt.getConfig()
        Map<?,ClassMap> propMapsByConditions = configuration.getPropertyMapsByConditions()
        assert propMapsByConditions.size()==1
        ClassMap classMap = propMapsByConditions.values().first()
        assert classMap.size() == 1
        List<PropertyMap> propMaps = classMap.values().first()
        assert propMaps.size() == 1
    }

}
