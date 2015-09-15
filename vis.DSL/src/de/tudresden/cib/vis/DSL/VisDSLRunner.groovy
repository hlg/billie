package de.tudresden.cib.vis.DSL

import de.tudresden.cib.vis.data.bimserver.EMFIfcGeometricAccessor
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser
import de.tudresden.cib.vis.data.bimserver.SimplePluginManager
import de.tudresden.cib.vis.mapping.Configuration
import de.tudresden.cib.vis.runtime.java3d.viewers.SimpleViewer
import de.tudresden.cib.vis.sampleApps.MappedJ3DLoader

if(args.length>0){
    def technique = new VisTechnique()
    def binding = new Binding([vt: technique])

    GroovyShell shell = new GroovyShell(binding)
    shell.evaluate(new File(args[0]))
    Configuration config = technique.config
    def pm = new SimplePluginManager()
    MappedJ3DLoader<EMFIfcParser.EngineEObject> loader = new MappedJ3DLoader<EMFIfcParser.EngineEObject>(new EMFIfcGeometricAccessor(pm, true), config);

    SimpleViewer viewer = new SimpleViewer(loader);
    viewer.setAxonometric(true);
    viewer.setPickingEnabled(true);
    viewer.chooseAndRun(args.length>1?args[1]:System.getProperty("user.dir"), "ifc",false);
} else {
    println "Usage: dslrunner.sh <configuration file> [<bim file>]"
}
