package runtime.java3d.loaders;

import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.Loader;
import org.apache.commons.io.input.ReaderInputStream;
import org.bimserver.emf.IdEObject;
import org.bimserver.models.ifc2x3tc1.IfcBuildingElement;
import org.bimserver.models.ifc2x3tc1.IfcProduct;
import org.bimserver.plugins.PluginException;
import org.bimserver.plugins.PluginManager;
import org.bimserver.plugins.deserializers.DeserializeException;
import org.bimserver.plugins.ifcengine.*;
import org.bimserver.plugins.serializers.IfcModelInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import runtime.java3d.colorTime.TypeAppearance;

import javax.media.j3d.*;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.io.*;
import java.net.URL;
import java.util.Date;

public class BimserverJava3dLoader implements Loader {
    private static final Logger LOGGER = LoggerFactory.getLogger(BimserverJava3dLoader.class);
    protected IfcModelInterface model;

    private boolean defaultPickability = true;
    private Appearance defaultAppearance = TypeAppearance.INACTIVE.createAppearance();

    protected IfcScene scene;
    private TNOIFCParser parser;
    private IfcEngineGeometry geometry;
    private IfcEngineModel ifcEngineModel;

    public BimserverJava3dLoader(PluginManager pm) {
        try {
            parser = new TNOIFCParser(pm);
        } catch (PluginException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public IfcScene load(File file) throws FileNotFoundException {
        return load(new FileInputStream(file));
    }

    private IfcScene load(InputStream ifcData) throws FileNotFoundException {
        try {
            TNOIFCParser.TNOIfcModel model = parser.loadData(ifcData);
            geometry = model.geometry;
            ifcEngineModel = model.geomModel;
            this.model = model.model;
        } catch (DeserializeException e) {
            throw new ParsingErrorException("failed deserializing ifc data", e);
        } catch (IfcEngineException e) {
            throw new ParsingErrorException("failed extracting geometry from ifc data", e);
        } catch (IOException e) {
            throw new FileNotFoundException(e.getMessage());
        }
        return createScene();
    }

    private IfcScene createScene() {

        scene = new IfcScene();
        BranchGroup mainScene = createSceneGraph();
        scene.setSceneGroup(mainScene);

        LOGGER.info(new Date(System.currentTimeMillis()) + " finished building scene graph");
        return scene;
    }

    protected BranchGroup createSceneGraph() {
        TransformGroup buildingTransformGroup = new TransformGroup();
        for (IdEObject idEObject : model.getValues()) {
            if (idEObject instanceof IfcBuildingElement) {
                createAndAddShapes((IfcBuildingElement) idEObject, buildingTransformGroup);
            }
        }
        BranchGroup buildingBranchGroup = new BranchGroup();
        buildingBranchGroup.addChild(buildingTransformGroup);
        return buildingBranchGroup;
    }

    protected void createAndAddShapes(IfcProduct ifcBuildingElement, Group sceneGraphNode) {
        try {
            if (geometry != null) {
                IfcEngineInstance instance = ifcEngineModel.getInstanceFromExpressId((int) ifcBuildingElement.getOid());
                IfcEngineInstanceVisualisationProperties instanceInModelling = instance.getVisualisationProperties();
                if (instanceInModelling.getPrimitiveCount() != 0) {
                    // Appearance defaultAppearance = TypeAppearance.valueOf(ifcBuildingElement.getClass().getSimpleName()).createAppearance();
                    if (defaultAppearance != null) {
                        TriangleArray triangleArray = new TriangleArray(instanceInModelling.getPrimitiveCount() * 3, GeometryArray.COORDINATES | GeometryArray.NORMALS);
                        for (int i = instanceInModelling.getStartIndex(); i < instanceInModelling.getPrimitiveCount() * 3 + instanceInModelling.getStartIndex(); i += 3) {
                            int offsetIndex = i - instanceInModelling.getStartIndex();
                            int i1 = geometry.getIndex(i) * 3;
                            int i2 = geometry.getIndex(i + 1) * 3;
                            int i3 = geometry.getIndex(i + 2) * 3;
                            triangleArray.setCoordinate(offsetIndex, new Point3f(geometry.getVertex(i1), geometry.getVertex(i1 + 1), geometry.getVertex(i1 + 2)));
                            triangleArray.setNormal(offsetIndex, new Vector3f(geometry.getNormal(i1), geometry.getNormal(i1 + 1), geometry.getNormal(i1 + 2)));
                            triangleArray.setCoordinate(offsetIndex + 1, new Point3f(geometry.getVertex(i2), geometry.getVertex(i2 + 1), geometry.getVertex(i2 + 2)));
                            triangleArray.setCoordinate(offsetIndex + 2, new Point3f(geometry.getVertex(i3), geometry.getVertex(i3 + 1), geometry.getVertex(i3 + 2)));
                            triangleArray.setNormal(offsetIndex + 1, new Vector3f(geometry.getNormal(i2), geometry.getNormal(i2 + 1), geometry.getNormal(i2 + 2)));
                            triangleArray.setNormal(offsetIndex + 2, new Vector3f(geometry.getNormal(i3), geometry.getNormal(i3 + 1), geometry.getNormal(i3 + 2)));
                        } // TODO: optimize loop indices
                        Shape3D shape3D = new Shape3D(triangleArray, defaultAppearance);
                        shape3D.setPickable(defaultPickability);
                        shape3D.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
                        shape3D.setCapability(Shape3D.ALLOW_PICKABLE_WRITE);
                        sceneGraphNode.addChild(shape3D); // TODO: loader, which builds transform hierarchy according to the LocalPlacement tree
                        scene.addNamedObject(ifcBuildingElement, shape3D);
                        shape3D.setUserData(ifcBuildingElement);
                    }
                }
            }
        } catch (IfcEngineException e) {
            LOGGER.error("", e);
        }
    }

    public IfcScene load(String fileName) throws FileNotFoundException {
        return load(new FileInputStream(fileName));
    }

    public IfcScene load(URL url) throws FileNotFoundException, IncorrectFormatException, ParsingErrorException {
        try {
            return load(url.openStream());
        } catch (IOException e) {
            throw new FileNotFoundException(e.getMessage()); // strange API
        }
    }

    public IfcScene load(Reader reader) throws FileNotFoundException, IncorrectFormatException, ParsingErrorException {
        return load(new ReaderInputStream(reader));
    }

    public void setBaseUrl(URL url) {
    }

    public void setBasePath(String s) {
    }

    public URL getBaseUrl() {
        return null;
    }

    public String getBasePath() {
        return null;
    }

    public void setFlags(int i) {
    }

    public int getFlags() {
        return LOAD_ALL;
    }

    public void setDefaultAppearance(Appearance defaultAppearance) {
        this.defaultAppearance = defaultAppearance;
    }

    public void setDefaultPickability(boolean defaultPickability) {
        this.defaultPickability = defaultPickability;
    }

    public void dispose() throws IfcEngineException {
        parser.dispose();
    }

    class ParsingErrorException extends com.sun.j3d.loaders.ParsingErrorException {
        // shouldn't this be a checked exception ???
        ParsingErrorException(String msg, Throwable cause) {
            super(msg);
            initCause(cause);
        }
    }
}
