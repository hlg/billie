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
import de.tudresden.cib.vis.data.mmqlserver.MmqlServerAccessor;
import de.tudresden.cib.vis.data.multimodel.*;
import de.tudresden.cib.vis.filter.Condition;
import de.tudresden.cib.vis.mapping.Configuration;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.TargetCreationException;
import de.tudresden.cib.vis.runtime.draw2d.Draw2DViewer;
import de.tudresden.cib.vis.runtime.java3d.viewers.SimpleViewer;
import de.tudresden.cib.vis.scene.DefaultEvent;
import de.tudresden.cib.vis.scene.SceneManager;
import de.tudresden.cib.vis.scene.draw2d.Draw2dBuilder;
import de.tudresden.cib.vis.scene.java3d.Java3dBuilder;
import de.tudresden.cib.vis.scene.java3d.Java3dFactory;
import de.tudresden.cib.vis.scene.text.TextBuilder;
import net.fortuna.ical4j.model.component.VEvent;
import org.bimserver.emf.IdEObject;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Panel;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;

import javax.media.j3d.BranchGroup;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
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
    }, MMAA_4D {
        @Override
        void run(String[] args) throws IOException, DataAccessException, TargetCreationException {
            GenericMultiModelAccessor<EMFIfcParser.EngineEObject> mmAccessor = new GenericMultiModelAccessor<EMFIfcParser.EngineEObject>(createPluginManager());
            SimpleViewer viewer = new SimpleViewer();
            viewer.setPickingEnabled(false);
            String mmaa = args.length > 1 ? args[1] : viewer.chooseFile("D:\\Nutzer\\helga\\div\\eworkBau\\mm", "mmaa").getCanonicalPath();
            mmAccessor.read(new File(mmaa).toURI().toURL(), new GenericMultiModelAccessor.EMTypeCondition(EMTypes.IFC), new GenericMultiModelAccessor.EMTypeCondition(EMTypes.ACTIVITY11));
            IfcSched_Colored4D<BranchGroup> config = new IfcSched_Colored4D<BranchGroup>(Java3dBuilder.createMapper(mmAccessor));
            config.config();
            SceneManager<LinkedObject<EMFIfcParser.EngineEObject>, BranchGroup> scene = config.execute();
            scene.animate();
            viewer.run(scene.getScene());  // or carport.zip
        }
    }, IFCICAL_4D {
        @Override
        void run(String[] args) throws IOException, DataAccessException, TargetCreationException {
            GenericMultiModelAccessor<EMFIfcParser.EngineEObject> mmAccessor = new GenericMultiModelAccessor<EMFIfcParser.EngineEObject>(createPluginManager());
            SimpleViewer viewer = new SimpleViewer();
            viewer.setPickingEnabled(false);
            String mmaa = args.length > 1 ? args[1] : viewer.chooseFile("D:\\Nutzer\\helga\\div\\eworkBau\\mm", "mmaa").getCanonicalPath();
            mmAccessor.read(new File(mmaa).toURI().toURL(), new GenericMultiModelAccessor.EMTypeCondition(EMTypes.IFC), new GenericMultiModelAccessor.EMByName("Fein"));
            IfcIcal_Colored4D<BranchGroup> config = new IfcIcal_Colored4D<BranchGroup>(Java3dBuilder.createMapper(mmAccessor));
            config.config();
            SceneManager<LinkedObject<EMFIfcParser.EngineEObject>, BranchGroup> scene = config.execute();
            scene.animate();
            viewer.run(scene.getScene());  // or carport.zip
        }
    }, IFCGAEBQTO_3D {
        @Override
        void run(String[] args) throws IOException, DataAccessException, TargetCreationException {
            MultiModelAccessor<EMFIfcParser.EngineEObject> mmAccessor = new MultiModelAccessor<EMFIfcParser.EngineEObject>(createPluginManager());
            SimpleViewer viewer = new SimpleViewer();
            viewer.setPickingEnabled(false);
            String zip = (args.length > 1 && !args[1].equals("-")) ? args[1] : viewer.chooseFile("D:\\Nutzer\\helga\\div\\mefisto-container", "zip").getCanonicalPath();
            List<String> ids = mmAccessor.read(new File(zip),new EMTypeCondition(EMTypes.IFC), new EMTypeCondition(EMTypes.GAEB){
                @Override
                public boolean isValidFor(Content alternative) {
                    for (I option : alternative.getContentOptions().getI()){
                        if(option.getK().equals("extension") && option.getV().equals("DA84")) return true;
                    }
                    return false;
                }
            }, new EMTypeCondition(EMTypes.QTO));
            // mmAccessor.setLinkModelId("L2"); // TODO: conditions!
            Mapper<LinkedObject<EMFIfcParser.EngineEObject>,Condition<LinkedObject<EMFIfcParser.EngineEObject>>, Java3dFactory.Java3DGraphObject,BranchGroup> mapper = Java3dBuilder.createMapper(mmAccessor);
            IfcGaeb_Colored3D<BranchGroup> config = null;
            try {
                config = args.length>2
                        ? new IfcGaeb_Colored3D<BranchGroup>(mapper, new File(args[2]))
                        : new IfcGaeb_Colored3D<BranchGroup>(mapper);
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (InstantiationException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            // config.absolute=false;
            config.gaebX84Id = ids.get(1);
            config.gaebX83Id = ids.get(1);
            config.config();
            viewer.run(config.execute().getScene());
        }
    }, IFCGAEBSPLIT_3D {
        @Override
        void run(String[] args) throws IOException, TargetCreationException, DataAccessException {
            MultiModelAccessor<EMFIfcParser.EngineEObject> mmAccessor = new MultiModelAccessor<EMFIfcParser.EngineEObject>(createPluginManager());
            SimpleViewer viewer = new SimpleViewer();
            viewer.setPickingEnabled(false);
            String zip = (args.length > 1 && !args[1].equals("-")) ? args[1] : viewer.chooseFile("D:\\Nutzer\\helga\\div", "zip").getCanonicalPath();
            List<String> ids = mmAccessor.read(new File(zip), new EMTypeCondition(EMTypes.IFC), new EMTypeCondition(EMTypes.GAEBSPLIT));
            Mapper<LinkedObject<EMFIfcParser.EngineEObject>, Condition<LinkedObject<EMFIfcParser.EngineEObject>>, Java3dFactory.Java3DGraphObject, BranchGroup> mapper = Java3dBuilder.createMapper(mmAccessor);
            IfcGaebSplit_Colored3D<BranchGroup> config = new IfcGaebSplit_Colored3D<BranchGroup>(mapper);
            config.gaebID = ids.get(1);
            config.config();
            viewer.run(config.execute().getScene());
        }
    }, GAEB_BARCHART {
        @Override
        void run(String[] args) throws IOException, TargetCreationException {
            Draw2DViewer viewer = new Draw2DViewer();
            Font big = new Font(viewer.getDefaultFont().getDevice(), "Times New Roman", 50, 0);
            Font normal = new Font(viewer.getDefaultFont().getDevice(), "Times New Roman", 10, 0);
            File input = args.length>1 ? new File(args[1]) : viewer.chooseFile("D:\\Nutzer\\helga\\div\\mefisto-container", "X8*");
            Gaeb_Barchart<Panel> gaebBarchartConfig = new Gaeb_Barchart<Panel>(Draw2dBuilder.createMapper(new EMFGaebAccessor(input.toURI().toURL()), normal));
            gaebBarchartConfig.config();
            viewer.setSnapShotParams("D:\\Nutzer\\helga\\pub\\graphic\\yes.png", SWT.IMAGE_PNG);
            viewer.showContent(gaebBarchartConfig.execute().getScene());
            big.dispose();
        }
    }, GAEB_BARCHART_MMQL {
        @Override
        void run(String[] args) throws IOException, TargetCreationException, DataAccessException {
            Draw2DViewer viewer = new Draw2DViewer();
            // Font big = new Font(viewer.getDefaultFont().getDevice(), "Times New Roman", 50, 0);
            Font normal = new Font(viewer.getDefaultFont().getDevice(), "Times New Roman", 10, 0);
            File input = args.length>1 ? new File(args[1]) : viewer.chooseFile("D:\\Nutzer\\helga\\div\\mefisto-container", "mmaa");
            MmqlServerAccessor mmqlAccessor = new MmqlServerAccessor();
            mmqlAccessor.read("use editor \"carport.mmaa\"\n select" +
                    "\titem.id as ID,\n" +
                    "\titem ? (\"text\", \"outline\") as outline,\n" +
                    "\titem.\"uP\" as UP\n" +
                    "from\n" +
                    "\t\"LV_1.X81\".\"Item\" as item\n");
            Gaeb_Barchart_Mmql<Panel> gaebBarchartConfig = new Gaeb_Barchart_Mmql<Panel>(Draw2dBuilder.createMapper(mmqlAccessor, normal));
            gaebBarchartConfig.config();
            // viewer.setSnapShotParams("D:\\Nutzer\\helga\\pub\\graphic\\yes.png", SWT.IMAGE_PNG);
            viewer.showContent(gaebBarchartConfig.execute().getScene());
            normal.dispose();
        }
    }, IFC_2D {
        @Override
        void run(String[] args) throws IOException, TargetCreationException, DataAccessException {
            Draw2DViewer viewer = new Draw2DViewer();
            File input = viewer.chooseFile("D:\\Nutzer\\helga\\div\\mefisto-container", "ifc");
            DataAccessor<EMFIfcParser.EngineEObject, Condition<EMFIfcParser.EngineEObject>> data = new EMFIfcGeometricAccessor(createPluginManager(), input.toURI().toURL());
            Ifc_2D<Panel> ifc2DConfiguration = new Ifc_2D<Panel>(Draw2dBuilder.createMapper(data, viewer.getDefaultFont()));
            ifc2DConfiguration.config();
            viewer.showContent(ifc2DConfiguration.execute().getScene());
        }
    }, GANTT {
        @Override
        void run(String[] args) throws IOException, TargetCreationException {
            Draw2DViewer viewer = new Draw2DViewer();
            File input = viewer.chooseFile(System.getProperty("user.dir"), "xml");
            Sched_Gantt<Panel> config = new Sched_Gantt<Panel>(Draw2dBuilder.createMapper(new EMFSchedule11Accessor(input.toURI().toURL()), viewer.getDefaultFont()));
            config.config();
            SceneManager<EObject,Panel> result = config.execute();
            result.animate();
            viewer.showContent(result.getScene());
            result.dispose();
        }
    }, ICAL_GANTT {
        @Override
        void run(String[] args) throws IOException, TargetCreationException, DataAccessException {
            Draw2DViewer viewer = new Draw2DViewer();
            URL input = viewer.chooseFile(System.getProperty("user.dir"), "ics").toURI().toURL();
            IcalAccessor accessor = new IcalAccessor();
            accessor.read(input);
            Ical_Gantt<Panel> config = new Ical_Gantt<Panel>(Draw2dBuilder.createMapper(accessor, viewer.getDefaultFont()));
            config.config();
            SceneManager<VEvent,Panel> result = config.execute();
            result.animate();
            viewer.showContent(result.getScene());
            result.dispose();
        }
    }, PROGRESS_TEXT {
        @Override
        void run(String[] args) throws IOException, TargetCreationException, DataAccessException {
            MultiModelAccessor<Activity> dataAcessor = new MultiModelAccessor<Activity>(createPluginManager());
            String basePath = "D:/Nutzer/helga/div/mefisto-container/kongress_3/combined_Angebot_LF/";
            dataAcessor.addAcessor("FM3", new EMFQtoAccessor(new File(basePath + "QTO/1/1 LV VA.xml").toURI().toURL(), "QTO1"));
            String[] lm_ids = {"FM5", "FM6", "FM7", "FM8", "FM9"};
            for(int i = 4; i<=8; i++){
                String location = basePath + String.format("QTO/1/1 RE LE_0%d.xml", i);
                dataAcessor.addAcessor(lm_ids[i-4], new EMFQtoAccessor(new File(location).toURI().toURL(), "QTO2"));
            }
            dataAcessor.addAcessor("FM4", new EMFSchedule11Accessor(new File(basePath + "Activity/1/Vorgangsmodell 1.xml").toURI().toURL(), "Activity1"));
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
    }, MMAA_REPORTS_4D {
        @Override
        void run(String[] args) throws IOException, TargetCreationException, DataAccessException {
            GenericMultiModelAccessor<EMFIfcParser.EngineEObject> dataAccessor = new GenericMultiModelAccessor<EMFIfcParser.EngineEObject>(createPluginManager());
            SimpleViewer viewer = new SimpleViewer();
            viewer.setPickingEnabled(false);
            List<String> ids = dataAccessor.read(viewer.chooseFile(System.getProperty("user.dir"), "mmaa").toURI().toURL(), new GenericMultiModelAccessor.EMTypeCondition(EMTypes.IFC), new GenericMultiModelAccessor.EMByName("Fein"), new GenericMultiModelAccessor.EMByName("progress"));
            Configuration  config = new Mmaa_Progress_Colored4D(Java3dBuilder.createMapper(dataAccessor), ids);
        }
    }, LINKS_HEB {
        @Override
        void run(String[] args) throws IOException, TargetCreationException, DataAccessException {
            EMFIfcHierarchicAcessor.SKIP_LAST_LEVEL = false;
            SimpleMultiModelAccessor dataAcessor = new SimpleMultiModelAccessor(createPluginManager());
            Draw2DViewer viewer = new Draw2DViewer();
            File input = args.length > 1 ? new File(args[1]) : viewer.chooseFolder("/home/dev/src/visMapping.git/");
            EMTypeCondition angebotserstellung = new EMTypeCondition(EMTypes.QTO) {
                @Override
                public boolean isValidFor(ElementaryModel model) {
                    return super.isValidFor(model) && model.getMeta().getPhase().getPhaseDesc().equals("Angebotserstellung");
                }
            };
            final LinkedList<String> ids = dataAcessor.readFromFolder(input, "L1", new EMTypeCondition(EMTypes.IFCHIERARCHIC), new EMTypeCondition(EMTypes.GAEBHIERARCHIC), new EMTypeCondition(EMTypes.QTO));
            EMFIfcHierarchicAcessor hierarchicIfc = (EMFIfcHierarchicAcessor) dataAcessor.getAccessor(ids.get(0));
            hierarchicIfc.index();
            DataAccessor<Hierarchic<EObject>, Condition<Hierarchic<EObject>>> hierarchicGaeb = dataAcessor.getAccessor(ids.get(1));
            Panel container = new Panel();
            GridLayout manager = new GridLayout(1, true);
            container.setLayoutManager(manager);

            IfcGaebQto_HEB<Panel> hebConfig = new IfcGaebQto_HEB<Panel>(Draw2dBuilder.createMapper(dataAcessor, viewer.getDefaultFont()));
            hebConfig.config();
            SceneManager<LinkedObject.ResolvedLink, Panel> hebScene = hebConfig.execute();

            Ifc_Icycle<Panel> ifcIcycle = new Ifc_Icycle<Panel>(Draw2dBuilder.createMapper(hierarchicIfc, viewer.getDefaultFont()), hebConfig.getIfcScale());
            ifcIcycle.setSkipLastLevel(false);
            ifcIcycle.setWithLastLevelLabels(true);
            ifcIcycle.config();

            final Configuration<Hierarchic<EObject>, Condition<Hierarchic<EObject>>, Panel> gaebIcycle = new Gaeb_Icycle<Panel>(Draw2dBuilder.createMapper(hierarchicGaeb, viewer.getDefaultFont()), hebConfig.getGaebScale());
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
            EMFIfcHierarchicAcessor.SKIP_LAST_LEVEL = false;
            EMFIfcHierarchicAcessor data = new EMFIfcHierarchicAcessor(createPluginManager());
            Draw2DViewer viewer = new Draw2DViewer();
            data.read(viewer.chooseFile("/home/dev/src", "ifc").toURI().toURL());
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
            IndexedDataAccessor<Hierarchic<EObject>, Condition<Hierarchic<EObject>>> data =new HierarchicGaebAccessor();
            Draw2DViewer viewer = new Draw2DViewer();
            data.read(viewer.chooseFile("/home/dev/src", "*").toURI().toURL());
            data.index();
            Gaeb_Icycle<Panel> config = new Gaeb_Icycle<Panel>(Draw2dBuilder.createMapper(data, viewer.getDefaultFont()));
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
            Gaeb_Barchart<Panel> gaebConfig = new Gaeb_Barchart<Panel>(Draw2dBuilder.createMapper(gaeb, viewer.getDefaultFont()));
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
