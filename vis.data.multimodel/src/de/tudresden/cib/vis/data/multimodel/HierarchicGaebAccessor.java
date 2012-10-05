package de.tudresden.cib.vis.data.multimodel;

import cib.lib.gaeb.model.gaeb.*;
import de.tudresden.cib.vis.data.Hierarchic;
import de.tudresden.cib.vis.data.HierarchicBase;
import de.tudresden.cib.vis.data.IndexedDataAccessor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HierarchicGaebAccessor extends IndexedDataAccessor<Hierarchic<EObject>> {

    private EMFGaebAccessor baseAcessor = new EMFGaebAccessor();
    private Map<String, Hierarchic<EObject>> wrappedData; // TgItem or BoQCtgy

    @Override
    public void index() {
        TgGAEB gaeb = ((DocumentRoot) baseAcessor.data).getGAEB();
        traverseAndCollectLookUp(gaeb.getAward().getBoQ().getBoQBody(), 0, 0, null, "");

        gaeb.getAward().getBoQ().getBoQBody().getBoQCtgy();
        gaeb.getAward().getBoQ().getBoQBody().getItemlist();
    }

    @Override
    public Hierarchic<EObject> getIndexed(String objectID) {
        return wrappedData.get(objectID);
    }

    @Override
    public void read(InputStream inputStream, long size) throws IOException {
        baseAcessor.read(inputStream, size);
        wrappedData = new HashMap<String, Hierarchic<EObject>>();
    }

    @Override
    public void readFromFolder(File directory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Hierarchic<EObject>> iterator() {
        return wrappedData.values().iterator();
    }

    private int traverseAndCollectLookUp(TgBoQBody boQBody, int depth, int nodesBefore, HierarchicTgItemBoQCtgy parent, String parentId) {
        EList<TgBoQCtgy> boQCtgys = boQBody.getBoQCtgy();
        int size = 0;
        for (TgBoQCtgy ctgy : boQCtgys) {
            String id = parentId + ctgy.getRNoPart() + ".";
            HierarchicTgItemBoQCtgy node = new HierarchicTgItemBoQCtgy(parent, nodesBefore + size, depth, ctgy);
            wrappedData.put(id, node);
            int currSize = traverseAndCollectLookUp(ctgy.getBoQBody(), depth + 1, nodesBefore + size, node, id);
            size += currSize;
            node.setNodeSize(currSize);
            if(parent!=null)parent.addChild(node);
        }
        TgItemlist itemlist = boQBody.getItemlist();
        if (itemlist != null)
            for (TgItem item : itemlist.getItem()) {
                HierarchicTgItemBoQCtgy node = new HierarchicTgItemBoQCtgy(parent, nodesBefore + size, depth, item);
                wrappedData.put(parentId + item.getRNoPart() + ".", node);
                size += 1;
                node.setNodeSize(1);
                parent.addChild(node);
            }
        return size;
    }

    public static class HierarchicTgItemBoQCtgy extends HierarchicBase<EObject> {
        public HierarchicTgItemBoQCtgy(Hierarchic<? extends EObject> parentNode, int nodesBefore, int depth, EObject object) {
            super(parentNode, nodesBefore, depth, object);
        }
    }

}
