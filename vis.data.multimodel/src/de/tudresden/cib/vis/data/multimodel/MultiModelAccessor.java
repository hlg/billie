package de.tudresden.cib.vis.data.multimodel;

import de.mefisto.model.container.*;
import de.mefisto.model.linkModel.Link;
import de.mefisto.model.linkModel.LinkModel;
import de.mefisto.model.linkModel.LinkObject;
import de.mefisto.model.parser.ContainerModelParser;
import de.mefisto.model.parser.LinkModelParser;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.IndexedDataAccessor;
import de.tudresden.cib.vis.data.bimserver.EMFIfcAccessor;
import org.bimserver.plugins.PluginManager;
import org.eclipse.emf.common.util.EList;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MultiModelAccessor<K> extends DataAccessor<LinkedObject<K>> {

    private Map<String, IndexedDataAccessor> elementaryModels = new HashMap<String, IndexedDataAccessor>();
    private Collection<LinkedObject<K>> groupedElements;
    private ElementaryModelType keyModelType = ElementaryModelType.OBJECT;

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

    private void readFromFolder(File folder) {
        File mmFile = new File(folder, "MultiModel.xml");
        assert folder.exists() && mmFile.exists();
        Container container = ContainerModelParser.readContainerModel(mmFile).getContainer();
        String firstAccesibleKeyModelId = null;
        EList<ElementaryModel> elementaryModels1 = container.getElementaryModelGroup().getElementaryModels();
        for (ElementaryModel elementaryModel : elementaryModels1) {
            IndexedDataAccessor accessor = firstAccessible(folder, elementaryModel);
            if (accessor != null) {
                elementaryModels.put(elementaryModel.getId(), accessor);
                if (elementaryModel.getType().equals(keyModelType))
                    firstAccesibleKeyModelId = elementaryModel.getId();
            }
        }
        if (firstAccesibleKeyModelId != null) {
            try {
                LinkModel linkModel = readLinkModel(folder, container.getLinkModelDescriptorGroup().getLinkModelDescriptors().get(0));
                groupBy(firstAccesibleKeyModelId, linkModel);
            } catch (MalformedURLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    private LinkModel readLinkModel(File folder, LinkModelDescriptor linkModelDesc) throws MalformedURLException {
        File linkFile = new File(folder, new URL(linkModelDesc.getFile()).getFile());
        return LinkModelParser.readLinkModel(linkFile).getLinkModel();
    }

    void groupBy(String groupingModelId, LinkModel linkModel) {
        Map<K, LinkedObject<K>> trackMap = new HashMap<K, LinkedObject<K>>();
        for (LinkObject link : linkModel.getLinkObjects()) {
            K keyObject = resolveKey(link, groupingModelId);
            if (!trackMap.containsKey(keyObject)) {
                trackMap.put(keyObject, new LinkedObject<K>(keyObject));
            }
            trackMap.get(keyObject).addLink(resolveLink(link, groupingModelId));
        }
        groupedElements = trackMap.values();
    }

    private LinkedObject.ResolvedLink resolveLink(LinkObject link, String groupingModelId) {
        LinkedObject.ResolvedLink resolved = new LinkedObject.ResolvedLink();
        for (Link linkedElement : link.getLinks()) {
            if (!linkedElement.getModelID().equals(groupingModelId)) {
                IndexedDataAccessor<?> data = elementaryModels.get(linkedElement.getModelID());
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
            IndexedDataAccessor data = elementaryModels.get(linkedElement.getModelID());
            if (data != null) {
                Object linkedObject = data.getIndexed(linkedElement.getObjectID());
                if (linkedElement.getModelID().equals(groupingModelId))
                    return (K) linkedObject; // TODO: make sure groupingModel and keyObjectType correspond
            }
        }
        return null;
    }

    private IndexedDataAccessor firstAccessible(File mmFolder, ElementaryModel elementaryModel) {
        for (Content content : elementaryModel.getContent()) {
            EMTypes recognizedType = EMTypes.find(elementaryModel.getType().getName(), content.getFormat(), content.getFormatVersion());
            if (recognizedType != null) {
                IndexedDataAccessor accessor = recognizedType.createAccessor();
                for (ContainerFile contentFile : content.getFiles()) {
                    try {
                        File file = new File(mmFolder, new URL(contentFile.getValue()).getFile());
                        accessor.read(new FileInputStream(file), contentFile.getNamespace()); // TODO: accessor should join multiple sucessively set/added files
                    } catch (MalformedURLException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
                accessor.index();
                return accessor;
            }
        }
        return null;
    }

    public Iterator<LinkedObject<K>> iterator() {
        return groupedElements.iterator();
    }

    public void read(InputStream inputStream) throws IOException {
        readFromFolder(unzip(inputStream));
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

    public void read(URL resource) {
        readFromFolder(new File(resource.getFile()));
    }

    public void addAcessor(String key, IndexedDataAccessor accessor) {
        accessor.index();
        elementaryModels.put(key, accessor);
    }

    enum AccessModes {
        PURE,
        GROUPED
    }

    public enum EMTypes {
        IFC("Object", "ifc", "2x3") {
            IndexedDataAccessor createAccessor() {
                return new EMFIfcAccessor(pm);
            }
        },
        GAEB("BoQ", "gaebxml", "3.1") {
            IndexedDataAccessor createAccessor() {
                return new EMFGaebAccessor();
            }
        },
        QTO("QTO", "xml", "1.0") {
            IndexedDataAccessor createAccessor() {
                return new EMFQtoAccessor();
            }
        },
        ACTIVITY10("Activity", "xml", "1.0") {
            @Override
            IndexedDataAccessor createAccessor() {
                return new EMFSchedule10Accessor();
            }
        },
        ACTIVITY11("Activity", "xml", "1.1") {
            @Override
            IndexedDataAccessor createAccessor() {
                return new EMFSchedule11Accessor();
            }
        },
        RISK("Risk", "xml", "1.0") {
            @Override
            IndexedDataAccessor createAccessor() {
                return new EMFRiskAccessor();
            }
        };

        private String modelType;
        private String format;
        private String formatVersion;
        static PluginManager pm;

        EMTypes(String modelType, String format, String formatVersion) {
            this.modelType = modelType;
            this.format = format;
            this.formatVersion = formatVersion;
        }

        abstract IndexedDataAccessor createAccessor();

        static EMTypes find(String modelType, String format, String formatVersion) {
            for (EMTypes type : EMTypes.values()) {
                if (type.modelType.equals(modelType) && type.format.equals(format) && type.formatVersion.equals(formatVersion)) {
                    return type;
                }
            }
            return null;
        }
    }

}
