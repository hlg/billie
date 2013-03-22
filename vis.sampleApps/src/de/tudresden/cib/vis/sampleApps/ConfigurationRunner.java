package de.tudresden.cib.vis.sampleApps;

import cib.mf.schedule.model.activity11.Activity;
import de.mefisto.model.container.Content;
import de.mefisto.model.container.ElementaryModel;
import de.mefisto.model.container.I;
import de.tudresden.cib.vis.TriggerListener;
import de.tudresden.cib.vis.configurations.*;
import de.tudresden.cib.vis.data.DataAccessException;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.Hierarchic;
import de.tudresden.cib.vis.data.IndexedDataAccessor;
import de.tudresden.cib.vis.data.bimserver.EMFIfcGeometricAccessor;
import de.tudresden.cib.vis.data.bimserver.EMFIfcHierarchicAcessor;
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser;
import de.tudresden.cib.vis.data.bimserver.SimplePluginManager;
import de.tudresden.cib.vis.data.multimodel.*;
import de.tudresden.cib.vis.mapping.Configuration;
import de.tudresden.cib.vis.mapping.TargetCreationException;
import de.tudresden.cib.vis.runtime.draw2d.Draw2DViewer;
import de.tudresden.cib.vis.runtime.java3d.viewers.SimpleViewer;
import de.tudresden.cib.vis.scene.DefaultEvent;
import de.tudresden.cib.vis.scene.SceneManager;
import de.tudresden.cib.vis.scene.draw2d.Draw2dBuilder;
import de.tudresden.cib.vis.scene.text.TextBuilder;
import org.bimserver.emf.IdEObject;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Panel;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;

import javax.media.j3d.BranchGroup;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public enum ConfigurationRunner {
    IFC_3D {
        @Override
        void run(String[] args) throws FileNotFoundException, DataAccessException {
            MappedJ3DLoader<EMFIfcParser.EngineEObject> loader = new MappedJ3DLoader<EMFIfcParser.EngineEObject>(new EMFIfcGeometricAccessor(createPluginManager(), true));
            new Ifc_3D<BranchGroup>(loader.getMapper()).config();
            SimpleViewer viewer = new SimpleViewer(loader);
            viewer.setPickingEnabled(false);
            viewer.run(args.length > 1 ? args[1] : viewer.chooseFile("D:\\Nutzer\\helga\\div\\ifc-modelle", "ifc").getPath());
        }
    }, IFC_3DSPACE {
        @Override
        void run(String[] args) throws FileNotFoundException, DataAccessException {
            MappedJ3DLoader<EMFIfcParser.EngineEObject> loader = new MappedJ3DLoader<EMFIfcParser.EngineEObject>(new EMFIfcGeometricAccessor(createPluginManager(), true));
            new Ifc_3D_Space(loader.getMapper()).config();
            SimpleViewer viewer = new SimpleViewer(loader);
            viewer.setPickingEnabled(false);
            viewer.run(viewer.chooseFile("D:\\Nutzer\\helga\\div\\ifc-modelle","ifc").getPath());
        }
    }, IFC_4D {
        @Override
        void run(String[] args) throws IOException {
            MappedJ3DLoader<LinkedObject<EMFIfcParser.EngineEObject>> loader = new MappedJ3DLoader<LinkedObject<EMFIfcParser.EngineEObject>>(new MultiModelAccessor<EMFIfcParser.EngineEObject>(createPluginManager()));
            new IfcSched_Colored4D<BranchGroup>(loader.getMapper()).config();
            SimpleViewer viewer = new SimpleViewer(loader);
            viewer.setPickingEnabled(false);
            viewer.run(args.length > 1 ? args[1] : viewer.chooseFile("D:\\Nutzer\\helga\\div\\mefisto-container", "zip").getCanonicalPath());  // or carport.zip
        }
    }, IFCGAEB_3D {
        @Override
        void run(String[] args) throws IOException {
            MultiModelAccessor<EMFIfcParser.EngineEObject> mmAccessor = new MultiModelAccessor<EMFIfcParser.EngineEObject>(createPluginManager());
            mmAccessor.setModels(new EMTypeCondition(EMTypes.IFC), new EMTypeCondition(EMTypes.GAEB){
                @Override
                public boolean isValidFor(Content alternative) {
                    for (I option : alternative.getContentOptions().getI()){
                        if(option.getK().equals("extension") && option.getV().equals("DA84")) return true;
                    }
                    return false;
                }
            });
            MappedJ3DLoader<LinkedObject<EMFIfcParser.EngineEObject>> loader = new MappedJ3DLoader<LinkedObject<EMFIfcParser.EngineEObject>>(mmAccessor);
            new IfcGaeb_Colored3D<BranchGroup>(loader.getMapper()).config();
            SimpleViewer viewer = new SimpleViewer(loader);
            viewer.setPickingEnabled(false);
            viewer.run(args.length > 1 ? args[1] : viewer.chooseFile("D:\\Nutzer\\helga\\div\\mefisto-container", "zip").getCanonicalPath());
        }
    }, GAEB_BARCHART {
        @Override
        void run(String[] args) throws IOException, TargetCreationException {
            Draw2DViewer viewer = new Draw2DViewer();
            Font big = new Font(viewer.getDefaultFont().getDevice(), "Times New Roman", 50, 0);
            Font normal = new Font(viewer.getDefaultFont().getDevice(), "Times New Roman", 10, 0);
            File input = args.length>1 ? new File(args[1]) : viewer.chooseFile("D:\\Nutzer\\helga\\div\\mefisto-container", "X81");
            Gaeb_Barchart<Panel> gaebBarchartConfig = new Gaeb_Barchart<Panel>(Draw2dBuilder.createMapper(new EMFGaebAccessor(new FileInputStream(input)), normal));
            gaebBarchartConfig.config();
            viewer.setSnapShotParams("D:\\Nutzer\\helga\\pub\\graphic\\yes.png", SWT.IMAGE_PNG);
            viewer.showContent(gaebBarchartConfig.execute().getScene());
            big.dispose();
        }
    }, IFC_2D {
        @Override
        void run(String[] args) throws IOException, TargetCreationException, DataAccessException {
            Draw2DViewer viewer = new Draw2DViewer();
            File input = viewer.chooseFile("D:\\Nutzer\\helga\\div\\mefisto-container", "ifc");
            DataAccessor<EMFIfcParser.EngineEObject> data = new EMFIfcGeometricAccessor(createPluginManager(), new FileInputStream(input), input.length());
            Ifc_2D<Panel> ifc2DConfiguration = new Ifc_2D<Panel>(Draw2dBuilder.createMapper(data, viewer.getDefaultFont()));
            ifc2DConfiguration.config();
            viewer.showContent(ifc2DConfiguration.execute().getScene());
        }
    }, GANTT {
        @Override
        void run(String[] args) throws IOException, TargetCreationException {
            Draw2DViewer viewer = new Draw2DViewer();
            File input = viewer.chooseFile(getClass().getResource(".").getPath(), "xml");
            Sched_Gantt<Panel> config = new Sched_Gantt<Panel>(Draw2dBuilder.createMapper(new EMFSchedule11Accessor(new FileInputStream(input)), viewer.getDefaultFont()));
            config.config();
            SceneManager<EObject,Panel> result = config.execute();
            result.animate();
            viewer.showContent(result.getScene());
            result.dispose();
        }
    }, PROGRESS_TEXT {
        @Override
        void run(String[] args) throws IOException, TargetCreationException {
            MultiModelAccessor<Activity> dataAcessor = new MultiModelAccessor<Activity>(createPluginManager());
            String basePath = "D:/Nutzer/helga/div/mefisto-container/kongress_3/combined_Angebot_LF/";
            dataAcessor.addAcessor("FM3", new EMFQtoAccessor(new FileInputStream(basePath + "QTO/1/1 LV VA.xml"), "QTO1"));
            String[] lm_ids = {"FM5", "FM6", "FM7", "FM8", "FM9"};
            for(int i = 4; i<=8; i++){
                String location = basePath + String.format("QTO/1/1 RE LE_0%d.xml", i);
                dataAcessor.addAcessor(lm_ids[i-4], new EMFQtoAccessor(new FileInputStream(location), "QTO2"));
            }
            dataAcessor.addAcessor("FM4", new EMFSchedule11Accessor(new FileInputStream(basePath + "Activity/1/Vorgangsmodell 1.xml"), "Activity1"));
            dataAcessor.groupBy("FM4", new File(basePath, "links/links.xml"));
            QtoSched_Text config = new QtoSched_Text(TextBuilder.createMapper(dataAcessor), lm_ids, "FM3");
            config.config();
            System.out.println(config.execute().getScene());
            System.out.println("--- finished ---");
        }
    }, PROGRESS_GANTT {
        @Override
        void run(String[] args) throws IOException, TargetCreationException, DataAccessException {
            MultiModelAccessor<Activity> dataAcessor = new MultiModelAccessor<Activity>(createPluginManager());
            String basePath = args.length > 1 ? args[1] : "/home/dev/src/visMapping.git/combined_Angebot_LF/";
            LinkedList<String> modelIds = dataAcessor.readFromFolder(new File(basePath), new EMTypeCondition(EMTypes.ACTIVITY11),
                    new EMTypeCondition(EMTypes.QTO) {
                        @Override
                        public boolean isValidFor(ElementaryModel model) {
                            return super.isValidFor(model) && model.getMeta().getPhase().getPhaseDesc().equals("Angebotserstellung");
                        }
                    },
                    new EMTypeCondition(EMTypes.QTO) {
                        @Override
                        public boolean isValidFor(ElementaryModel model) {
                            return super.isValidFor(model) && model.getMeta().getPhase().getPhaseDesc().equals("Leistungsermittlung");
                        }
            });
            dataAcessor.sort(new Comparator<LinkedObject<Activity>>() {
                public int compare(LinkedObject<Activity> link, LinkedObject<Activity> otherLink) {
                    String activityPath = new ActivityHelper(link.getKeyObject()).extractActivityDescription();
                    String otherActivityPath = new ActivityHelper(otherLink.getKeyObject()).extractActivityDescription();
                    return activityPath.compareTo(otherActivityPath);
                }
            });
            Draw2DViewer viewer = new Draw2DViewer();
            List<String> lm = modelIds.subList(2,modelIds.size()-1);
            QtoSched_GanttAnim<Panel> config = new QtoSched_GanttAnim<Panel>(Draw2dBuilder.createMapper(dataAcessor,viewer.getDefaultFont()), lm.toArray(new String[lm.size()]), modelIds.get(1));
            config.config();
            SceneManager<LinkedObject<Activity>, Panel> scene =  config.execute();
            scene.animate();
            viewer.showContent(scene.getScene());
        }
    }, IFC_REPORTS_4D {
        @Override
        void run(String[] args) throws IOException, TargetCreationException {
            MultiModelAccessor<EMFIfcParser.EngineEObject> dataAcessor = new MultiModelAccessor<EMFIfcParser.EngineEObject>(createPluginManager());
            MappedJ3DLoader<LinkedObject<EMFIfcParser.EngineEObject>> loader = new MappedJ3DLoader<LinkedObject<EMFIfcParser.EngineEObject>>(dataAcessor);
            String[] lm_ids = {"FM5", "FM6", "FM7", "FM8", "FM9"};
            Configuration config = new IfcQtoSched_Colored4D<BranchGroup>(loader.getMapper(), lm_ids, "FM3");
            config.config();
            SimpleViewer viewer = new SimpleViewer(loader);
            viewer.setPickingEnabled(false);
            viewer.run(args.length > 1 ? args[1] : viewer.chooseFile("D:\\Nutzer\\helga\\div\\mefisto-container", "zip").getCanonicalPath());
        }
    }, LINKS_HEB {
        @Override
        void run(String[] args) throws IOException, TargetCreationException, DataAccessException {
            SimpleMultiModelAccessor dataAcessor = new SimpleMultiModelAccessor(createPluginManager());
            Draw2DViewer viewer = new Draw2DViewer();
            File input = args.length > 1 ? new File(args[1]) : viewer.chooseFolder("/home/dev/src/visMapping.git/");
            EMTypeCondition angebotserstellung = new EMTypeCondition(EMTypes.QTO) {
                @Override
                public boolean isValidFor(ElementaryModel model) {
                    return super.isValidFor(model) && model.getMeta().getPhase().getPhaseDesc().equals("Angebotserstellung");
                }
            };
            final LinkedList<String> ids = dataAcessor.readFromFolder(input, "L2", new EMTypeCondition(EMTypes.IFCHIERARCHIC), new EMTypeCondition(EMTypes.GAEBHIERARCHIC), new EMTypeCondition(EMTypes.QTO));
            DataAccessor<Hierarchic<IdEObject>> hierarchicIfc = dataAcessor.getAccessor(ids.get(0));
            DataAccessor<Hierarchic<EObject>> hierarchicGaeb = dataAcessor.getAccessor(ids.get(1));
            Panel container = new Panel();
            GridLayout manager = new GridLayout(1, true);
            container.setLayoutManager(manager);

            IfcGaebQto_HEB<Panel> hebConfig = new IfcGaebQto_HEB<Panel>(Draw2dBuilder.createMapper(dataAcessor, viewer.getDefaultFont()));
            hebConfig.config();
            SceneManager<LinkedObject.ResolvedLink, Panel> hebScene = hebConfig.execute();

            Configuration<Hierarchic<IdEObject>,Panel> ifcIcycle = new Ifc_Icycle<Panel>(Draw2dBuilder.createMapper(hierarchicIfc, viewer.getDefaultFont()), hebConfig.getIfcScale());
            ifcIcycle.config();

            final Configuration<Hierarchic<EObject>, Panel> gaebIcycle = new Gaeb_Icycle<Panel>(Draw2dBuilder.createMapper(hierarchicGaeb, viewer.getDefaultFont()), hebConfig.getGaebScale());
            gaebIcycle.config();

            final SceneManager<Hierarchic<IdEObject>, Panel> ifcIcycleScene = ifcIcycle.execute();
            final SceneManager<Hierarchic<EObject>, Panel> gaebIcycleScene = gaebIcycle.execute();

            hebConfig.listeners.add(new TriggerListener<LinkedObject.ResolvedLink>() {
                @Override
                public void notify(LinkedObject.ResolvedLink data) {
                    gaebIcycleScene.fire(DefaultEvent.CLICK, data.getLinkedHierarchicGaeb().get(ids.get(1)));
                    ifcIcycleScene.fire(DefaultEvent.CLICK, data.getLinkedHierarchicIfc().get(ids.get(0)));
                }
            });

            container.add(ifcIcycleScene.getScene());
            container.add(hebScene.getScene());
            container.add(gaebIcycleScene.getScene());
            viewer.showContent(container);
        }
    }, IFC_ICYCLE {
        @Override
        void run(String[] args) throws IOException, TargetCreationException, DataAccessException {
            EMFIfcHierarchicAcessor data = new EMFIfcHierarchicAcessor(createPluginManager());
            data.setSkipLastLevel(false);
            Draw2DViewer viewer = new Draw2DViewer();
            data.read(viewer.chooseFile("/home/dev/src", "ifc"));
            data.index();
            Ifc_Icycle<Panel> config = new Ifc_Icycle<Panel>(Draw2dBuilder.createMapper(data, viewer.getDefaultFont()));
            config.setSkipLastLevel(false);
            config.setWithLastLevelLabels(true);
            config.config();
            viewer.showContent(config.execute().getScene());
        }
    }, GAEB_ICYCLE {
        @Override
        void run(String[] args) throws IOException, TargetCreationException, DataAccessException {
            IndexedDataAccessor<Hierarchic<EObject>> data =new HierarchicGaebAccessor();
            Draw2DViewer viewer = new Draw2DViewer();
            data.read(viewer.chooseFile("/home/dev/src", "*"));
            data.index();
            Configuration<?,Panel> config = new Gaeb_Icycle(Draw2dBuilder.createMapper(data, viewer.getDefaultFont()));
            config.config();
            viewer.showContent(config.execute().getScene());
        }
    }, IFC_GAEB_INTERACTION {
        @Override
        void run(String[] args) throws IOException, TargetCreationException, DataAccessException {
            SimplePluginManager pluginManager = createPluginManager();
            MultiModelAccessor byIfc = new MultiModelAccessor<EObject>(pluginManager);
            Draw2DViewer viewer = new Draw2DViewer();
            File input = args.length > 1 ? new File(args[1]) : viewer.chooseFolder("/home/dev/src/visMapping.git/");
            List<String> modelIds = byIfc.read(input, new EMTypeCondition(EMTypes.IFCHIERARCHIC), new EMTypeCondition(EMTypes.GAEB)); // check its the right GAEB
            IndexedDataAccessor ifc =  byIfc.getAccessor(modelIds.get(0));
            IndexedDataAccessor gaeb = byIfc.getAccessor(modelIds.get(1));
            Configuration<?,Panel> gaebConfig = new Gaeb_Barchart<Panel>(Draw2dBuilder.createMapper(gaeb, viewer.getDefaultFont()));
            gaebConfig.config();
            SceneManager<?,Panel> gaebScene = gaebConfig.execute();
            Panel container = new Panel();
            GridLayout manager = new GridLayout(2, true);
            container.setLayoutManager(manager);
            container.add(gaebScene.getScene());
        }
    };

    public static void main(String[] args) throws TargetCreationException, DataAccessException, IOException {
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

    SimplePluginManager createPluginManager(){
        SimplePluginManager pluginManager = new SimplePluginManager();
        pluginManager.loadPluginsFromCurrentClassloader();
        pluginManager.initAllLoadedPlugins();
        return pluginManager;
    }

    abstract void run(String[] args) throws IOException, TargetCreationException, DataAccessException;

}
