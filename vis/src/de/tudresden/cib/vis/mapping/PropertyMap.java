package de.tudresden.cib.vis.mapping;

import de.tudresden.cib.vis.scene.Event;
import de.tudresden.cib.vis.scene.SceneManager;
import de.tudresden.cib.vis.scene.VisFactory2D;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class PropertyMap<S, T extends VisFactory2D.GraphObject> {
    protected S data;
    protected T graphObject;
    protected int index;

    Class<S> dataClass;
    Class<T> graphClass;

    private Provider<T> provider;
    private SceneManager<? super S> sceneManager;

    protected PropertyMap() {
        Type[] actualTypeArguments = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
        dataClass = (Class<S>) getRawType(actualTypeArguments[0]);
        graphClass = (Class<T>) getRawType(actualTypeArguments[1]);
    }

    private Type getRawType(Type dataType) {
        return (dataType instanceof ParameterizedType ? ((ParameterizedType) dataType).getRawType() : dataType);
    }

    public T map(S source, T target, int i) {
        this.index = i;
        this.data = source;
        this.graphObject = target;
        configure();
        sceneManager.addMapped(source, target);
        return graphObject;
    }

    public T map(S source, Class<T> targetClass, int i) throws IllegalAccessException, InstantiationException {
        T target = targetClass.newInstance();
        return map(source, target, i);
    }

    public T map(S source, int i) throws TargetCreationException {
        if (provider == null) {
            throw new TargetCreationException("missing provider"); // TODO: how to handle or avoid?
        }
        return map(source, provider.create(), i);
    }

    public void with(Provider<T> provider) {
        this.provider = provider;
    }

    protected void addChange(int time, de.tudresden.cib.vis.scene.Change<T> change){
        sceneManager.addChange(time, graphObject, change);
    }

    protected void addChange(Event event, de.tudresden.cib.vis.scene.Change<T> change){
        sceneManager.addChange(event, graphObject, change);
    }

    protected void addTrigger(Event event){
        sceneManager.addTrigger(event, graphObject);
    }

    public void with(SceneManager<? super S> sceneManager) {
        this.sceneManager = sceneManager;
    }

    protected abstract void configure();

    public boolean checkCondition(S source) {
        this.data = source;
        return condition();
    }

    protected boolean condition() {
        return true;
    }

    public interface Provider<T> {
        T create();
    }
}
