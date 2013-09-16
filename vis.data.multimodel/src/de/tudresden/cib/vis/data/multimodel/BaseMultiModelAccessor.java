package de.tudresden.cib.vis.data.multimodel;

import de.mefisto.model.container.*;
import de.mefisto.model.linkModel.LinkModel;
import de.mefisto.model.parser.ContainerModelParser;
import de.mefisto.model.parser.LinkModelParser;
import de.tudresden.cib.vis.data.DataAccessException;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.IndexedDataAccessor;
import de.tudresden.cib.vis.filter.Condition;
import de.tudresden.cib.vis.filter.ConditionFilter;
import org.apache.commons.io.IOUtils;
import org.eclipse.emf.common.util.EList;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public abstract class BaseMultiModelAccessor<K> extends DataAccessor<K, Condition<K>> {
    protected Map<String, IndexedDataAccessor> elementaryModels = new HashMap<String, IndexedDataAccessor>();
    private ConditionFilter<K> filter = new ConditionFilter<K>();

    protected Container readContainer(File folder) throws DataAccessException {
        File mmFile = new File(folder, "MultiModel.xml");
        if(!mmFile.exists()) throw new DataAccessException("not a valid multi model container folder");
        return ContainerModelParser.readContainerModel(mmFile).getContainer();
    }

    protected List<String> findModelsOfType(File folder, EMCondition modelType, EList<ElementaryModel> candidateModels) throws DataAccessException {
        List<String> matchingModels = new ArrayList<String>();
        for (ElementaryModel model: candidateModels){
            if(modelType.isValidFor(model)){
                for(Content alternative: model.getContent()){
                    if(modelType.isValidFor(alternative)){
                        IndexedDataAccessor accessor = (modelType instanceof EMTypeCondition) ?
                                ((EMTypeCondition) modelType).required.createAccessor() :
                                EMTypes.find(model.getType().getName(), alternative.getFormat(), alternative.getFormatVersion()).createAccessor();
                        readEM(folder, alternative, accessor);
                        elementaryModels.put(model.getId(), accessor);
                        matchingModels.add(model.getId());
                    }
                }
            }
        }
        return matchingModels;
    }

    protected LinkModelDescriptor findLinkModel(Container container, String linkModelId) throws DataAccessException {
        EList<LinkModelDescriptor> linkModelDescriptors = container.getLinkModelDescriptorGroup().getLinkModelDescriptors();
        if(linkModelId==null) return linkModelDescriptors.get(0);
        else {
            for (LinkModelDescriptor lmd : linkModelDescriptors){
                if(lmd.getId().equals(linkModelId)) return lmd;
            }
        }
        throw new DataAccessException("no link model with id " + linkModelId);
    }

    protected LinkModel readLinkModel(File folder, Container container, String linkModelId) throws MalformedURLException, DataAccessException {
        return readLinkModel(folder, findLinkModel(container, linkModelId));
    }

    private LinkModel readLinkModel(File folder, LinkModelDescriptor linkModelDesc) throws MalformedURLException {
        File linkFile = new File(folder, new URL(linkModelDesc.getFile()).getFile());
        return readLinkModel(linkFile);
    }

    protected LinkModel readLinkModel(File linkFile) {
        return LinkModelParser.readLinkModel(linkFile).getLinkModel();
    }

    protected void readEM(File mmFolder, Content content, IndexedDataAccessor accessor) throws DataAccessException {
        for (ContainerFile contentFile : content.getFiles()) {
            try {
                File file = new File(mmFolder, new URL(contentFile.getValue()).getFile());
                accessor.read(new FileInputStream(file), contentFile.getNamespace(), file.length()); // TODO: accessor should join multiple sucessively set/added files
            } catch (MalformedURLException e) {
                throw new DataAccessException(e);
            } catch (IOException e) {
                throw new DataAccessException(e);
            }
        }
        accessor.index();
    }

    protected IndexedDataAccessor firstAccessible(File mmFolder, ElementaryModel elementaryModel) throws DataAccessException {
        for (Content content : elementaryModel.getContent()) {
            EMTypes recognizedType = EMTypes.find(elementaryModel.getType().getName(), content.getFormat(), content.getFormatVersion());
            if (recognizedType != null) {
                IndexedDataAccessor accessor = recognizedType.createAccessor();
                readEM(mmFolder, content, accessor);
                return accessor;
            }
        }
        return null;
    }

    public File unzip(InputStream inputStream) throws IOException {
        File tmp = new File("tmpunzip");
        if(tmp.exists()) tmp.delete();
        tmp.mkdir();
        tmp.deleteOnExit();
        ZipInputStream zip = new ZipInputStream(inputStream);
        ZipEntry zipEntry = zip.getNextEntry();
        while (zipEntry != null) {
            File file = new File(tmp, zipEntry.getName());
            if (zipEntry.isDirectory()) file.mkdirs();
            else {
                file.getParentFile().mkdirs();
                FileOutputStream fos = new FileOutputStream(file);
                IOUtils.copy(zip, fos);
                fos.close();
            }
            zipEntry = zip.getNextEntry();
        }
        zip.close();
        return tmp;
    }

    public IndexedDataAccessor getAccessor(String modelId) {
        return elementaryModels.get(modelId);
    }

    @Override
    public Iterable<? extends K> filter(Condition<K> condition) {
        return filter.filter(condition,this);
    }

    @Override
    public Condition<K> getDefaultCondition() {
        return new Condition<K>() {
            @Override
            public boolean matches(K data) {
                return true;
            }
        };
    }

    interface EMCondition {
        boolean isValidFor(ElementaryModel model);
        boolean isValidFor(Content alternative);
        String getErrorMessage();
    }

}
