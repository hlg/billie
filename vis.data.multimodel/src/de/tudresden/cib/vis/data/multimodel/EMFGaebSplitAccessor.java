package de.tudresden.cib.vis.data.multimodel;

import cib.lib.gaeb.model.gaeb.*;
import cib.lib.gaeb.model.gaeb.util.GaebResourceFactoryImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

import java.util.HashMap;
import java.util.Map;

public class EMFGaebSplitAccessor extends EMFGenericAccessor<TgQtySplit> {
    @Override
    protected Resource createResource(URI uri) {
        GaebPackage.eINSTANCE.eClass();
        return new GaebResourceFactoryImpl().createResource(uri);
    }

    @Override
    protected Map<String, TgQtySplit> collectLookUp() {
        // alternatively for(EObject object : this){ // collect rNoParts}
        TgGAEB gaeb = ((DocumentRoot)data).getGAEB();
        Map<String, TgQtySplit> gaebLookUp = new HashMap<String, TgQtySplit>();
        traverseAndCollectLookUp(gaeb.getAward().getBoQ().getBoQBody(), 0, "", gaebLookUp);
        return gaebLookUp;
    }

    private void traverseAndCollectLookUp(TgBoQBody boQBody, int depth, String parentId, Map<String, TgQtySplit> lookup) {
        EList<TgBoQCtgy> boQCtgys = boQBody.getBoQCtgy();
        for (TgBoQCtgy ctgy : boQCtgys) {
            String id = parentId + ctgy.getRNoPart() + ".";
            traverseAndCollectLookUp(ctgy.getBoQBody(), depth + 1, id, lookup);
        }
        TgItemlist itemlist = boQBody.getItemlist();
        if (itemlist != null)
            for (final TgItem item : itemlist.getItem()) {
                int splitIdx = 0;
                for (TgQtySplit split : item.getQtySplit()){
                    lookup.put(namespace + parentId + item.getRNoPart() + "." + splitIdx, split);
                    splitIdx++;
                }
            }
    }

}
