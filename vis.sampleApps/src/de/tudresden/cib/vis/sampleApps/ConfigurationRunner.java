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
import org.bimserver.plugins.PluginException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
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
            InputStream input = viewer.getClass().getResourceAsStream("/resources/LV1.X81");
            GaebBarchartConfiguration gaebBarchartConfig = new GaebBarchartConfiguration(big, input);
            gaebBarchartConfig.config();
            viewer.setSnapShotParams("D:/test.png", SWT.IMAGE_PNG);
            viewer.showContent(gaebBarchartConfig.runMapper());
            big.dispose();
        }
    }, IFC_2D {
        @Override
        void run() throws IOException, PluginException, TargetCreationException {
            Draw2DViewer viewer = new Draw2DViewer();
            InputStream input = viewer.getClass().getResourceAsStream("/resources/carport2.ifc");
            Ifc2DConfiguration ifc2DConfiguration = new Ifc2DConfiguration(viewer.getDefaultFont(), input);
            ifc2DConfiguration.config();
            viewer.showContent(ifc2DConfiguration.runMapper());
        }
    };

    public static void main(String[] args) throws TargetCreationException, IOException, PluginException {
        Set<String> names = new HashSet<String>();
        for(ConfigurationRunner conf: values()){
            names.add(conf.name());
        }
        if (args.length >= 1 && names.contains(args[0])) {
            valueOf(args[0]).run();
        } else {
            System.out.println("available configurations:");
            for(String name: names){
                System.out.println(name);
            }
        }
    }

    abstract void run() throws IOException, PluginException, TargetCreationException;

}
