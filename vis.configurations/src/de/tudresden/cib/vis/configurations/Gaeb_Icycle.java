package de.tudresden.cib.vis.configurations;

import cib.lib.gaeb.model.gaeb.TgBoQCtgy;
import cib.lib.gaeb.model.gaeb.TgItem;
import de.tudresden.cib.vis.TriggerListener;
import de.tudresden.cib.vis.data.Hierarchic;
import de.tudresden.cib.vis.data.multimodel.HierarchicGaebAccessor;
import de.tudresden.cib.vis.mapping.Configuration;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.Change;
import de.tudresden.cib.vis.scene.DefaultEvent;
import de.tudresden.cib.vis.scene.VisFactory2D;
import org.eclipse.emf.ecore.EObject;

public class Gaeb_Icycle<S> extends Configuration<Hierarchic<EObject>, S> {

    private int scale = 1;
    private boolean withLabels = false;

    public Gaeb_Icycle(Mapper<Hierarchic<EObject>, ?, S> mapper) {
        super(mapper);
    }

    public Gaeb_Icycle(Mapper<Hierarchic<EObject>, ?, S> mapper, int scale) {
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
            mapper.addMapping(new PropertyMap<HierarchicGaebAccessor.HierarchicTgItemBoQCtgy, VisFactory2D.Label>() {
                @Override
                protected boolean condition() {
                    return data.getChildren().isEmpty();
                }

                @Override
                protected void configure() {
                    TgItem object = (TgItem) data.getObject();
                    graphObject.setLeft(data.getNodesBefore() * scale + 15);
                    graphObject.setTop(data.getDepth() * 25 + 40);
                    String title = object.getID();
                    graphObject.setText(title.length() <= 20 ? title : "... " + title.substring(title.length() - 20, title.length() - 1));
                    graphObject.setRotation(90);
                }
            });
        mapper.addMapping(new PropertyMap<HierarchicGaebAccessor.HierarchicTgItemBoQCtgy, VisFactory2D.Label>() {
            @Override
            protected boolean condition() {
                return !data.getChildren().isEmpty();
            }

            @Override
            protected void configure() {
                TgBoQCtgy object = (TgBoQCtgy) data.getObject();
                graphObject.setLeft(data.getNodesBefore() * scale + 5);
                graphObject.setTop(data.getDepth() * 25 + 5);
                String title = object.getID();
                int doubleNodeSize = data.getNodeSize() * 2;
                graphObject.setText(title.length() <= doubleNodeSize ? title : "... " + title.substring(title.length() - doubleNodeSize, title.length() - 1));
                addTrigger(DefaultEvent.CLICK);
            }
        });
    }
}
