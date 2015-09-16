package de.tudresden.cib.vis.configurations;

import de.tudresden.cib.vis.TriggerListener;
import de.tudresden.cib.vis.data.Hierarchic;
import de.tudresden.cib.vis.data.bimserver.EMFIfcHierarchicAcessor;
import de.tudresden.cib.vis.filter.Condition;
import de.tudresden.cib.vis.mapping.Configuration;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.Change;
import de.tudresden.cib.vis.scene.DefaultEvent;
import de.tudresden.cib.vis.scene.VisFactory2D;
import org.bimserver.emf.IdEObject;
import org.bimserver.models.ifc2x3tc1.IfcRoot;
import org.bimserver.models.ifc2x3tc1.IfcSpatialStructureElement;

public class Ifc_Icycle extends Configuration<Hierarchic<IdEObject>, Condition<Hierarchic<IdEObject>>> {

    private int scale = 13;
    private boolean WITH_LABELS = false;
    private boolean SKIP_LAST_LEVEL = true;

    public Ifc_Icycle(int scale) {
        this.scale = scale;
    }

    public Ifc_Icycle() {
        super();
    }


    @Override
    public void config() {
        this.addMapping(new Condition<Hierarchic<IdEObject>>() {
            @Override
            public boolean matches(Hierarchic<IdEObject> data) {
                return !SKIP_LAST_LEVEL || !data.getChildren().isEmpty();
            }
        }, new PropertyMap<EMFIfcHierarchicAcessor.HierarchicIfc, VisFactory2D.Rectangle>() {
            @Override
            protected void configure() {
                addTrigger(DefaultEvent.CLICK);
                addChange(DefaultEvent.CLICK, new Change<VisFactory2D.Rectangle>() {
                    private Hierarchic<IdEObject> d = data;
                    @Override
                    protected void configure() {
                        graph.setColor(255, 0, 0);
                        for (TriggerListener<Hierarchic<IdEObject>> listener: listeners) listener.notify(d);
                    }
                });
                graphObject.setLeft(data.getNodesBefore() * scale);
                graphObject.setWidth(data.getNodeSize() * scale);
                graphObject.setTop(data.getDepth() * 25);
                graphObject.setHeight(25);
                if (data.getObject() instanceof IfcSpatialStructureElement) graphObject.setColor(200, 200, 200);
                graphObject.setBackground();
            }
        });
        if (WITH_LABELS)
            this.addMapping(new Condition<Hierarchic<IdEObject>>() {
                @Override
                public boolean matches(Hierarchic<IdEObject> data) {
                    return data.getChildren().isEmpty();
                }
            }, new PropertyMap<EMFIfcHierarchicAcessor.HierarchicIfc, VisFactory2D.Label>() {
                @Override
                protected void configure() {
                    IfcRoot object = (IfcRoot) data.getObject();
                    graphObject.setLeft(data.getNodesBefore() * scale);
                    graphObject.setTop(data.getDepth() * 25 + 40);
/*
                    String title = object.getName();
                    String subtitle = object instanceof IfcSpace ? ((IfcSpace)object).getLongName() : null;
                    if(title!=null && subtitle!=null) title += " " + subtitle;
*/
                    String title= object.getGlobalId().getWrappedValue();
                    graphObject.setText(title == null ? "xxx" : title.length() <= 20 ? title : title.substring(0, 20) + " ..");
                    graphObject.setVertical(true);
                }
            });
        this.addMapping(new Condition<Hierarchic<IdEObject>>() {
            @Override
            public boolean matches(Hierarchic<IdEObject> data) {
                return !data.getChildren().isEmpty();
            }
        }, new PropertyMap<EMFIfcHierarchicAcessor.HierarchicIfc, VisFactory2D.Label>() {
            @Override
            protected void configure() {
                IfcSpatialStructureElement object = (IfcSpatialStructureElement) data.getObject();
                graphObject.setLeft(data.getNodesBefore() * scale + 5);
                graphObject.setTop(data.getDepth() * 25 + 5);
                String title = object.getGlobalId().getWrappedValue(); // object.getName();
                int doubleNodeSize = data.getNodeSize() * 2;
                graphObject.setText(title == null ? "xxx" : title.length() <= doubleNodeSize ? title : title.substring(0,doubleNodeSize));
                graphObject.setForeground();
                addTrigger(DefaultEvent.CLICK);
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
