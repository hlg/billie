package de.tudresden.cib.vis.sampleApps;

import cib.mf.qto.model.AnsatzType;
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
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public enum ConfigurationRunner {
    IFC_3D {
        @Override
        void run(String[] args) throws FileNotFoundException, PluginException {
            MappedJ3DLoader<EMFIfcParser.EngineEObject> loader = new MappedJ3DLoader<EMFIfcParser.EngineEObject>(new EMFIfcAccessor(new SimplePluginManager()));
            new Ifc_3D(loader.getMapper()).config();
            SimpleViewer viewer = new SimpleViewer(loader);
            viewer.run(viewer.chooseFile("D:\\Nutzer\\helga\\div\\ifc-modelle", "ifc").getPath());
        }
    }, IFC_3DSPACE {
        @Override
        void run(String[] args) throws FileNotFoundException, PluginException {
            MappedJ3DLoader<EMFIfcParser.EngineEObject> loader = new MappedJ3DLoader<EMFIfcParser.EngineEObject>(new EMFIfcAccessor(new SimplePluginManager()));
            new Ifc_3D_Space(loader.getMapper()).config();
            SimpleViewer viewer = new SimpleViewer(loader);
            viewer.run(viewer.chooseFile("D:\\Nutzer\\helga\\div\\ifc-modelle","ifc").getPath());
        }
    }, IFC_4D {
        @Override
        void run(String[] args) throws IOException, PluginException {
            MappedJ3DLoader<LinkedObject<EMFIfcParser.EngineEObject>> loader = new MappedJ3DLoader<LinkedObject<EMFIfcParser.EngineEObject>>(new MultiModelAccessor<EMFIfcParser.EngineEObject>(new SimplePluginManager()));
            new IfcSched_Colored4D(loader.getMapper()).config();
            SimpleViewer viewer = new SimpleViewer(loader);
            viewer.setPickingEnabled(false);
            viewer.run(viewer.chooseFile("D:\\Nutzer\\helga\\div\\mefisto-container", "zip").getCanonicalPath());  // or carport.zip
        }
    }, IFCGAEB_3D {
        @Override
        void run(String[] args) throws IOException, PluginException {
            MappedJ3DLoader<LinkedObject<EMFIfcParser.EngineEObject>> loader = new MappedJ3DLoader<LinkedObject<EMFIfcParser.EngineEObject>>(new MultiModelAccessor<EMFIfcParser.EngineEObject>(new SimplePluginManager()));
            new IfcGaeb_Colored3D(loader.getMapper()).config();
            SimpleViewer viewer = new SimpleViewer(loader);
            viewer.run(viewer.chooseFile("D:\\Nutzer\\helga\\div\\mefisto-container", "zip").getCanonicalPath());
        }
    }, GAEB_BARCHART {
        @Override
        void run(String[] args) throws IOException, PluginException, TargetCreationException {
            Draw2DViewer viewer = new Draw2DViewer();
            Font big = new Font(viewer.getDefaultFont().getDevice(), "Times New Roman", 50, 0);
            Font normal = new Font(viewer.getDefaultFont().getDevice(), "Times New Roman", 10, 0);
            File input = viewer.chooseFile("D:\\Nutzer\\helga\\div\\mefisto-container", "X81");
            Gaeb_Barchart gaebBarchartConfig = new Gaeb_Barchart(normal, new FileInputStream(input));
            gaebBarchartConfig.config();
            viewer.setSnapShotParams("/home/dev/test.png", SWT.IMAGE_PNG);
            viewer.showContent(gaebBarchartConfig.execute().getScene());
            big.dispose();
        }
    }, IFC_2D {
        @Override
        void run(String[] args) throws IOException, PluginException, TargetCreationException {
            Draw2DViewer viewer = new Draw2DViewer();
            File input = viewer.chooseFile("D:\\Nutzer\\helga\\div\\mefisto-container", "ifc");
            Ifc_2D ifc2DConfiguration = new Ifc_2D(viewer.getDefaultFont(), new FileInputStream(input), input.length());
            ifc2DConfiguration.config();
            viewer.showContent(ifc2DConfiguration.execute().getScene());
        }
    }, GANTT {
        @Override
        void run(String[] args) throws IOException, PluginException, TargetCreationException {
            Draw2DViewer viewer = new Draw2DViewer();
            File input = viewer.chooseFile(getClass().getResource(".").getPath(), "xml");
            Sched_Gantt config = new Sched_Gantt(viewer.getDefaultFont(), new FileInputStream(input));
            config.config();
            SceneManager<EObject,Panel> result = config.execute();
            result.animate();
            viewer.showContent(result.getScene());
            result.dispose();
        }
    }, PROGRESS_TEXT {
        @Override
        void run(String[] args) throws IOException, PluginException, TargetCreationException {
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
            QtoSched_Text config = new QtoSched_Text(dataAcessor, lm_ids, "FM3");
            config.config();
            System.out.println(config.execute().getScene());
            System.out.println("--- finished ---");
        }
    }, PROGRESS_GANTT {
        @Override
        void run(String[] args) throws IOException, PluginException, TargetCreationException {
            MultiModelAccessor<Activity> dataAcessor = new MultiModelAccessor<Activity>(new SimplePluginManager());
            String basePath = args.length > 1 ? args[1] : "/home/dev/src/visMapping.git/combined_Angebot_LF/";
            dataAcessor.addAcessor("FM3", new EMFQtoAccessor(new FileInputStream(basePath + "QTO/1/1 LV VA.xml"), "QTO1"));
            String[] lm_ids = {"FM5", "FM6", "FM7", "FM8", "FM9"};
            for(int i = 4; i<=8; i++){
                String location = basePath + String.format("QTO/1/1 RE LE_0%d.xml", i);
                dataAcessor.addAcessor(lm_ids[i-4], new EMFQtoAccessor(new FileInputStream(location), "QTO2"));
            }
            dataAcessor.addAcessor("FM4", new EMFSchedule11Accessor(new FileInputStream(basePath + "Activity/1/Vorgangsmodell 1.xml"), "Activity1"));
            dataAcessor.groupBy("FM4", new File(basePath, "links/links.xml"));
            dataAcessor.sort(new Comparator<LinkedObject<Activity>>() {
                @Override
                public int compare(LinkedObject<Activity> link, LinkedObject<Activity> otherLink) {
                    String activityPath = new ActivityHelper(link.getKeyObject()).extractActivityDescription();
                    String otherActivityPath = new ActivityHelper(otherLink.getKeyObject()).extractActivityDescription();
                    return activityPath.compareTo(otherActivityPath);
                }
            });
            Draw2DViewer viewer = new Draw2DViewer();
            QtoSched_GanttAnim config = new QtoSched_GanttAnim(dataAcessor, lm_ids, "FM3",  viewer.getDefaultFont());
            config.config();
            SceneManager<LinkedObject<Activity>, Panel> scene =  config.execute();
            scene.animate();
            viewer.showContent(scene.getScene());
        }
    }, IFC_REPORTS_4D {
        @Override
        void run(String[] args) throws IOException, PluginException, TargetCreationException {
            MultiModelAccessor<EMFIfcParser.EngineEObject> dataAcessor = new MultiModelAccessor<EMFIfcParser.EngineEObject>(new SimplePluginManager());
            MappedJ3DLoader<LinkedObject<EMFIfcParser.EngineEObject>> loader = new MappedJ3DLoader<LinkedObject<EMFIfcParser.EngineEObject>>(dataAcessor);
            String[] lm_ids = {"FM5", "FM6", "FM7", "FM8", "FM9"};
            Configuration config = new IfcQtoSched_Colored4D(loader.getMapper(), lm_ids, "FM3");
            config.config();
            SimpleViewer viewer = new SimpleViewer(loader);
            viewer.run("/home/dev/src/visMapping.git/combined_Angebot_LF.zip");
        }
    }, LINKS_HEB {
        @Override
        void run(String[] args) throws IOException, PluginException, TargetCreationException {
            MultiModelAccessor<AnsatzType> dataAcessor = new MultiModelAccessor<AnsatzType>(new SimplePluginManager());
            File input = new File("/home/dev/src/visMapping.git/combined_Angebot_LF.zip");
            dataAcessor.read(new FileInputStream(input), input.length());
            Draw2DViewer viewer = new Draw2DViewer();
            Configuration<?,?,Panel> config = new IfcGaebQto_HEB(dataAcessor, viewer.getDefaultFont());
            config.config();
            SceneManager<?,Panel> scene = config.execute();
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
            valueOf(args[0]).run(args);
        }
    }

    abstract void run(String[] args) throws IOException, PluginException, TargetCreationException;

}
