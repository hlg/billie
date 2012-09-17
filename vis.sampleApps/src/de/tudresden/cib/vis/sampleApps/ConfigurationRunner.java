package de.tudresden.cib.vis.sampleApps;

import cib.mf.schedule.model.activity11.Activity;
import de.tudresden.cib.vis.configurations.*;
import de.tudresden.cib.vis.data.bimserver.EMFIfcAccessor;
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser;
import de.tudresden.cib.vis.data.bimserver.SimplePluginManager;
import de.tudresden.cib.vis.data.multimodel.EMFQtoAccessor;
import de.tudresden.cib.vis.data.multimodel.EMFSchedule11Accessor;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public enum ConfigurationRunner {
    IFC_3D {
        @Override
        void run() throws FileNotFoundException, PluginException {
            MappedJ3DLoader<EMFIfcParser.EngineEObject> loader = new MappedJ3DLoader<EMFIfcParser.EngineEObject>(new EMFIfcAccessor(new SimplePluginManager()));
            new Ifc3DConfiguration(loader.getMapper()).config();
            SimpleViewer viewer = new SimpleViewer(loader);
            viewer.run(viewer.chooseFile("D:\\Nutzer\\helga\\div\\ifc-modelle", "ifc").getPath());
        }
    }, IFC_3DSPACE {
        @Override
        void run() throws FileNotFoundException, PluginException {
            MappedJ3DLoader<EMFIfcParser.EngineEObject> loader = new MappedJ3DLoader<EMFIfcParser.EngineEObject>(new EMFIfcAccessor(new SimplePluginManager()));
            new Ifc3DSpaceConfiguration(loader.getMapper()).config();
            SimpleViewer viewer = new SimpleViewer(loader);
            viewer.run(viewer.chooseFile("D:\\Nutzer\\helga\\div\\ifc-modelle","ifc").getPath());
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
            viewer.setSnapShotParams("/home/helga/test.png", SWT.IMAGE_PNG);
            viewer.showContent(gaebBarchartConfig.execute().getScene());
            big.dispose();
        }
    }, IFC_2D {
        @Override
        void run() throws IOException, PluginException, TargetCreationException {
            Draw2DViewer viewer = new Draw2DViewer();
            File input = viewer.chooseFile("D:\\Nutzer\\helga\\div\\mefisto-container", "ifc");
            Ifc2DConfiguration ifc2DConfiguration = new Ifc2DConfiguration(viewer.getDefaultFont(), new FileInputStream(input), input.length());
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
    }, PROGRESS_TEXT {
        @Override
        void run() throws IOException, PluginException, TargetCreationException {
            MultiModelAccessor<Activity> dataAcessor = new MultiModelAccessor<Activity>(new SimplePluginManager());
            String basePath = "D:/Nutzer/helga/div/mefisto-container/kongress_3/combined_Angebot_LF/";
            dataAcessor.addAcessor("FM3", new EMFQtoAccessor(new FileInputStream(basePath + "QTO/1/1 LV VA.xml"), "QTO1"));
            String[] lm_ids = {"FM5", "FM6", "FM7", "FM8", "FM9"};
            for(int i = 4; i<=8; i++){
                String location = basePath + String.format("QTO/1/1 RE LE_0%d.xml", i);
                dataAcessor.addAcessor(lm_ids[i-4], new EMFQtoAccessor(new FileInputStream(location), "QTO2"));
            }
            dataAcessor.addAcessor("FM4", new EMFSchedule11Accessor(new FileInputStream(basePath + "Activity/1/Vorgangsmodell 1.xml"), "Activity1"));
            dataAcessor.groupBy("FM4", new File(basePath, "links/links.xml"));
            ProgressreportTextConfig config = new ProgressreportTextConfig(dataAcessor, lm_ids, "FM3");
            config.config();
            System.out.println(config.execute().getScene());
            System.out.println("--- finished ---");
        }
    }, PROGRESS_GANTT {
        @Override
        void run() throws IOException, PluginException, TargetCreationException {
            MultiModelAccessor<Activity> dataAcessor = new MultiModelAccessor<Activity>(new SimplePluginManager());
            String basePath = "D:/Nutzer/helga/div/mefisto-container/kongress_3/combined_Angebot_LF/";
            dataAcessor.addAcessor("FM3", new EMFQtoAccessor(new FileInputStream(basePath + "QTO/1/1 LV VA.xml"), "QTO1"));
            String[] lm_ids = {"FM5", "FM6", "FM7", "FM8", "FM9"};
            for(int i = 4; i<=8; i++){
                String location = basePath + String.format("QTO/1/1 RE LE_0%d.xml", i);
                dataAcessor.addAcessor(lm_ids[i-4], new EMFQtoAccessor(new FileInputStream(location), "QTO2"));
            }
            dataAcessor.addAcessor("FM4", new EMFSchedule11Accessor(new FileInputStream(basePath + "Activity/1/Vorgangsmodell 1.xml"), "Activity1"));
            dataAcessor.groupBy("FM4", new File(basePath, "links/links.xml"));
            Draw2DViewer viewer = new Draw2DViewer();
            ProgressreportGanttConfig config = new ProgressreportGanttConfig(dataAcessor, lm_ids, "FM3",  viewer.getDefaultFont());
            config.config();
            SceneManager<LinkedObject<Activity>, Panel> scene =  config.execute();
            scene.animate();
            viewer.showContent(scene.getScene());
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
