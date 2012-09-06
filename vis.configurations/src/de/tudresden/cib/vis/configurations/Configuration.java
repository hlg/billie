package de.tudresden.cib.vis.configurations;

import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.TargetCreationException;
import de.tudresden.cib.vis.scene.VisBuilder;
import de.tudresden.cib.vis.scene.VisFactory2D;

public abstract class Configuration<E, G extends VisFactory2D.GraphObject, S> {
    protected Mapper<E, G, S> mapper;

    public Configuration(DataAccessor<E> data, VisFactory2D visFactory, VisBuilder<G,S> visBuilder) {
        mapper = new Mapper<E, G, S>(data, visFactory, visBuilder);
    }

    public Configuration(Mapper<E, G, S> mapper) {
        this.mapper = mapper;
    }

    public S execute() throws TargetCreationException {
        return mapper.map();
    }
}
