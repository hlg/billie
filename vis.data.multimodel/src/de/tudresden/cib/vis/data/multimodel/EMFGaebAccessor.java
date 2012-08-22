package de.tudresden.cib.vis.data.multimodel;

import cib.lib.gaeb.model.gaeb.*;
import cib.lib.gaeb.model.gaeb.util.GaebResourceImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class EMFGaebAccessor extends EMFGenericAccessor<TgItem> {

    public EMFGaebAccessor(){

    }

    public EMFGaebAccessor(URL url) throws IOException {
        super(url);
    }

    public EMFGaebAccessor(InputStream inputStream) throws IOException {
        super(inputStream);
    }

    public EMFGaebAccessor(InputStream stream, String nameSpace) throws IOException {
        super(stream, nameSpace);
    }

    public EMFGaebAccessor(EObject data) {
        super(data);
    }

    @Override
    protected Resource createResource(URI uri) {
        GaebPackage.eINSTANCE.eClass();
        return new GaebResourceImpl(uri);
    }

    @Override
    protected Map<String, TgItem> collectLookUp() {
        // alternatively for(EObject object : this){ // collect rNoParts}
        TgGAEB gaeb = ((DocumentRoot)data).getGAEB();
        Map<String, TgItem> gaebLookUp = new HashMap<String, TgItem>();
        traverseAndCollectLookUp(gaeb.getAward().getBoQ().getBoQBody(), 0, namespace, gaebLookUp);
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

}
