package de.tudresden.cib.vis.mapping;

import de.tudresden.cib.vis.scene.Change;
import de.tudresden.cib.vis.scene.Event;
import de.tudresden.cib.vis.scene.VisFactory2D;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class PropertyMap<S, T extends VisFactory2D.GraphObject> {
    protected S data;
    protected T graphObject;
    protected int index;

    Class<S> dataClass;
    Class<T> graphClass;

    Map<Integer, Change<T>> scheduledChanges = new HashMap<Integer, Change<T>>();
    Map<Event, Change<T>> triggeredChanges = new HashMap<Event, Change<T>>();
    Set<Event> triggers = new HashSet<Event>();

    public PropertyMap(Class<S> data, Class<T> graph){
        dataClass = data;
        graphClass = graph;
    }

    protected PropertyMap() {
        Type[] actualTypeArguments = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
        dataClass = (Class<S>) getRawType(actualTypeArguments[0]);
        graphClass = (Class<T>) getRawType(actualTypeArguments[1]);
    }

    private Type getRawType(Type dataType) {
        return (dataType instanceof ParameterizedType ? ((ParameterizedType) dataType).getRawType() : dataType);
    }

    protected T map(S source, T target, int i) {
        this.index = i;
        this.data = source;
        this.graphObject = target;
        configure();
        return graphObject;
    }

    public T map(S source, Provider<T> provider, int i) throws TargetCreationException {
        if (provider == null) {
            throw new TargetCreationException("missing provider"); // TODO: how to handle or avoid?
        }
        return map(source, provider.create(), i);
    }

    protected void addChange(int time, de.tudresden.cib.vis.scene.Change<T> change){
        scheduledChanges.put(time, change);
    }

    protected void addChange(Event event, de.tudresden.cib.vis.scene.Change<T> change){
        triggeredChanges.put(event, change);
    }

    protected void addTrigger(Event event){
        triggers.add(event);
    }

    protected abstract void configure();

    public interface Provider<T> {
        T create();
    }
}
