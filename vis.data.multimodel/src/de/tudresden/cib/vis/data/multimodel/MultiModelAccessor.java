package de.tudresden.cib.vis.data.multimodel;

import de.mefisto.model.container.Container;
import de.mefisto.model.container.ElementaryModel;
import de.mefisto.model.container.ElementaryModelType;
import de.mefisto.model.linkModel.Link;
import de.mefisto.model.linkModel.LinkModel;
import de.mefisto.model.linkModel.LinkObject;
import de.tudresden.cib.vis.data.IndexedDataAccessor;
import org.bimserver.plugins.PluginManager;
import org.eclipse.emf.common.util.EList;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class MultiModelAccessor<K> extends BaseMultiModelAccessor<LinkedObject<K>> {

    private Collection<LinkedObject<K>> groupedElements;
    private ElementaryModelType keyModelType = ElementaryModelType.OBJECT;
    private EMTypes keyModel = null;
    private EMTypes[] requiredModels = null;

    public MultiModelAccessor(PluginManager pm) {
        EMTypes.pm = pm;
    }

    public MultiModelAccessor(Map<ElementaryModel, IndexedDataAccessor> ems, LinkModel lm){
        String groupingModelId = null;
        for(Map.Entry<ElementaryModel, IndexedDataAccessor> model: ems.entrySet()){
            if(model.getKey().getType().equals(keyModelType)) groupingModelId = model.getKey().getId();
            elementaryModels.put(model.getKey().getId(), model.getValue());
        }
        if (groupingModelId!=null) groupBy(groupingModelId, lm);
    }

    public void readFromFolder(File folder) {
        if (keyModel!=null && requiredModels != null) try {
            readFromFolder(folder, keyModel, requiredModels);
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        Container container = readContainer(folder);
        String firstAccesibleKeyModelId = null;
        EList<ElementaryModel> foundModels = container.getElementaryModelGroup().getElementaryModels();
        for (ElementaryModel elementaryModel : foundModels) {
            IndexedDataAccessor accessor = firstAccessible(folder, elementaryModel);
            if (accessor != null) {
                elementaryModels.put(elementaryModel.getId(), accessor);
                if (elementaryModel.getType().equals(keyModelType))
                    firstAccesibleKeyModelId = elementaryModel.getId();
            }
        }
        if (firstAccesibleKeyModelId != null) {
            try {
                LinkModel linkModel = readLinkModel(folder, container, null);
                groupBy(firstAccesibleKeyModelId, linkModel);
            } catch (MalformedURLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public LinkedList<String> readFromFolder(File folder, EMTypes keyModel, EMTypes... requiredModels) throws MalformedURLException {
       return readFromFolder(folder, null, keyModel, requiredModels);
    }

    public LinkedList<String> readFromFolder(File folder, String linkModelId, EMTypes keyModel, EMTypes... requiredModels) throws MalformedURLException {
        // TODO: specify model by ID or additional conditions?
        Container container = readContainer(folder);
        EList<ElementaryModel> foundModels = container.getElementaryModelGroup().getElementaryModels();
        LinkedList<String> modelIds = new LinkedList<String>();
        String keyModelId = findModelOfType(folder, keyModel, foundModels);
        modelIds.add(keyModelId);
        for(EMTypes required: requiredModels){
            modelIds.add(findModelOfType(folder, required, foundModels));
        }
        LinkModel linkModel = readLinkModel(folder, container, linkModelId);
        groupBy(keyModelId, linkModel);
        return modelIds;
    }

    void groupBy(String groupingModelId, LinkModel linkModel) {
        Map<K, LinkedObject<K>> trackMap = new HashMap<K, LinkedObject<K>>();
        for (LinkObject link : linkModel.getLinkObjects()) {
            K keyObject = resolveKey(link, groupingModelId);
            if (keyObject != null) {
                if(!trackMap.containsKey(keyObject)) {
                    trackMap.put(keyObject, new LinkedObject<K>(keyObject));
                }  
                trackMap.get(keyObject).addLink(resolveLink(link, groupingModelId));
            }
        }
        groupedElements = trackMap.values();
    }

    public void groupBy(String groupingModelId, File linkModelFile){
        groupBy(groupingModelId, readLinkModel(linkModelFile));
    }

    public void sort(Comparator<LinkedObject<K>> comparator){
        // todo: also for ordinary DataAccessors
        SortedSet<LinkedObject<K>> sorted = new TreeSet<LinkedObject<K>>(comparator);
        sorted.addAll(groupedElements);
        groupedElements = sorted;
    }

    private LinkedObject.ResolvedLink resolveLink(LinkObject link, String groupingModelId) {
        LinkedObject.ResolvedLink resolved = new LinkedObject.ResolvedLink();
        for (Link linkedElement : link.getLinks()) {
            if (!linkedElement.getModelID().equals(groupingModelId)) {
                IndexedDataAccessor<?> data = getAccessor(linkedElement.getModelID());
                if (data != null) {
                    resolveAndAddObject(data, linkedElement, resolved);
                }
            }
        }
        return resolved;
    }

    private <I> void resolveAndAddObject(IndexedDataAccessor<I> dataAccessor, Link linkedElement, LinkedObject.ResolvedLink resolved) {
        I linkedObject = dataAccessor.getIndexed(linkedElement.getObjectID());
        resolved.addObject(linkedElement.getModelID(), linkedObject);
    }

    private K resolveKey(LinkObject link, String groupingModelId) {
        for (Link linkedElement : link.getLinks()) {
            IndexedDataAccessor data = getAccessor(linkedElement.getModelID());
            if (data != null) {
                Object linkedObject = data.getIndexed(linkedElement.getObjectID());
                if (linkedElement.getModelID().equals(groupingModelId))
                    return (K) linkedObject; // TODO: make sure groupingModel and keyObjectType correspond
            }
        }
        return null;
    }

    public Iterator<LinkedObject<K>> iterator() {
        return groupedElements.iterator();
    }

    public void read(InputStream inputStream, long size) throws IOException {
        readFromFolder(unzip(inputStream));
    }

    public void read(URL resource) {
        readFromFolder(new File(resource.getFile()));
    }

    public void addAcessor(String key, IndexedDataAccessor accessor) {
        accessor.index();
        elementaryModels.put(key, accessor);
    }

    public void setModels(EMTypes keyModel, EMTypes... requiredModels) {
        this.keyModel = keyModel;
        this.requiredModels = requiredModels;
    }

}
