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
import de.tudresden.cib.vis.runtime.java3d.viewers.AxonometricViewer;
import de.tudresden.cib.vis.runtime.java3d.viewers.SimpleViewer;
import de.tudresden.cib.vis.scene.DefaultEvent;
import de.tudresden.cib.vis.scene.SceneManager;
import de.tudresden.cib.vis.scene.draw2d.Draw2dBuilder;
import de.tudresden.cib.vis.scene.draw2d.Draw2dFactory;
import de.tudresden.cib.vis.scene.java3d.Java3dBuilder;
import de.tudresden.cib.vis.scene.java3d.Java3dFactory;
import de.tudresden.cib.vis.scene.text.TextBuilder;
import de.tudresden.cib.vis.scene.text.TextFactory;
import groovy.lang.GroovyShell;
import net.fortuna.ical4j.model.component.VEvent;
import org.bimserver.emf.IdEObject;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Panel;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;

import javax.media.j3d.BranchGroup;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public enum ConfigurationRunner {
    IFC_3D {
        @Override
        void run(String[] args) throws FileNotFoundException, DataAccessException {
            Ifc_3D config = new Ifc_3D(true);
            config.config();
            MappedJ3DLoader<EMFIfcParser.EngineEObject> loader = new MappedJ3DLoader<EMFIfcParser.EngineEObject>(new EMFIfcGeometricAccessor(new SimplePluginManager(), true), config);
            SimpleViewer viewer = new SimpleViewer(loader);
            viewer.chooseAndRun(args.length > 1 ? args[1] : System.getProperty("user.dir"), "ifc", false); //D:\Nutzer\helga\div\ifc-modelle
        }
    }, IFC_3D_AXONOMETRIC {
        @Override
        void run(String[] args) throws FileNotFoundException, DataAccessException {
            Ifc_3D config = new Ifc_3D(true);
            config.config();
            MappedJ3DLoader<EMFIfcParser.EngineEObject> loader = new MappedJ3DLoader<EMFIfcParser.EngineEObject>(new EMFIfcGeometricAccessor(new SimplePluginManager(), true), config);
            SimpleViewer viewer = new SimpleViewer(loader);
            viewer.setAxonometric(true);   // is also rotating by default, TODO: make rotation optional
            viewer.setPickingEnabled(false); // true by default
            viewer.chooseAndRun(args.length > 1 ? args[1] : System.getProperty("user.dir"), "ifc", false); //D:\Nutzer\helga\div\ifc-modelle
        }
    }, IFC_3D_INTERACTIVE {
        @Override
        void run(String[] args) throws IOException, TargetCreationException, DataAccessException {
            SimpleViewer viewer = new SimpleViewer();
            File ifc = viewer.chooseFile(args.length > 1 ? args[1] : System.getProperty("user.dir"), "ifc", false);
            final EMFIfcGeometricAccessor emf = new EMFIfcGeometricAccessor(new SimplePluginManager(), true);
            emf.read(ifc.toURI().toURL());
            Mapper<EMFIfcParser.EngineEObject, Condition<EMFIfcParser.EngineEObject>, Java3dFactory.Java3DGraphObject, BranchGroup> mapper = Java3dBuilder.createMapper(emf);
            Ifc_3D config = new Ifc_3D(false);
            config.config();
            final SceneManager<EMFIfcParser.EngineEObject, BranchGroup> scene = mapper.map(config);
            final JCheckBox checkBox = new JCheckBox("(un)highlight");
            final JTextField textField = new JTextField("object.object instanceof org.bimserver.models.ifc2x3tc1.IfcSlab");
            textField.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println(textField.getText());
                    final GroovyShell shell = new GroovyShell();
                    for(EMFIfcParser.EngineEObject object : emf.filter(new Condition<EMFIfcParser.EngineEObject>() {
                        @Override
                        public boolean matches(EMFIfcParser.EngineEObject data) {
                            shell.setVariable("object", data);
                            return shell.evaluate(textField.getText()).equals(true);
                        }
                    })){
                        scene.fire(checkBox.isSelected() ? Ifc_3D.EventX.HIGHLIGHT : Ifc_3D.EventX.UNHIGHLIGHT, object);
                    }  // TODO: or pass iterable directly to scene#fire (change signatur Collection -> Iterable)
                }
            });
            JPanel topRow = new JPanel(new BorderLayout());
            topRow.add(textField, BorderLayout.CENTER);
            topRow.add(checkBox, BorderLayout.EAST);
            viewer.add(topRow, BorderLayout.NORTH); // TODO: try ConsoleTextEditor from groovy.ui
            viewer.run(scene.getScene());
        }
    }, IFCSPACE_3D {
        @Override
        void run(String[] args) throws FileNotFoundException, DataAccessException {
            Ifc_3D_Space config = new Ifc_3D_Space();
            config.config();
            MappedJ3DLoader<EMFIfcParser.EngineEObject> loader = new MappedJ3DLoader<EMFIfcParser.EngineEObject>(new EMFIfcGeometricAccessor(new SimplePluginManager(), true), config);
            SimpleViewer viewer = new SimpleViewer(loader);
            viewer.setPickingEnabled(false);
            viewer.chooseAndRun(args.length > 1 ? args[1] : System.getProperty("user.dir"), "ifc", false);
        }
    }, IFC_4D {
        @Override
        void run(String[] args) throws IOException {
            IfcSched_Colored4D config = new IfcSched_Colored4D();
            config.config();
            MappedJ3DLoader<LinkedObject<EMFIfcParser.EngineEObject>> loader = new MappedJ3DLoader<LinkedObject<EMFIfcParser.EngineEObject>>(new MultiModelAccessor<EMFIfcParser.EngineEObject>(new SimplePluginManager()), config);
            SimpleViewer viewer = new SimpleViewer(loader);
            viewer.setPickingEnabled(false);
            viewer.chooseAndRun(args.length > 1 ? args[1] : System.getProperty("user.dir"), "zip", true);  // "D:\\Nutzer\\helga\\div\\mefisto-container" or carport.zip
        }
    }, MMAA_4D {
        @Override
        void run(String[] args) throws IOException, DataAccessException, TargetCreationException {
            GenericMultiModelAccessor<EMFIfcParser.EngineEObject> mmAccessor = new GenericMultiModelAccessor<EMFIfcParser.EngineEObject>(new SimplePluginManager());
            SimpleViewer viewer = new SimpleViewer();
            viewer.setPickingEnabled(false);
            String mmaa = viewer.chooseFile(args.length > 1 ? args[1] : System.getProperty("user.dir"), "mmaa", true).getCanonicalPath(); // "D:\\Nutzer\\helga\\div\\eworkBau\\mm"
            mmAccessor.read(new File(mmaa).toURI().toURL(), new GenericMultiModelAccessor.EMTypeCondition(EMTypes.IFC), new GenericMultiModelAccessor.EMTypeCondition(EMTypes.ACTIVITY11));
            IfcSched_Colored4D config = new IfcSched_Colored4D();
            config.config();
            SceneManager<LinkedObject<EMFIfcParser.EngineEObject>, BranchGroup> scene = Java3dBuilder.createMapper(mmAccessor).map(config);
            scene.animate();
            viewer.run(scene.getScene());  // or carport.zip
        }
    }, IFCICAL_4D {
        @Override
        void run(String[] args) throws IOException, DataAccessException, TargetCreationException {
            GenericMultiModelAccessor<EMFIfcParser.EngineEObject> mmAccessor = new GenericMultiModelAccessor<EMFIfcParser.EngineEObject>(new SimplePluginManager());
            SimpleViewer viewer = new SimpleViewer();
            viewer.setPickingEnabled(true);
            String mmaa = viewer.chooseFile(args.length > 1 ? args[1] : System.getProperty("user.dir"), "mmaa", true).getCanonicalPath(); //"D:\\Nutzer\\helga\\div\\eworkBau\\mm"
            mmAccessor.read(new File(mmaa).toURI().toURL(), new GenericMultiModelAccessor.EMTypeCondition(EMTypes.IFC), new GenericMultiModelAccessor.EMByName("Fein"));
            IfcIcal_Colored4D config = new IfcIcal_Colored4D();
            config.config();
            SceneManager<LinkedObject<EMFIfcParser.EngineEObject>, BranchGroup> scene = Java3dBuilder.createMapper(mmAccessor).map(config);
            scene.animate();
            viewer.run(scene.getScene());  // or carport.zip
        }
    }, IFCGAEBQTO_3D {
        @Override
        void run(String[] args) throws IOException, DataAccessException, TargetCreationException {
            MultiModelAccessor<EMFIfcParser.EngineEObject> mmAccessor = new MultiModelAccessor<EMFIfcParser.EngineEObject>(new SimplePluginManager());
            SimpleViewer viewer = new SimpleViewer();
            viewer.setPickingEnabled(false);
            String zip = viewer.chooseFile((args.length > 1 && !args[1].equals("-")) ? args[1] : System.getProperty("user.dir"), "zip", true).getCanonicalPath(); //"D:\\Nutzer\\helga\\div\\mefisto-container"
            List<String> ids = mmAccessor.read(new File(zip),new EMTypeCondition(EMTypes.IFC), new EMTypeCondition(EMTypes.GAEB){
                @Override
                public boolean isValidFor(Content alternative) {
                    for (I option : alternative.getContentOptions().getI()){
                        if(option.getK().equals("extension") && (option.getV().equals("DA84")) || option.getV().equals("DA81")) return true;
                    }
                    return false;
                }
            }, new EMTypeCondition(EMTypes.QTO));
            // mmAccessor.setLinkModelId("L2"); // TODO: conditions!
            Mapper<LinkedObject<EMFIfcParser.EngineEObject>,Condition<LinkedObject<EMFIfcParser.EngineEObject>>, Java3dFactory.Java3DGraphObject,BranchGroup> mapper = Java3dBuilder.createMapper(mmAccessor);
            IfcGaeb_Colored3D config = null;
            try {
                config = args.length>2
                        ? new IfcGaeb_Colored3D(new File(args[2]))
                        : new IfcGaeb_Colored3D();
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (InstantiationException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            // config.absolute=false;
            config.gaebX84Id = ids.get(1);
            config.gaebX83Id = ids.get(1);
            config.config();
            viewer.run(mapper.map(config).getScene());
        }
    }, IFCGAEBSPLIT_3D {
        @Override
        void run(String[] args) throws IOException, TargetCreationException, DataAccessException {
            MultiModelAccessor<EMFIfcParser.EngineEObject> mmAccessor = new MultiModelAccessor<EMFIfcParser.EngineEObject>(new SimplePluginManager());
            SimpleViewer viewer = new SimpleViewer();
            viewer.setPickingEnabled(false);
            String zip = viewer.chooseFile((args.length > 1 && !args[1].equals("-")) ? args[1] : System.getProperty("user.dir"), "zip", true).getCanonicalPath(); // "D:\\Nutzer\\helga\\div"
            List<String> ids = mmAccessor.read(new File(zip), new EMTypeCondition(EMTypes.IFC), new EMTypeCondition(EMTypes.GAEBSPLIT));
            Mapper<LinkedObject<EMFIfcParser.EngineEObject>, Condition<LinkedObject<EMFIfcParser.EngineEObject>>, Java3dFactory.Java3DGraphObject, BranchGroup> mapper = Java3dBuilder.createMapper(mmAccessor);
            IfcGaebSplit_Colored3D config = new IfcGaebSplit_Colored3D();
            config.gaebID = ids.get(1);
            config.config();
            viewer.run(mapper.map(config).getScene());
        }
    }, GAEB_BARCHART {
        @Override
        void run(String[] args) throws IOException, TargetCreationException {
            Draw2DViewer viewer = new Draw2DViewer();
            Font big = new Font(viewer.getDefaultFont().getDevice(), "Times New Roman", 50, 0);
            Font normal = new Font(viewer.getDefaultFont().getDevice(), "Times New Roman", 10, 0);
            File input = args.length>1 ? new File(args[1]) : viewer.chooseFile(System.getProperty("user.dir"), "X8*"); //"D:\\Nutzer\\helga\\div\\mefisto-container"
            Gaeb_Barchart gaebBarchartConfig = new Gaeb_Barchart();
            gaebBarchartConfig.config();
            //  viewer.setSnapShotParams(new File(System.getProperty("user.dir"), "yes.png").getCanonicalPath(), SWT.IMAGE_PNG);
            viewer.showContent(Draw2dBuilder.createMapper(new EMFGaebAccessor(input.toURI().toURL()), normal).map(gaebBarchartConfig).getScene());
            big.dispose();
        }
    }, GAEB_BARCHART_MMQL {
        @Override
        void run(String[] args) throws IOException, TargetCreationException, DataAccessException {
            Draw2DViewer viewer = new Draw2DViewer();
            Font normal = new Font(viewer.getDefaultFont().getDevice(), "Times New Roman", 10, 0);
            File input = args.length>1 ? new File(args[1]) : viewer.chooseFile(System.getProperty("user.dir"), "mmaa"); //"D:\\Nutzer\\helga\\div\\mefisto-container"
            MmqlServerAccessor mmqlAccessor = new MmqlServerAccessor();
            mmqlAccessor.read("use editor \"carport.mmaa\"\n select" +
                    "\titem.id as ID,\n" +
                    "\titem ? (\"text\", \"outline\") as outline,\n" +
                    "\titem.\"uP\" as UP\n" +
                    "from\n" +
                    "\t\"LV_1.X81\".\"Item\" as item\n");
            Gaeb_Barchart_Mmql gaebBarchartConfig = new Gaeb_Barchart_Mmql();
            gaebBarchartConfig.config();
            // viewer.setSnapShotParams("D:\\Nutzer\\helga\\pub\\graphic\\yes.png", SWT.IMAGE_PNG);
            viewer.showContent(Draw2dBuilder.createMapper(mmqlAccessor, normal).map(gaebBarchartConfig).getScene());
            normal.dispose();
        }
    }, IFC_2D {
        @Override
        void run(String[] args) throws IOException, TargetCreationException, DataAccessException {
            Draw2DViewer viewer = new Draw2DViewer();
            File input = viewer.chooseFile(System.getProperty("user.dir"), "ifc"); // "D:\\Nutzer\\helga\\div\\mefisto-container"
            DataAccessor<EMFIfcParser.EngineEObject, Condition<EMFIfcParser.EngineEObject>> data = new EMFIfcGeometricAccessor(new SimplePluginManager(), input.toURI().toURL());
            Ifc_2D ifc2DConfiguration = new Ifc_2D();
            ifc2DConfiguration.config();
            viewer.showContent(Draw2dBuilder.createMapper(data, viewer.getDefaultFont()).map(ifc2DConfiguration).getScene());
        }
    }, GANTT {
        @Override
        void run(String[] args) throws IOException, TargetCreationException {
            Draw2DViewer viewer = new Draw2DViewer();
            File input = viewer.chooseFile(System.getProperty("user.dir"), "xml");
            Sched_Gantt config = new Sched_Gantt();
            config.config();
            SceneManager<EObject,Panel> result = Draw2dBuilder.createMapper(new EMFSchedule11Accessor(input.toURI().toURL()), viewer.getDefaultFont()).map(config);
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
            Ical_Gantt config = new Ical_Gantt();
            config.config();
            SceneManager<VEvent,Panel> result = Draw2dBuilder.createMapper(accessor, viewer.getDefaultFont()).map(config);
            result.animate();
            viewer.showContent(result.getScene());
            result.dispose();
        }
    }, PROGRESS_TEXT {
        @Override
        void run(String[] args) throws IOException, TargetCreationException, DataAccessException {
            MultiModelAccessor<Activity> dataAcessor = new MultiModelAccessor<Activity>(new SimplePluginManager());
            String basePath = args.length>1 ? args[1] : "D:/Nutzer/helga/div/mefisto-container/kongress_3/combined_Angebot_LF/";
            dataAcessor.addAcessor("FM3", new EMFQtoAccessor(new File(basePath + "QTO/1/1 LV VA.xml").toURI().toURL(), "QTO1"));
            String[] lm_ids = {"FM5", "FM6", "FM7", "FM8", "FM9"};
            for(int i = 4; i<=8; i++){
                String location = basePath + String.format("QTO/1/1 RE LE_0%d.xml", i);
                dataAcessor.addAcessor(lm_ids[i-4], new EMFQtoAccessor(new File(location).toURI().toURL(), "QTO2"));
            }
            dataAcessor.addAcessor("FM4", new EMFSchedule11Accessor(new File(basePath + "Activity/1/Vorgangsmodell 1.xml").toURI().toURL(), "Activity1"));
            dataAcessor.groupBy("FM4", new File(basePath, "links/links.xml"));
            Mapper<LinkedObject<Activity>, Condition<LinkedObject<Activity>>, TextFactory.TextLabel, String> mapper = TextBuilder.createMapper(dataAcessor);
            QtoSched_Text config = new QtoSched_Text(lm_ids, "FM3");
            config.config();
            System.out.println(mapper.map(config).getScene());
            System.out.println("--- finished ---");
        }
    }, PROGRESS_GANTT {
        @Override
        void run(String[] args) throws IOException, TargetCreationException, DataAccessException {
            MultiModelAccessor<Activity> dataAcessor = new MultiModelAccessor<Activity>(new SimplePluginManager());
            Draw2DViewer viewer = new Draw2DViewer();
            String basePath = args.length > 1 ? args[1] : viewer.chooseFolder(System.getProperty("user.dir")).getAbsolutePath(); //"/home/dev/src/visMapping.git/combined_Angebot_LF/"
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
            List<String> lm = modelIds.subList(2,modelIds.size()-1);
            Mapper<LinkedObject<Activity>, Condition<LinkedObject<Activity>>, Draw2dFactory.Draw2dObject, Panel> mapper = Draw2dBuilder.createMapper(dataAcessor, viewer.getDefaultFont());
            QtoSched_GanttAnim config = new QtoSched_GanttAnim(lm.toArray(new String[lm.size()]), modelIds.get(1));
            config.config();
            SceneManager<LinkedObject<Activity>, Panel> scene =  mapper.map(config);
            scene.animate();
            viewer.showContent(scene.getScene());
        }
    }, IFC_REPORTS_4D {
        @Override
        void run(String[] args) throws IOException, TargetCreationException {
            MultiModelAccessor<EMFIfcParser.EngineEObject> dataAcessor = new MultiModelAccessor<EMFIfcParser.EngineEObject>(new SimplePluginManager());
            String[] lm_ids = {"FM5", "FM6", "FM7", "FM8", "FM9"};
            Configuration config = new IfcQtoSched_Colored4D(lm_ids, "FM3");
            config.config();
            MappedJ3DLoader<LinkedObject<EMFIfcParser.EngineEObject>> loader = new MappedJ3DLoader<LinkedObject<EMFIfcParser.EngineEObject>>(dataAcessor, config);
            SimpleViewer viewer = new SimpleViewer(loader);
            viewer.setPickingEnabled(false);
            viewer.chooseAndRun(args.length > 1 ? args[1] : System.getProperty("user.dir"), "zip", true); // "D:\\Nutzer\\helga\\div\\mefisto-container"
        }
    }, MMAA_REPORTS_4D {
        @Override
        void run(String[] args) throws IOException, TargetCreationException, DataAccessException {
            GenericMultiModelAccessor<EMFIfcParser.EngineEObject> dataAccessor = new GenericMultiModelAccessor<EMFIfcParser.EngineEObject>(new SimplePluginManager());
            SimpleViewer viewer = new AxonometricViewer();
            viewer.setPickingEnabled(false);
            List<String> ids = dataAccessor.read(viewer.chooseFile(System.getProperty("user.dir"), "mmaa", true).toURI().toURL(),
                    new GenericMultiModelAccessor.EMTypeCondition(EMTypes.IFC),
                    new GenericMultiModelAccessor.EMByName("Fein"),
                    new GenericMultiModelAccessor.EMByName("progress"),
                    new GenericMultiModelAccessor.EMTypeCondition(EMTypes.GAEBSPLIT)
            );
            Mmaa_Progress_Colored4D config = new Mmaa_Progress_Colored4D(ids);
            config.scale = (args.length > 1) ? Integer.valueOf(args[1]) : 3600000;
            config.config();
            SceneManager<?, BranchGroup> scene  = Java3dBuilder.createMapper(dataAccessor).map(config);
            scene.animate();
            viewer.run(scene.getScene());
        }
    }, MMAA_REPORT_SNAPSHOT {
        @Override
        void run(String[] args) throws IOException, TargetCreationException, DataAccessException {
            GenericMultiModelAccessor<EMFIfcParser.EngineEObject> dataAccessor = new GenericMultiModelAccessor<EMFIfcParser.EngineEObject>(new SimplePluginManager());
            SimpleViewer viewer = new AxonometricViewer(false);
            viewer.setPickingEnabled(false);
            List<String> ids = dataAccessor.read(viewer.chooseFile(System.getProperty("user.dir"), "mmaa", true).toURI().toURL(),
                    new GenericMultiModelAccessor.EMTypeCondition(EMTypes.IFC),
                    new GenericMultiModelAccessor.EMByName("Fein"),
                    new GenericMultiModelAccessor.EMByName("progressCalendar")
            );
            Mmaa_Progress_Colored config = new Mmaa_Progress_Colored(ids, "2M6f_UD1nEkvEACW0qZrgl");
            config.config();
            SceneManager<?, BranchGroup> scene = Java3dBuilder.createMapper(dataAccessor).map(config);
            scene.animate();
            viewer.run(scene.getScene());

        }
    },  LINKS_HEB {
        @Override
        void run(String[] args) throws IOException, TargetCreationException, DataAccessException {
            EMFIfcHierarchicAcessor.SKIP_LAST_LEVEL = false;
            SimpleMultiModelAccessor dataAcessor = new SimpleMultiModelAccessor(new SimplePluginManager());
            Draw2DViewer viewer = new Draw2DViewer();
            File input = args.length > 1 ? new File(args[1]) : viewer.chooseFolder(System.getProperty("user.dir")); // "/home/dev/src/visMapping.git/"
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

            IfcGaebQto_HEB hebConfig = new IfcGaebQto_HEB();
            hebConfig.config();
            SceneManager<LinkedObject.ResolvedLink, Panel> hebScene = Draw2dBuilder.createMapper(dataAcessor, viewer.getDefaultFont()).map(hebConfig);

            Ifc_Icycle ifcIcycle = new Ifc_Icycle(hebConfig.getIfcScale());
            ifcIcycle.setSkipLastLevel(false);
            ifcIcycle.setWithLastLevelLabels(true);
            ifcIcycle.config();

            final Configuration<Hierarchic<EObject>, Condition<Hierarchic<EObject>>> gaebIcycle = new Gaeb_Icycle(hebConfig.getGaebScale());
            gaebIcycle.config();

            final SceneManager<Hierarchic<IdEObject>, Panel> ifcIcycleScene = Draw2dBuilder.createMapper(hierarchicIfc, viewer.getDefaultFont()).map(ifcIcycle);
            final SceneManager<Hierarchic<EObject>, Panel> gaebIcycleScene = Draw2dBuilder.createMapper(hierarchicGaeb, viewer.getDefaultFont()).map(gaebIcycle);

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
            EMFIfcHierarchicAcessor data = new EMFIfcHierarchicAcessor(new SimplePluginManager());
            Draw2DViewer viewer = new Draw2DViewer();
            data.read(viewer.chooseFile(System.getProperty("user.dir"), "ifc").toURI().toURL()); // "/home/dev/src"
            data.index();
            Ifc_Icycle config = new Ifc_Icycle();
            config.setSkipLastLevel(false);
            config.setWithLastLevelLabels(true);
            config.config();
            viewer.showContent(Draw2dBuilder.createMapper(data, viewer.getDefaultFont()).map(config).getScene());
        }
    }, GAEB_ICYCLE {
        @Override
        void run(String[] args) throws IOException, TargetCreationException, DataAccessException {
            IndexedDataAccessor<Hierarchic<EObject>, Condition<Hierarchic<EObject>>> data =new HierarchicGaebAccessor();
            Draw2DViewer viewer = new Draw2DViewer();
            data.read(viewer.chooseFile(System.getProperty("user.dir"), "*").toURI().toURL()); // "/home/dev/src"
            data.index();
            Gaeb_Icycle config = new Gaeb_Icycle();
            config.config();
            viewer.showContent(Draw2dBuilder.createMapper(data, viewer.getDefaultFont()).map(config).getScene());
        }
    }, IFC_GAEB_INTERACTION {
        @Override
        void run(String[] args) throws IOException, TargetCreationException, DataAccessException {
            SimplePluginManager pluginManager = new SimplePluginManager();
            MultiModelAccessor byIfc = new MultiModelAccessor<EObject>(pluginManager);
            Draw2DViewer viewer = new Draw2DViewer();
            File input = args.length > 1 ? new File(args[1]) : viewer.chooseFolder(System.getProperty("user.dir")); // "/home/dev/src/visMapping.git/"
            List<String> modelIds = byIfc.read(input, new EMTypeCondition(EMTypes.IFCHIERARCHIC), new EMTypeCondition(EMTypes.GAEB)); // check its the right GAEB
            IndexedDataAccessor ifc =  byIfc.getAccessor(modelIds.get(0));
            IndexedDataAccessor gaeb = byIfc.getAccessor(modelIds.get(1));
            Gaeb_Barchart gaebConfig = new Gaeb_Barchart();
            gaebConfig.config();
            SceneManager<?,Panel> gaebScene = Draw2dBuilder.createMapper(gaeb, viewer.getDefaultFont()).map(gaebConfig);
            Panel container = new Panel();
            GridLayout manager = new GridLayout(2, true);
            container.setLayoutManager(manager);
            container.add(gaebScene.getScene());
        }
    };

    public static void main(String[] args) throws TargetCreationException, DataAccessException, IOException {
        String[] names = new String[values().length];
        for(ConfigurationRunner conf: values()){
            names[conf.ordinal()] = conf.name();
        }
        System.out.println("Usage: configrunner <configuration> [<bim file>]\navailable configurations:");
        for(String name: names){
            System.out.println(name);
        }
        if (args.length >= 1 && Arrays.asList(names).contains(args[0])) {
            System.out.println("\nrunning " + args[0]);
            valueOf(args[0]).run(args);
        }
    }

    abstract void run(String[] args) throws IOException, TargetCreationException, DataAccessException;

}
