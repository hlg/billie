package mapping;

import visualization.VisFactory2D;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class PropertyMap<S,T extends VisFactory2D.GraphObject> {
    protected S data;
    protected T graphObject;
    protected int index;

    Class<S> dataClass;
    Class<T> graphClass;

    private Provider<T> provider;
    
    protected PropertyMap(){
        Type[] actualTypeArguments = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
        dataClass = (Class<S>) actualTypeArguments[0];
        graphClass = (Class<T>) actualTypeArguments[1];
    }

    public T map(S source, T target, int i){
        this.index = i;
        this.data = source;
        this.graphObject = target;
        configure();
        return graphObject;
    }

    public T map(S source, Class<T> targetClass, int i) throws IllegalAccessException, InstantiationException {
        T target = targetClass.newInstance();
        return map(source, target, i);
    }

    public T map(S source, int i) throws TargetCreationException {
        if(provider == null){
            throw new TargetCreationException("missing provider");
        }
        return map(source, provider.create(), i);
    }

    public void with(Provider<T> provider){
        this.provider = provider;
    }

    protected abstract void configure();

    public boolean checkCondition(S source){
        this.data = source;
        return condition();
    }

    protected boolean condition(){
        return true;
    }

    public interface Provider<T> {
        T create();
    }
}
