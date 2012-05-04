package configurations;

import data.EMFIfcAccessor;
import data.EMFIfcParser;
import mapping.PropertyMap;
import mapping.TargetCreationException;
import org.bimserver.models.ifc2x3.IfcBuildingElement;
import visualization.VisFactory3D;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.IOException;

public class Ifc3DMapper extends MappedBimserverViewer<EMFIfcParser.EngineEObject> {

    protected void configMapping() {
        mapper.addMapping(new PropertyMap<EMFIfcParser.EngineEObject, VisFactory3D.Polyeder>() {
            @Override
            protected boolean condition() {
                return data.getObject() instanceof IfcBuildingElement
                       /* && ((IfcBuildingElement) data.getObject()).getRepresentation() != null
                        && !((IfcBuildingElement) data.getObject()).getContainedInStructure().isEmpty()
                        && ((IfcBuildingElement) data.getObject()).getContainedInStructure().get(0).getRelatingStructure().getName().equals("E14")*/
                        ;
            }

            @Override
            protected void configure() {
                EMFIfcParser.Geometry geometry = data.getGeometry();
                assert geometry != null;
                /* EList<IfcRelContainedInSpatialStructure> containedInStructure = ((IfcBuildingElement) data.getObject()).getContainedInStructure();
                if (!containedInStructure.isEmpty() && containedInStructure.get(0).getRelatingStructure().getName().equals("E14"))
                    graphObject.setColor(1, 0, 0); */
                graphObject.setVertizes(geometry.vertizes);
                graphObject.setNormals(geometry.normals);
            }
        });
    }

    @Override
    void loadFile() throws IOException {
        File ifc = chooseFile("D:\\Nutzer\\helga\\div\\ifc-modelle");
        EMFIfcAccessor data = new EMFIfcAccessor();
        data.setInput(ifc);
        this.data = data;
    }

    public static void main(String[] args) throws TargetCreationException, IOException {
        Ifc3DMapper ifcViewer = new Ifc3DMapper();
        ifcViewer.run();
    }
    private File chooseFile(String directoryPath) {
        JFileChooser chooser = (directoryPath != null) ? new JFileChooser(directoryPath) : new JFileChooser();
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith("ifc");
            }

            @Override
            public String getDescription() {
                return "IFC files";
            }
        };
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(this);
        return (returnVal == JFileChooser.APPROVE_OPTION) ? chooser.getSelectedFile() : null;
    }
}
