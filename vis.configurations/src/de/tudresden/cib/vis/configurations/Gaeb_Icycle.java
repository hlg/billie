package de.tudresden.cib.vis.configurations;

import cib.lib.gaeb.model.gaeb.TgBoQ;
import cib.lib.gaeb.model.gaeb.TgBoQCtgy;
import cib.lib.gaeb.model.gaeb.TgItem;
import de.tudresden.cib.vis.TriggerListener;
import de.tudresden.cib.vis.data.Hierarchic;
import de.tudresden.cib.vis.data.multimodel.HierarchicGaebAccessor;
import de.tudresden.cib.vis.filter.Condition;
import de.tudresden.cib.vis.mapping.Configuration;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.Change;
import de.tudresden.cib.vis.scene.DefaultEvent;
import de.tudresden.cib.vis.scene.VisFactory2D;
import org.eclipse.emf.ecore.EObject;

public class Gaeb_Icycle<S> extends Configuration<Hierarchic<EObject>, Condition<Hierarchic<EObject>>, S> {

    private int scale = 10;
    private boolean withLabels = true;

    public Gaeb_Icycle(Mapper<Hierarchic<EObject>, Condition<Hierarchic<EObject>>, ?, S> mapper) {
        super(mapper);
    }

    public Gaeb_Icycle(Mapper<Hierarchic<EObject>, Condition<Hierarchic<EObject>>, ?, S> mapper, int scale) {
        super(mapper);
        this.scale = scale;
    }

    @Override
    public void config() {
        mapper.addMapping(new PropertyMap<HierarchicGaebAccessor.HierarchicTgItemBoQCtgy, VisFactory2D.Rectangle>() {
            @Override
            protected void configure() {
                graphObject.setLeft(data.getNodesBefore() * scale);
                graphObject.setWidth(data.getNodeSize() * scale);
                graphObject.setTop(data.getDepth() * 25);
                graphObject.setHeight(25);
                graphObject.setBackground();
                addTrigger(DefaultEvent.CLICK);
                addChange(DefaultEvent.CLICK, new Change<VisFactory2D.Rectangle>() {
                    private Hierarchic<EObject> d = data;
                    @Override
                    protected void configure() {
                        graph.setColor(255, 0, 0);
                        for (TriggerListener<Hierarchic<EObject>> listener: listeners) listener.notify(d);
                    }
                });
                // if (data.getObject() instanceof TgBoQCtgy) graphObject.setColor(200, 200, 100);
            }
        });
        if (withLabels)
            mapper.addMapping(new Condition<Hierarchic<EObject>>(){
                                  @Override
                                  public boolean matches(Hierarchic<EObject> data) {
                                      return data.getChildren().isEmpty();
                                  }
                              }, new PropertyMap<HierarchicGaebAccessor.HierarchicTgItemBoQCtgy, VisFactory2D.Label>() {
                @Override
                protected void configure() {
                    TgItem object = (TgItem) data.getObject();
                    graphObject.setLeft(data.getNodesBefore() * scale + 15);
                    graphObject.setTop(data.getDepth() * 25 + 40);
                    String title = object.getDescription().getOutlineText() != null ? object.getDescription().getOutlineText().getOutlTxt().getTextOutlTxt().get(0).getSpan().get(0).getValue() : object.getID();
                    graphObject.setText(title.length() <= 20 ? title : title.substring(0, 20) + " ..");
                    graphObject.setRotation(90);
                }
            });
        mapper.addMapping(new Condition<Hierarchic<EObject>>(){
            @Override
            public boolean matches(Hierarchic<EObject> data) {
                return !data.getChildren().isEmpty();
            }
        }, new PropertyMap<HierarchicGaebAccessor.HierarchicTgItemBoQCtgy, VisFactory2D.Label>() {
            @Override
            protected void configure() {
                graphObject.setLeft(data.getNodesBefore() * scale + 5);
                graphObject.setTop(data.getDepth() * 25 + 5);
                String title = data.getObject() instanceof TgBoQCtgy ? ((TgBoQCtgy) data.getObject()).getLblTx().getSpan().get(0).getValue(): ((TgBoQ)data.getObject()).getBoQInfo().getName(); // object.getID();
                int nodeSize = data.getNodeSize();
                graphObject.setText(title.length() <= nodeSize ? title : title.substring(0, nodeSize) + " ..");
                graphObject.setForeground();
                addTrigger(DefaultEvent.CLICK);
            }
        });
    }
}
