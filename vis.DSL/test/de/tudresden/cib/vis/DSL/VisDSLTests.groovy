package de.tudresden.cib.vis.DSL

import de.tudresden.cib.vis.filter.Condition
import de.tudresden.cib.vis.mapping.Mapper
import de.tudresden.cib.vis.mapping.PropertyMap
import de.tudresden.cib.vis.scene.SceneManager
import de.tudresden.cib.vis.scene.VisFactory2D
import de.tudresden.cib.vis.scene.VisFactory3D

public class VisDSLTests extends GroovyTestCase {

    public void setUp(){

    }

    public void testSimpleMapping(){
        def mapper = new FakeMapper()
        VisTechnique visTechnique = new VisDSL().vt(mapper) {
            rule(Number, VisFactory2D.GraphObject) {
                condition {
                    data == 3
                }
                initial {
                    graphObject.setColor(data,0,0)
                }
            }
        }
        assert visTechnique.mapper.mappingCount == 1;   // TODO: check the mapper
    }

    class FakeMapper extends Mapper  {
        def mappingCount = 0
        void addMapping (Condition c, PropertyMap pm){
            assert c.matches(3)
            def target = new VisFactory2D.GraphObject(){
                int n
                @Override
                void setColor(int r, int g, int b) {
                    n=r
                }
            }
            pm.with([addMapped:{d,g -> }] as SceneManager)
            pm.map(3, target, 0)
            assert target.n == 3
            mappingCount++
        }
        FakeMapper() {
            super(null, null, null)
        }
    }
}
