package de.tudresden.cib.vis.data.multimodel;

import de.mefisto.model.container.Container;
import de.mefisto.model.container.ElementaryModel;
import de.mefisto.model.container.ElementaryModelType;
import de.mefisto.model.linkModel.Link;
import de.mefisto.model.linkModel.LinkModel;
import de.mefisto.model.linkModel.LinkObject;
import de.tudresden.cib.vis.data.DataAccessException;
import de.tudresden.cib.vis.data.IndexedDataAccessor;
import de.tudresden.cib.vis.filter.Condition;
import org.bimserver.plugins.PluginManager;
import org.eclipse.emf.common.util.EList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

public class MultiModelAccessor<K> extends BaseMultiModelAccessor<LinkedObject<K>> {

    private Collection<LinkedObject<K>> groupedElements;
    private ElementaryModelType keyModelType = ElementaryModelType.OBJECT;
    private EMTypeCondition keyModel = null;
    private EMTypeCondition[] requiredModels = null;

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

    public void readFromFolder(File folder) throws DataAccessException {
        if (keyModel!=null && requiredModels != null) try {
            readFromFolder(folder, keyModel, requiredModels);
        } catch (MalformedURLException e) {
            throw new DataAccessException(e);
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

    public LinkedList<String> readFromFolder(File folder, EMTypeCondition keyModel, EMTypeCondition... requiredModels) throws MalformedURLException, DataAccessException {
       return readFromFolder(folder, null, keyModel, requiredModels);
    }

    public LinkedList<String> read(File file, EMTypeCondition keyModel, EMTypeCondition... requiredModels) throws IOException, DataAccessException {
       return readFromFolder(file.isDirectory() ? file : unzip(new FileInputStream(file)), keyModel, requiredModels);
    }

    public LinkedList<String> read(File file, String linkModelId, EMCondition keyModel, EMCondition... requiredModels) throws IOException, DataAccessException {
        return readFromFolder(file.isDirectory() ? file : unzip(new FileInputStream(file)), linkModelId, keyModel, requiredModels);
    }

    public LinkedList<String> readFromFolder(File folder, String linkModelId, EMCondition keyModel, EMCondition... requiredModels) throws MalformedURLException, DataAccessException {
        // TODO: specify model by ID or additional conditions?
        Container container = readContainer(folder);
        EList<ElementaryModel> foundModels = container.getElementaryModelGroup().getElementaryModels();
        LinkedList<String> modelIds = new LinkedList<String>();
        List<String> candidateKeyModels = findModelsOfType(folder, keyModel, foundModels);
        if(candidateKeyModels.size()>1) throw new DataAccessException("ambiguous key model conditions");
        if(candidateKeyModels.size()==0) throw new DataAccessException("no key model found");
        String keyModelId = candidateKeyModels.get(0);
        modelIds.add(keyModelId);
        for(EMCondition required: requiredModels){
            List<String> modelsOfType = findModelsOfType(folder, required, foundModels);
            if(modelsOfType.isEmpty()) throw new DataAccessException("required model missing: " + required.toString());
            modelIds.addAll(modelsOfType);
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
                IndexedDataAccessor data = getAccessor(linkedElement.getModelID());
                if (data != null) {
                    resolveAndAddObject(data, linkedElement, resolved);
                }
            }
        }
        return resolved;
    }

    private <I> void resolveAndAddObject(IndexedDataAccessor<I, Condition<I>> dataAccessor, Link linkedElement, LinkedObject.ResolvedLink resolved) {
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

    public void addAcessor(String key, IndexedDataAccessor accessor) throws DataAccessException {
        accessor.index();
        elementaryModels.put(key, accessor);
    }

    public void setModels(EMTypeCondition keyModel, EMTypeCondition... requiredModels) {
        this.keyModel = keyModel;
        this.requiredModels = requiredModels;
    }

}
