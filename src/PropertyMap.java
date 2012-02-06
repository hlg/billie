import javax.xml.transform.Source;

public abstract class PropertyMap<S,T> {
    S source;
    T target;
    VisBuilder builder;
    private Provider<T> provider;

    protected void map(S source, T target){
        this.source = source;
        this.target = target;
        configure();
    }

    protected void map(S source, Class<T> targetClass) throws IllegalAccessException, InstantiationException {
        T target = targetClass.newInstance();
        map(source, target);
    }

    protected void map(S source) throws TargetCreationException {
        if(provider == null){
            throw new TargetCreationException("missing provider");
        }
        map(source, provider.create());
    }

    protected void with(Provider<T> provider){
        this.provider = builder.getProvider(target.getClass());
        this.provider = provider;
    }

    protected abstract void configure();

    interface Provider<T> {
        T create();
    }
}
