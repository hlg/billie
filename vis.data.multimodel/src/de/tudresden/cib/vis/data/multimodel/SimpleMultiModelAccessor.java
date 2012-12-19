package de.tudresden.cib.vis.data.multimodel;

import de.mefisto.model.container.Container;
import de.mefisto.model.container.ElementaryModel;
import de.mefisto.model.linkModel.Link;
import de.mefisto.model.linkModel.LinkModel;
import de.mefisto.model.linkModel.LinkObject;
import de.tudresden.cib.vis.data.DataAccessException;
import de.tudresden.cib.vis.data.IndexedDataAccessor;
import de.tudresden.cib.vis.data.bimserver.SimplePluginManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SimpleMultiModelAccessor extends BaseMultiModelAccessor<LinkedObject.ResolvedLink> {
    private List<LinkedObject.ResolvedLink> resolved = new ArrayList<LinkedObject.ResolvedLink>();

    public SimpleMultiModelAccessor(SimplePluginManager pluginManager) {
        EMTypes.pm = pluginManager;
    }

    @Override
    public void read(InputStream inputStream, long size) throws IOException, DataAccessException {
        readFromFolder(unzip(inputStream));
    }

    @Override
    public void readFromFolder(File directory) throws DataAccessException {
        Container container = readContainer(directory);
        for (ElementaryModel em : container.getElementaryModelGroup().getElementaryModels()) {
            IndexedDataAccessor data = firstAccessible(directory, em);
            elementaryModels.put(em.getId(), data);

        }
        try {
            LinkModel linkModel = readLinkModel(directory, container, null);
            resolveLinks(linkModel);
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public LinkedList<String> read(File file, EMTypeCondition... requiredModels) throws IOException, DataAccessException {
        return readFromFolder(file.isDirectory() ? file : unzip(new FileInputStream(file)), requiredModels);
    }

    public LinkedList<String> readFromFolder(File folder, EMTypeCondition... requiredModels) throws MalformedURLException, DataAccessException {
        return readFromFolder(folder, null, requiredModels);
    }
    public LinkedList<String> read(File file, String linkModelId, EMTypeCondition... requiredModels) throws IOException, DataAccessException {
        return readFromFolder(file.isDirectory() ? file : unzip(new FileInputStream(file)), linkModelId, requiredModels);
    }
    public LinkedList<String> readFromFolder(File folder, String linkModelId, EMTypeCondition... requiredModels) throws MalformedURLException, DataAccessException {
        Container container = readContainer(folder);
        LinkedList<String> modelIds = new LinkedList<String>();
        for (EMTypeCondition type : requiredModels) {
            List<String> modelsOfType = findModelsOfType(folder, type, container.getElementaryModelGroup().getElementaryModels());
            if (modelsOfType.isEmpty()) throw new DataAccessException("missing required model: " + type.toString());
            modelIds.addAll(modelsOfType);
        }
        LinkModel linkModel = readLinkModel(folder, container, linkModelId);
        resolveLinks(linkModel);
        return modelIds;
    }

    private void resolveLinks(LinkModel linkModel) {
        for (LinkObject link : linkModel.getLinkObjects()) {
            resolved.add(resolveLink(link));
        }

    }

    private LinkedObject.ResolvedLink resolveLink(LinkObject link) {
        LinkedObject.ResolvedLink resolved = new LinkedObject.ResolvedLink();
        for (Link linkedElement : link.getLinks()) {
            IndexedDataAccessor<?> data = getAccessor(linkedElement.getModelID());
            if (data != null) {
                resolved.addObject(linkedElement.getModelID(), data.getIndexed(linkedElement.getObjectID()));
            }
        }
        return resolved;

    }

    @Override
    public Iterator<LinkedObject.ResolvedLink> iterator() {
        return resolved.iterator();
    }
}
