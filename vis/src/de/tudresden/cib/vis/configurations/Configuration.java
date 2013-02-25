package de.tudresden.cib.vis.configurations;

import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.TargetCreationException;
import de.tudresden.cib.vis.scene.SceneManager;
import de.tudresden.cib.vis.scene.VisBuilder;
import de.tudresden.cib.vis.scene.VisFactory2D;

public abstract class Configuration<E, F> {
    protected Mapper<E, ?, F> mapper;

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
