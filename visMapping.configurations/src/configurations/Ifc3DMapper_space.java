package configurations;

import data.EMFIfcAccessor;
import data.EMFIfcParser;
import mapping.PropertyMap;
import mapping.TargetCreationException;
import org.bimserver.models.ifc2x3.IfcSpace;
import visualization.VisFactory3D;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.IOException;

public class Ifc3DMapper_space extends MappedBimserverViewer<EMFIfcParser.EngineEObject> {

    protected void configMapping() {
        mapper.addMapping(new PropertyMap<EMFIfcParser.EngineEObject, VisFactory3D.Polyeder>() {
            @Override
            protected boolean condition() {
                return data.getObject() instanceof IfcSpace
                        && ((IfcSpace) data.getObject()).getRepresentation() != null
                        && ((IfcSpace) data.getObject()).getDecomposes().get(0).getRelatingObject().getName().equals("20.OG")
                    ;
            }

            @Override
            protected void configure() {
                EMFIfcParser.Geometry geometry = data.getGeometry();
                assert geometry != null;
                graphObject.setVertizes(geometry.vertizes);
                graphObject.setNormals(geometry.normals);
            }
        });
    }

    @Override
    void loadFile() throws IOException {
        File ifc = chooseFile(".");
        EMFIfcAccessor data = new EMFIfcAccessor();
        data.setInput(ifc);
        this.data = data;
    }

    public static void main(String[] args) throws TargetCreationException, IOException {
        Ifc3DMapper_space ifcViewer = new Ifc3DMapper_space();
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
