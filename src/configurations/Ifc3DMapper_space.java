package configurations;

import data.EMFIfcAccessor;
import mapping.PropertyMap;
import mapping.TargetCreationException;
import org.bimserver.models.ifc2x3.IfcSpace;
import visualization.VisFactory3D;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Ifc3DMapper_space extends MappedBimserverViewer<EMFIfcAccessor.EngineEObject> {

    protected void configMapping() {
        mapper.addMapping(new PropertyMap<EMFIfcAccessor.EngineEObject, VisFactory3D.Polyeder>() {
            @Override
            protected boolean condition() {
                return data.getObject() instanceof IfcSpace && ((IfcSpace) data.getObject()).getRepresentation() != null;
            }

            @Override
            protected void configure() {
                EMFIfcAccessor.Geometry geometry = data.getGeometry();
                assert geometry != null;
                if (((IfcSpace) data.getObject()).getDecomposes().get(0).getRelatingObject().getName().equals("9.OG"))
                    graphObject.setColor(1, 0, 0);
                graphObject.setVertizes(geometry.vertizes);
                graphObject.setNormals(geometry.normals);
            }
        });
    }

    @Override
    void loadFile() throws FileNotFoundException {
        File ifc = chooseFile(null);
        long size = ifc.length();
        EMFIfcAccessor data = new EMFIfcAccessor();
        data.setInput(new FileInputStream(ifc), size);
        this.data = data;
    }

    public static void main(String[] args) throws TargetCreationException, FileNotFoundException {
        Ifc3DMapper_space ifcViewer = new Ifc3DMapper_space();
        ifcViewer.run();
    }

    private File chooseFile(String directoryPath) {
        JFileChooser chooser = (directoryPath != null) ? new JFileChooser(directoryPath) : new JFileChooser();
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().endsWith("ifc");
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
