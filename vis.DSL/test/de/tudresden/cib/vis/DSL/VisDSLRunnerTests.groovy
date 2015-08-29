package de.tudresden.cib.vis.DSL

import de.tudresden.cib.vis.runtime.java3d.viewers.SimpleViewer
import org.junit.Test

class VisDSLRunnerTests {

    public void testDefabultSetup(){
        // assumes default Accessor and Vismodel
        // otherwise "create Draw2d from EMFIfc"

        SimpleViewer fullResult = dsl.evaluate(visModel);
        fullResult.loader.mapper;
        fullResult.axometric;
        fullResult.pickingEnabled;
    }

    @Test
    public void testAccessorConfig(){
        // split into more methods
    }

    @Test
    public void testVisModelConfig(){
        // split into more methods
    }

}
