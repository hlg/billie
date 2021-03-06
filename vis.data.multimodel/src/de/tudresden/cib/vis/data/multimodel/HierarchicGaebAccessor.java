package de.tudresden.cib.vis.data.multimodel;

import cib.lib.gaeb.model.gaeb.*;
import de.tudresden.cib.vis.data.Hierarchic;
import de.tudresden.cib.vis.data.HierarchicBase;
import de.tudresden.cib.vis.data.IndexedDataAccessor;
import de.tudresden.cib.vis.filter.Condition;
import de.tudresden.cib.vis.filter.ConditionFilter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HierarchicGaebAccessor extends IndexedDataAccessor<Hierarchic<EObject>, Condition<Hierarchic<EObject>>> {

    private final ConditionFilter<Hierarchic<EObject>> filter = new ConditionFilter<Hierarchic<EObject>>();
    private EMFGaebAccessor baseAcessor = new EMFGaebAccessor();
    private Map<String, Hierarchic<EObject>> wrappedData; // TgItem or BoQCtgy

    @Override
    public void index() {
        TgBoQ boQ = ((DocumentRoot) baseAcessor.data).getGAEB().getAward().getBoQ();
        HierarchicTgItemBoQCtgy root= new HierarchicTgItemBoQCtgy(null, 0, 0, boQ);
        root.setNodeSize(traverseAndCollectLookUp(boQ.getBoQBody(), 1, 0, root, ""));
        wrappedData.put("", root);
    }

    @Override
    public Hierarchic<EObject> getIndexed(String objectID) {
        return wrappedData.get(objectID);
    }

    @Override
    public void read(URL url) throws IOException {
        baseAcessor.read(url);
        wrappedData = new HashMap<String, Hierarchic<EObject>>();
    }

    @Override
    public Iterable<? extends Hierarchic<EObject>> filter(Condition<Hierarchic<EObject>> condition) {
        return filter.filter(condition, this);
    }

    @Override
    public Condition<Hierarchic<EObject>> getDefaultCondition() {
        return new Condition<Hierarchic<EObject>>() {
            @Override
            public boolean matches(Hierarchic<EObject> data) {
                return true;
            }
        };
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
            wrappedData.put(namespace+id, node);
            int currSize = traverseAndCollectLookUp(ctgy.getBoQBody(), depth + 1, nodesBefore + size, node, id);
            size += currSize;
            node.setNodeSize(currSize);
            if(parent!=null)parent.addChild(node);
        }
        TgItemlist itemlist = boQBody.getItemlist();
        if (itemlist != null)
            for (TgItem item : itemlist.getItem()) {
                HierarchicTgItemBoQCtgy node = new HierarchicTgItemBoQCtgy(parent, nodesBefore + size, depth, item);
                wrappedData.put(namespace + parentId + item.getRNoPart() + ".", node);
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
