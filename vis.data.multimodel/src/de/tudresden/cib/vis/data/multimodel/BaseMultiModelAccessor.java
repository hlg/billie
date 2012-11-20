package de.tudresden.cib.vis.data.multimodel;

import de.mefisto.model.container.*;
import de.mefisto.model.linkModel.LinkModel;
import de.mefisto.model.parser.ContainerModelParser;
import de.mefisto.model.parser.LinkModelParser;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.IndexedDataAccessor;
import org.eclipse.emf.common.util.EList;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public abstract class BaseMultiModelAccessor<K> extends DataAccessor<K> {
    protected Map<String, IndexedDataAccessor> elementaryModels = new HashMap<String, IndexedDataAccessor>();

    protected Container readContainer(File folder) {
        File mmFile = new File(folder, "MultiModel.xml");
        assert folder.exists() && mmFile.exists();
        return ContainerModelParser.readContainerModel(mmFile).getContainer();
    }

    protected String findModelOfType(File folder, EMCondition modelType, EList<ElementaryModel> foundModels) {
        String modelId = findAndReadModel(folder, foundModels, modelType);
        if(modelId==null) throw new RuntimeException(modelType.getErrorMessage());
        return modelId;
    }

    protected LinkModelDescriptor findLinkModel(Container container, String linkModelId) {
        EList<LinkModelDescriptor> linkModelDescriptors = container.getLinkModelDescriptorGroup().getLinkModelDescriptors();
        if(linkModelId==null) return linkModelDescriptors.get(0);
        else {
            for (LinkModelDescriptor lmd : linkModelDescriptors){
                if(lmd.getId().equals(linkModelId)) return lmd;
            }
        }
        return null;
    }

    private String findAndReadModel(File folder, EList<ElementaryModel> foundModels, EMCondition required){
        for (ElementaryModel model: foundModels){
            if(required.isValidFor(model)){
                for(Content alternative: model.getContent()){
                    if(required.isValidFor(alternative)){
                        IndexedDataAccessor accessor = (required instanceof EMTypeCondition) ?
                                ((EMTypeCondition)required).required.createAccessor() :
                                EMTypes.find(model.getType().getName(), alternative.getFormat(), alternative.getFormatVersion()).createAccessor();
                        readEM(folder, alternative, accessor);
                        elementaryModels.put(model.getId(), accessor);
                        return model.getId();
                    }
                }
            }
        }
        return null;
    }

    protected LinkModel readLinkModel(File folder, Container container, String linkModelId) throws MalformedURLException {
        return readLinkModel(folder, findLinkModel(container, linkModelId));
    }

    private LinkModel readLinkModel(File folder, LinkModelDescriptor linkModelDesc) throws MalformedURLException {
        File linkFile = new File(folder, new URL(linkModelDesc.getFile()).getFile());
        return readLinkModel(linkFile);
    }

    protected LinkModel readLinkModel(File linkFile) {
        return LinkModelParser.readLinkModel(linkFile).getLinkModel();
    }

    protected void readEM(File mmFolder, Content content, IndexedDataAccessor accessor) {
        for (ContainerFile contentFile : content.getFiles()) {
            try {
                File file = new File(mmFolder, new URL(contentFile.getValue()).getFile());
                accessor.read(new FileInputStream(file), contentFile.getNamespace(), file.length()); // TODO: accessor should join multiple sucessively set/added files
            } catch (MalformedURLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        accessor.index();
    }

    protected IndexedDataAccessor firstAccessible(File mmFolder, ElementaryModel elementaryModel) {
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

    protected File unzip(InputStream inputStream) throws IOException {
        File tmp = new File("tmpunzip");
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
                for (int c = zip.read(); c != -1; c = zip.read()) {
                    fos.write(c);
                }
            }
            zipEntry = zip.getNextEntry();
        }
        return tmp;
    }

    public IndexedDataAccessor getAccessor(String modelId) {
        return elementaryModels.get(modelId);
    }

    interface EMCondition {
        boolean isValidFor(ElementaryModel model);
        boolean isValidFor(Content alternative);
        String getErrorMessage();
    }

}
