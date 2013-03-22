package de.tudresden.cib.vis.mapping;

import de.tudresden.cib.vis.TriggerListener;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.scene.SceneManager;
import de.tudresden.cib.vis.scene.VisBuilder;
import de.tudresden.cib.vis.scene.VisFactory2D;

import java.util.ArrayList;
import java.util.Collection;

public abstract class Configuration<E, F> {
    protected Mapper<E, ?, F> mapper;
    public Collection<TriggerListener<E>> listeners = new ArrayList<TriggerListener<E>>();

    public <Z extends VisFactory2D.GraphObject> Configuration(DataAccessor<E> data, VisFactory2D visFactory, VisBuilder<Z, F> visBuilder) {
        mapper = new Mapper<E, Z, F>(data, visFactory, visBuilder);
    }

    public Configuration(Mapper<E, ?, F> mapper) {
        this.mapper = mapper;
    }

    public abstract void config();

    public SceneManager<E, F> execute() throws TargetCreationException {
        return mapper.map();
    }
}
