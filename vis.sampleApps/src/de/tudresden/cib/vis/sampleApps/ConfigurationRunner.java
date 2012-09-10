package de.tudresden.cib.vis.sampleApps;

import de.tudresden.cib.vis.configurations.*;
import de.tudresden.cib.vis.data.bimserver.EMFIfcAccessor;
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser;
import de.tudresden.cib.vis.data.bimserver.SimplePluginManager;
import de.tudresden.cib.vis.data.multimodel.LinkedObject;
import de.tudresden.cib.vis.data.multimodel.MultiModelAccessor;
import de.tudresden.cib.vis.mapping.TargetCreationException;
import de.tudresden.cib.vis.runtime.draw2d.Draw2DViewer;
import de.tudresden.cib.vis.runtime.java3d.viewers.SimpleViewer;
import de.tudresden.cib.vis.scene.SceneManager;
import org.bimserver.plugins.PluginException;
import org.eclipse.draw2d.Panel;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public enum ConfigurationRunner {
    IFC_3D {
        @Override
        void run() throws FileNotFoundException, PluginException {
            MappedJ3DLoader<EMFIfcParser.EngineEObject> loader = new MappedJ3DLoader<EMFIfcParser.EngineEObject>(new EMFIfcAccessor(new SimplePluginManager()));
            new Ifc3DConfiguration(loader.getMapper()).config();
            SimpleViewer viewer = new SimpleViewer(loader);
            viewer.run(new FileReader(viewer.chooseFile("D:\\Nutzer\\helga\\div\\ifc-modelle", "ifc")));
        }
    }, IFC_3DSPACE {
        @Override
        void run() throws FileNotFoundException, PluginException {
            MappedJ3DLoader<EMFIfcParser.EngineEObject> loader = new MappedJ3DLoader<EMFIfcParser.EngineEObject>(new EMFIfcAccessor(new SimplePluginManager()));
            new Ifc3DSpaceConfiguration(loader.getMapper()).config();
            SimpleViewer viewer = new SimpleViewer(loader);
            viewer.run(new FileReader(viewer.chooseFile("D:\\Nutzer\\helga\\div\\ifc-modelle","ifc")));
        }
    }, IFC_4D {
        @Override
        void run() throws IOException, PluginException {
            MappedJ3DLoader<LinkedObject<EMFIfcParser.EngineEObject>> loader = new MappedJ3DLoader<LinkedObject<EMFIfcParser.EngineEObject>>(new MultiModelAccessor<EMFIfcParser.EngineEObject>(new SimplePluginManager()));
            new Ifc4DConfiguration(loader.getMapper()).config();
            SimpleViewer viewer = new SimpleViewer(loader);
            viewer.run(viewer.chooseFile("D:\\Nutzer\\helga\\div\\mefisto-container", "zip").getCanonicalPath());  // or carport.zip
        }
    }, IFCGAEB_3D {
        @Override
        void run() throws IOException, PluginException {
            MappedJ3DLoader<LinkedObject<EMFIfcParser.EngineEObject>> loader = new MappedJ3DLoader<LinkedObject<EMFIfcParser.EngineEObject>>(new MultiModelAccessor<EMFIfcParser.EngineEObject>(new SimplePluginManager()));
            new IfcGaebColored3DConfiguration(loader.getMapper()).config();
            SimpleViewer viewer = new SimpleViewer(loader);
            viewer.run(viewer.chooseFile("D:\\Nutzer\\helga\\div\\mefisto-container", "zip").getCanonicalPath());
        }
    }, GAEB_BARCHART {
        @Override
        void run() throws IOException, PluginException, TargetCreationException {
            Draw2DViewer viewer = new Draw2DViewer();
            Font big = new Font(viewer.getDefaultFont().getDevice(), "Times New Roman", 50, 0);
            Font normal = new Font(viewer.getDefaultFont().getDevice(), "Times New Roman", 10, 0);
            File input = viewer.chooseFile("D:\\Nutzer\\helga\\div\\mefisto-container", "X81");
            GaebBarchartConfiguration gaebBarchartConfig = new GaebBarchartConfiguration(normal, new FileInputStream(input));
            gaebBarchartConfig.config();
            viewer.setSnapShotParams("D:/test.png", SWT.IMAGE_PNG);
            viewer.showContent(gaebBarchartConfig.execute().getScene());
            big.dispose();
        }
    }, IFC_2D {
        @Override
        void run() throws IOException, PluginException, TargetCreationException {
            Draw2DViewer viewer = new Draw2DViewer();
            File input = viewer.chooseFile("D:\\Nutzer\\helga\\div\\mefisto-container", "ifc");
            Ifc2DConfiguration ifc2DConfiguration = new Ifc2DConfiguration(viewer.getDefaultFont(), new FileInputStream(input));
            ifc2DConfiguration.config();
            viewer.showContent(ifc2DConfiguration.execute().getScene());
        }
    }, GANTT {
        @Override
        void run() throws IOException, PluginException, TargetCreationException {
            Draw2DViewer viewer = new Draw2DViewer();
            File input = viewer.chooseFile(getClass().getResource(".").getPath(), "xml");
            TimelineConfiguration config = new TimelineConfiguration(viewer.getDefaultFont(), new FileInputStream(input));
            config.config();
            SceneManager<EObject,Panel> result = config.execute();
            result.animate();
            viewer.showContent(result.getScene());
            result.dispose();
        }
    };

    public static void main(String[] args) throws TargetCreationException, IOException, PluginException {
        Set<String> names = new HashSet<String>();
        for(ConfigurationRunner conf: values()){
            names.add(conf.name());
        }
        System.out.println("available configurations:");
        for(String name: names){
            System.out.println(name);
        }
        if (args.length >= 1 && names.contains(args[0])) {
            System.out.println("\nrunning " + args[0]);
            valueOf(args[0]).run();
        }
    }

    abstract void run() throws IOException, PluginException, TargetCreationException;

}
