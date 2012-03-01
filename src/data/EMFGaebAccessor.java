package data;

import cib.lib.gaeb.model.gaeb.*;
import cib.lib.gaeb.model.gaeb.util.GaebResourceFactoryImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EMFGaebAccessor implements IndexedDataAccessor<EObject> {

    Map<String, TgItem> index;
    EObject data;
    private String namespace = "";

    public EMFGaebAccessor(){

    }

    public EMFGaebAccessor(URL url) throws IOException {
        URI fileUri = URI.createFileURI(url.getPath());
        Resource resource = createResource(fileUri);
        resource.load(null);
        data = resource.getContents().get(0);
    }

    public EMFGaebAccessor(InputStream inputStream) throws IOException {
        URI fakeUri = URI.createURI("inputstream://fake.resource.uri");
        Resource resource = createResource(fakeUri);
        resource.load(inputStream, null);
        data = resource.getContents().get(0);
    }

    private Resource createResource(URI uri) {
        GaebPackage.eINSTANCE.eClass();
        GaebResourceFactoryImpl gaebResourceFactory = new GaebResourceFactoryImpl();
        return gaebResourceFactory.createResource(uri);
    }

    public Iterator<EObject> iterator() {
        return data.eAllContents();
    }

    public void setInput(File file) throws IOException {
        URI fileUri = URI.createFileURI(file.getPath());
        Resource resource = createResource(fileUri);
        resource.load(null);
        data = resource.getContents().get(0);
    }

    public void index() {
        index = collectLookUp(((DocumentRoot)data).getGAEB(), namespace);
        // alternatively for(EObject object : this){ // collect rNoParts}
    }

    public void setInput(File file, String namespace) throws IOException {
        setInput(file);
        this.namespace = namespace+"::";
    }

    private Map<String, TgItem> collectLookUp(TgGAEB gaeb, String indexPrefix) {
        Map<String, TgItem> gaebLookUp = new HashMap<String, TgItem>();
        traverseAndCollectLookUp(gaeb.getAward().getBoQ().getBoQBody(), 0, indexPrefix, gaebLookUp);
        return gaebLookUp;
    }

    private void traverseAndCollectLookUp(TgBoQBody boQBody, int depth, String parentId, Map<String, TgItem> lookup) {
        EList<TgBoQCtgy> boQCtgys = boQBody.getBoQCtgy();
        for (TgBoQCtgy ctgy : boQCtgys) {
            String id = parentId + ctgy.getRNoPart() + ".";
            traverseAndCollectLookUp(ctgy.getBoQBody(), depth + 1, id, lookup);
        }
        TgItemlist itemlist = boQBody.getItemlist();
        if (itemlist != null)
            for (TgItem item : itemlist.getItem()) {
                lookup.put(parentId + item.getRNoPart() + ".", item);
            }
    }

    public EObject getIndexed(String objectID) {
        return index.get(objectID);
    }
}
