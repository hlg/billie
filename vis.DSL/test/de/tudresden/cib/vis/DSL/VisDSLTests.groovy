package de.tudresden.cib.vis.DSL;

import de.tudresden.cib.vis.mapping.Configuration
import de.tudresden.cib.vis.scene.VisFactory3D
import org.bimserver.models.ifc2x3tc1.IfcObject;
import org.junit.Before;
import org.junit.Test;

public class VisDSLTests {

    @Before
    public void setup(){

    }

    @Test
    public void testSimpleMapping(){
        VisTechnique visTechnique = new VisDSL().vt {
            rule(IfcObject, VisFactory3D.Polyeder) {
                condition {
                    data.obj type IfcBuildingElement
                }
                initial {
                    graph.vert = data.geo.vert
                    graph.norm = data.geo.norm
                    graph.ind = data.geo.ind
                    graph.color = rgba(128,128,128,150)
                }
            }
        }
        Configuration config = visTechnique.config;
        config.mapper;   // TODO: check the config
    }

}
