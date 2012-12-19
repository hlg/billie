package de.tudresden.cib.vis.swingApp

import cib.mf.schedule.model.activity11.Activity
import com.sun.j3d.utils.universe.SimpleUniverse
import de.mefisto.model.container.ElementaryModel
import de.tudresden.cib.vis.configurations.*
import de.tudresden.cib.vis.data.DataAccessException
import de.tudresden.cib.vis.data.DataAccessor
import de.tudresden.cib.vis.data.Hierarchic
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser
import de.tudresden.cib.vis.data.bimserver.SimplePluginManager
import de.tudresden.cib.vis.data.multimodel.*
import de.tudresden.cib.vis.runtime.java3d.UniverseBuilder
import de.tudresden.cib.vis.runtime.java3d.views.OrbitalView
import de.tudresden.cib.vis.scene.SceneManager
import de.tudresden.cib.vis.scene.java3d.Java3dBuilder
import de.tudresden.cib.vis.scnen.java2d.Java2DBuilder
import groovy.swing.SwingBuilder
import org.bimserver.emf.IdEObject
import org.eclipse.emf.ecore.EObject

import javax.media.j3d.BranchGroup
import javax.media.j3d.Canvas3D
import javax.swing.*
import javax.swing.filechooser.FileFilter
import java.awt.*
import java.awt.BorderLayout as BL
import java.awt.FlowLayout as FL
import java.util.List

public class MefistoDemo {

    def fileName;
    File folder;
    JScrollPane p0;
    JScrollPane p1;
    JScrollPane p2;
    JScrollPane p3;
    SwingBuilder swingBuilder

    private static SimplePluginManager pm = new SimplePluginManager();
    static {
        pm.loadPluginsFromCurrentClassloader();
        pm.initAllLoadedPlugins();
    }

    public void loadGantt(JScrollPane panel) {
        try {
            MultiModelAccessor<Activity> dataAcessor = new MultiModelAccessor<Activity>(pm);
            LinkedList<String> modelIds = dataAcessor.read(folder, new EMTypeCondition(EMTypes.ACTIVITY11),
                    new EMTypeCondition(EMTypes.QTO) {
                        @Override
                        public boolean isValidFor(ElementaryModel model) {
                            return super.isValidFor(model) && model.meta.phase.phaseDesc.equals('Angebotserstellung');
                        }
                    },
                    new EMTypeCondition(EMTypes.QTO) {
                        @Override
                        public boolean isValidFor(ElementaryModel model) {
                            return super.isValidFor(model) && model.meta.phase.phaseDesc.equals('Leistungsermittlung');
                        }
                    });
            dataAcessor.sort(new Comparator<LinkedObject<Activity>>() {
                public int compare(LinkedObject<Activity> link, LinkedObject<Activity> otherLink) {
                    String activityPath = new ActivityHelper(link.getKeyObject()).extractActivityDescription();
                    String otherActivityPath = new ActivityHelper(otherLink.getKeyObject()).extractActivityDescription();
                    return activityPath.compareTo(otherActivityPath);
                }
            });
            List<String> lm = modelIds.subList(2, modelIds.size());
            QtoSched_GanttAnim<JPanel> config = new QtoSched_GanttAnim<JPanel>(Java2DBuilder.createMapper(dataAcessor), lm.toArray(new String[lm.size()]), modelIds.get(1));
            config.config()
            SceneManager<LinkedObject<Activity>, JPanel> scene = config.execute();
            scene.animate();
            swingBuilder.edt { panel.getViewport().add(scene.getScene()) }
        } catch (DataAccessException e) {
            showError(e, panel)
        }
    }

    private void loadHEB(JScrollPane panel) {
        try {
            SimpleMultiModelAccessor dataAcessor = new SimpleMultiModelAccessor(pm);
            LinkedList<String> ids = dataAcessor.read(folder, "L2", new EMTypeCondition(EMTypes.QTO) {
                @Override
                public boolean isValidFor(ElementaryModel model) {
                    return super.isValidFor(model) && model.getMeta().getPhase().getPhaseDesc().equals("Angebotserstellung");
                }
            }, new EMTypeCondition(EMTypes.IFCHIERARCHIC), new EMTypeCondition(EMTypes.GAEBHIERARCHIC));
            DataAccessor<Hierarchic<IdEObject>> hierarchicIfc = dataAcessor.getAccessor(ids.get(1));
            DataAccessor<Hierarchic<EObject>> hierarchicGaeb = dataAcessor.getAccessor(ids.get(2));
            JPanel container = swingBuilder.panel { boxLayout(axis: BoxLayout.Y_AXIS) };
            IfcGaebQto_HEB<JPanel> hebConfig = new IfcGaebQto_HEB<JPanel>(Java2DBuilder.createMapper(dataAcessor));
            hebConfig.config();
            JPanel hebScene = hebConfig.execute().getScene();
            Configuration<?, JPanel> ifcIcycle = new Ifc_Icycle<JPanel>(Java2DBuilder.createMapper(hierarchicIfc), hebConfig.getIfcScale());
            ifcIcycle.config();
            Configuration<?, JPanel> gaebIcycle = new Gaeb_Icycle<JPanel>(Java2DBuilder.createMapper(hierarchicGaeb), hebConfig.getGaebScale());
            gaebIcycle.config();
            container.add(ifcIcycle.execute().getScene());
            container.add(hebScene);
            container.add(gaebIcycle.execute().getScene());
            swingBuilder.edt { panel.getViewport().add(container); panel.revalidate(); panel.repaint(); }
        } catch (DataAccessException e) {
            showError(e, panel)
        }
    }

    private void loadBarchart(JScrollPane panel) {
        try {
            SimpleMultiModelAccessor data = new SimpleMultiModelAccessor(pm);
            if (!folder) new File(fileName.text).withInputStream { folder = data.unzip(it) }
            List<String> modelIds = data.read(folder, new EMTypeCondition(EMTypes.GAEB));
            DataAccessor<EObject> gaeb = data.getAccessor(modelIds.get(0));
            Gaeb_Barchart<JPanel> config = new Gaeb_Barchart<JPanel>(Java2DBuilder.createMapper(gaeb));
            config.config();
            SceneManager<EObject, JPanel> scene = config.execute();
            swingBuilder.edt { panel.getViewport().add(scene.getScene()) }
        } catch (DataAccessException e) {
            showError(e, panel)
        }
    }

    private void load3dModel(JScrollPane panel) {
        try {
            MultiModelAccessor<EMFIfcParser.EngineEObject> accessor = new MultiModelAccessor<EMFIfcParser.EngineEObject>(pm);
            accessor.read(folder)
            IfcSched_Colored4D<BranchGroup> config = new IfcSched_Colored4D<BranchGroup>(Java3dBuilder.createMapper(accessor));
            config.config()
            SceneManager<?, BranchGroup> scene = config.execute()
            Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
            UniverseBuilder universe = new UniverseBuilder();
            universe.addView(new OrbitalView(canvas));
            swingBuilder.edt {
                panel.getViewport().add(canvas)
            }
            canvas.setVisible(true);
            scene.animate()
            universe.addLights(scene.getScene())
            universe.showScene(scene.getScene())
        } catch (DataAccessException e) {
            showError(e, panel)
        }
    }

    private void showError(e, panel) {
        swingBuilder.edt {
            JPanel childPanel = panel.getViewport().getComponent(0) as JPanel
            childPanel.remove(0)
            childPanel.add(label(foreground: Color.red, text: e.getMessage()))
            panel.revalidate()
            panel.repaint()
        }
    }

    public static void main(String[] args) {
        // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        new MefistoDemo().show()
    }

    void show() {
        swingBuilder = new SwingBuilder()
        swingBuilder.edt {
            List<Image> icons = [16, 32, 48, 64, 128].collect { imgSize -> imageIcon(resource: "/resources/mefisto${imgSize}.png").image }
            frame(title: 'Mefisto Vis Demo', show: true, defaultCloseOperation: JFrame.EXIT_ON_CLOSE, extendedState: JFrame.MAXIMIZED_BOTH, iconImages: icons) {
                panel(id: 'mainPanel', constraints: BL.NORTH) {
                    flowLayout(alignment: FL.LEFT)
                    label(text: "Container:")
                    fileName = textField(text: '.', columns: 60)
                    button(text: "Select", action {
                        def chooser = fileChooser(
                                dialogTitle: "Choose container, zipped or unzipped!",
                                fileSelectionMode: JFileChooser.FILES_AND_DIRECTORIES,
                                fileFilter: [getDescription: {-> "*.zip, *.mmc, dir" },
                                        accept: { File file -> file.isDirectory() || file.name ==~ /.*?\.mmc/ || file.name ==~ /.*?\.zip/ }] as FileFilter,
                                currentDirectory: new File(fileName.text).parentFile
                        )
                        if (chooser.showOpenDialog(fileName) != JFileChooser.APPROVE_OPTION) return
                        fileName.text = chooser.selectedFile.canonicalPath
                    })
                    button(text: 'Load', action {
                        def ckFolder = new File(fileName.text)
                        println ckFolder
                        if (ckFolder.isDirectory()) folder = ckFolder
                        println "Loading $fileName.text"
                        [p0, p1, p2, p3].each { p ->
                            p.getViewport().add(panel(layout: flowLayout(alignment: FL.LEFT)) {
                                progressBar(background: Color.WHITE, indeterminate: true, progressString: 'loading', stringPainted: true)
                            }) // label(text: 'loading ...')
                        }
                        doOutside {
                            loadBarchart(p0)
                            loadGantt(p1)
                            loadHEB(p2)
                            load3dModel(p3)
                        }
                    })
                }
                tabbedPane(stateChanged: { println "switched to panel $it.source.selectedIndex" }) {
                    p0 = scrollPane(title: 'Barchart') {}
                    p1 = scrollPane(title: 'Animated progress') {}
                    p2 = scrollPane(title: 'HEB Linked GAEB and Object') {}
                    p3 = scrollPane(title: 'Colored 3D Model') {}
                }
            }
        }
    }
}
