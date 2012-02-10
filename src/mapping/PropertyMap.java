package mapping;

import visualization.VisFactory;

public abstract class PropertyMap<S,T extends VisFactory.GraphObject> {
    protected S data;
    protected T graphObject;
    private Provider<T> provider;

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
