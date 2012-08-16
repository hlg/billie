package data.multimodel;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import visMapping.data.IndexedDataAccessor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.Map;

public abstract class EMFGenericAccessor<T extends EObject> extends IndexedDataAccessor<EObject> {
    EObject data;
    Map<String, T> index;   // todo: index for links on categories?
    protected String namespace = "";

    EMFGenericAccessor(URL url) throws IOException {
        setData(URI.createFileURI(URLDecoder.decode(url.getPath(),"UTF-8")));
    }

    protected EMFGenericAccessor() {
    }

    public EMFGenericAccessor(InputStream inputStream) throws IOException {
        setData(inputStream);
    }

    private void setData(InputStream inputStream) throws IOException {
        URI fakeUri = URI.createURI("inputstream://fake.resource.uri");
        Resource resource = createResource(fakeUri);
        resource.load(inputStream, null);
        data = resource.getContents().get(0);
    }

    protected abstract Resource createResource(URI uri);

    public Iterator<EObject> iterator() {
        return data.eAllContents();
    }

    public void setInput(InputStream inputStream) throws IOException {
        setData(inputStream);
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

    public void setInput(InputStream inputStream, String namespace) throws IOException {
        setInput(inputStream);
        this.namespace = namespace + "::";
    }

    public T getIndexed(String objectID) {
        return index.get(objectID);
    }
}
