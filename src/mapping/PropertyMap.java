package mapping;

import visualization.VisFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class PropertyMap<S,T extends VisFactory.GraphObject> {
    protected S data;
    protected T graphObject;

    Class<S> dataClass;
    Class<T> graphClass;

    private Provider<T> provider;
    
    protected PropertyMap(){
        Type[] actualTypeArguments = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
        dataClass = (Class<S>) actualTypeArguments[0];
        graphClass = (Class<T>) actualTypeArguments[1];
    }

    public T map(S source, T target){
        this.data = source;
        this.graphObject = target;
        configure();
        return graphObject;
    }

    public T map(S source, Class<T> targetClass) throws IllegalAccessException, InstantiationException {
        T target = targetClass.newInstance();
        return map(source, target);
    }

    public T map(S source) throws TargetCreationException {
        if(provider == null){
            throw new TargetCreationException("missing provider");
        }
        return map(source, provider.create());
    }

    public void with(Provider<T> provider){
        this.provider = provider;
    }

    protected abstract void configure();

    public interface Provider<T> {
        T create();
    }
}
