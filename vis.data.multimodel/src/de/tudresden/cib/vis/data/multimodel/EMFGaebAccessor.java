package de.tudresden.cib.vis.data.multimodel;

import cib.lib.gaeb.model.gaeb.*;
import cib.lib.gaeb.model.gaeb.util.GaebResourceFactoryImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class EMFGaebAccessor extends EMFGenericAccessor<TgItem> {

    public EMFGaebAccessor(){
        super();
    }

    public EMFGaebAccessor(URL url) throws IOException {
        super(url);
    }

    public EMFGaebAccessor(URL url, String nameSpace) throws IOException {
        super(url, nameSpace);
    }

    public EMFGaebAccessor(EObject data) {
        super(data);
    }

    @Override
    protected Resource createResource(URI uri) {
        GaebPackage.eINSTANCE.eClass();
        GaebResourceFactoryImpl gaebResourceFactory = new GaebResourceFactoryImpl();
        return gaebResourceFactory.createResource(uri);
    }

    @Override
    protected Map<String, TgItem> collectLookUp() {
        // alternatively for(EObject object : this){ // collect rNoParts}
        TgGAEB gaeb = ((DocumentRoot)data).getGAEB();
        Map<String, TgItem> gaebLookUp = new HashMap<String, TgItem>();
        traverseAndCollectLookUp(gaeb.getAward().getBoQ().getBoQBody(), 0, "", gaebLookUp);
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
                lookup.put(namespace + parentId + item.getRNoPart() + ".", item);
            }
    }

}
