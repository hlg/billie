package de.tudresden.cib.vis.data.multimodel;

import de.tudresden.cib.vis.data.IndexedDataAccessor;
import de.tudresden.cib.vis.filter.Condition;
import de.tudresden.cib.vis.filter.ConditionFilter;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

public abstract class EMFGenericAccessor<T extends EObject> extends IndexedDataAccessor<EObject, Condition<EObject>> {
    private final ConditionFilter<EObject> filter;
    EObject data;
    Map<String, T> index;   // todo: index for links on categories?

    EMFGenericAccessor(EObject parsed){
        this();
        data = parsed;
    }

    protected EMFGenericAccessor() {
        super();
        filter = new ConditionFilter<EObject>();
    }

    public EMFGenericAccessor(URL url) throws IOException {
        this();
        setData(url);
    }

    public EMFGenericAccessor(URL url, String namespace) throws IOException {
        this();
        this.namespace = namespace + "::";
        setData(url);
    }

    private void setData(URL url) throws IOException {
        URI fakeUri = URI.createURI(url.toString());
        Resource resource = createResource(fakeUri);
        resource.load(null);
        data = resource.getContents().get(0);
    }

    protected abstract Resource createResource(URI uri);

    public Iterator<EObject> iterator() {
        return data.eAllContents();
    }

    public void read(URL url) throws IOException {
        setData(url);
    }

    @Override
    public Iterable<? extends EObject> filter(Condition<EObject> condition) {
        return filter.filter(condition, this);
    }

    @Override
    public Condition<EObject> getDefaultCondition() {
        return new Condition<EObject>(){
            @Override
            public boolean matches(EObject data) {
                return true;
            }
        };
    }

    protected void setData(URI fileUri) throws IOException {
        Resource resource = createResource(fileUri);
        resource.load(null);
        data = resource.getContents().get(0);
    }

    public void index() {
        index = collectLookUp();
    }

    protected abstract Map<String, T> collectLookUp();

    public T getIndexed(String objectID) {
        return index.get(objectID);
    }
}
