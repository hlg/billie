package de.tudresden.cib.vis.configurations;

import de.tudresden.cib.vis.data.Hierarchic;
import de.tudresden.cib.vis.data.bimserver.EMFIfcHierarchicAcessor;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.VisFactory2D;
import org.bimserver.emf.IdEObject;
import org.bimserver.models.ifc2x3tc1.IfcRoot;
import org.bimserver.models.ifc2x3tc1.IfcSpatialStructureElement;

public class Ifc_Icycle<S> extends Configuration<Hierarchic<IdEObject>, S> {

    private int scale = 1;
    private static boolean WITH_LABELS = false;
    private static boolean SKIP_LAST_LEVEL = true;

    public Ifc_Icycle(Mapper<Hierarchic<IdEObject>, ?, S> mapper, int scale) {
        super(mapper);
        this.scale = scale;
    }

    public Ifc_Icycle(Mapper<Hierarchic<IdEObject>, ?, S> mapper) {
        super(mapper);
    }


    @Override
    public void config() {
        mapper.addMapping(new PropertyMap<EMFIfcHierarchicAcessor.HierarchicIfc, VisFactory2D.Rectangle>() {
            @Override
            protected boolean condition() {
                return !SKIP_LAST_LEVEL || !data.getChildren().isEmpty();
            }

            @Override
            protected void configure() {
                graphObject.setLeft(data.getNodesBefore() * scale);
                graphObject.setWidth(data.getNodeSize() * scale);
                graphObject.setTop(data.getDepth() * 25);
                graphObject.setHeight(25);
                if (data.getObject() instanceof IfcSpatialStructureElement) graphObject.setColor(200, 200, 200);
            }
        });
        if (WITH_LABELS)
            mapper.addMapping(new PropertyMap<EMFIfcHierarchicAcessor.HierarchicIfc, VisFactory2D.Label>() {
                @Override
                protected boolean condition() {
                    return data.getChildren().isEmpty();
                }

                @Override
                protected void configure() {
                    IfcRoot object = (IfcRoot) data.getObject();
                    graphObject.setLeft(data.getNodesBefore() * scale);
                    graphObject.setTop(data.getDepth() * 25 + 150);
                    String title = object.getName();
                    graphObject.setText(title == null ? "xxx" : title.length() <= 20 ? title : "... " + title.substring(title.length() - 20, title.length() - 1));
                    graphObject.setRotation(-90);
                }
            });
        mapper.addMapping(new PropertyMap<EMFIfcHierarchicAcessor.HierarchicIfc, VisFactory2D.Label>() {
            @Override
            protected boolean condition() {
                return !data.getChildren().isEmpty();
            }

            @Override
            protected void configure() {
                IfcSpatialStructureElement object = (IfcSpatialStructureElement) data.getObject();
                graphObject.setLeft(data.getNodesBefore() * scale + 5);
                graphObject.setTop(data.getDepth() * 25 + 5);
                String title = object.getName();
                int doubleNodeSize = data.getNodeSize() * 2;
                graphObject.setText(title == null ? "xxx" : title.length() <= doubleNodeSize ? title : "... " + title.substring(title.length() - doubleNodeSize, title.length() - 1));
            }
        });
    }

    public void setSkipLastLevel(boolean skipLastLevel) {
        SKIP_LAST_LEVEL = skipLastLevel;
    }

    public void setWithLastLevelLabels(boolean withLabels) {
        WITH_LABELS = withLabels;
    }
}