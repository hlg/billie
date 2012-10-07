package de.tudresden.cib.vis.data.multimodel;

import cib.lib.gaeb.model.gaeb.TgItem;
import cib.mf.qto.model.AnsatzType;
import cib.mf.risk.model.risk.RiskList;
import cib.mf.schedule.model.activity11.Activity;
import de.mefisto.model.container.*;
import de.mefisto.model.linkModel.Link;
import de.mefisto.model.linkModel.LinkModel;
import de.mefisto.model.linkModel.LinkObject;
import de.mefisto.model.parser.ContainerModelParser;
import de.mefisto.model.parser.LinkModelParser;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.IndexedDataAccessor;
import de.tudresden.cib.vis.data.bimserver.EMFIfcGeometricAccessor;
import de.tudresden.cib.vis.data.bimserver.EMFIfcHierarchicAcessor;
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser;
import org.bimserver.plugins.PluginManager;
import org.eclipse.emf.common.util.EList;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
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

    public void readFromFolder(File folder) {
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

    public void readFromFolder(File folder, EMTypes keyModel, EMTypes... requiredModels) throws MalformedURLException {
        File mmFile = new File(folder, "MultiModel.xml");
        assert folder.exists() && mmFile.exists();
        Container container = ContainerModelParser.readContainerModel(mmFile).getContainer();
        EList<ElementaryModel> foundModels = container.getElementaryModelGroup().getElementaryModels();
        for(EMTypes required: requiredModels){
            IndexedDataAccessor accessor = required.createAccessor();
            String modelId = findAndReadModel(folder, foundModels, required, accessor);
            if(modelId==null) throw new RuntimeException(String.format("missing required model: type=%s, format=%s, version=%s", required.modelType, required.format, required.formatVersion));
            elementaryModels.put(modelId, accessor);
        }
        IndexedDataAccessor keyModelAccessor = keyModel.createAccessor();
        String keyModelId = findAndReadModel(folder, foundModels, keyModel, keyModelAccessor);
        if(keyModelId==null) throw new RuntimeException(String.format("missing key model: type=%s, format=%s, version=%s", keyModel.modelType, keyModel.format, keyModel.formatVersion));
        elementaryModels.put(keyModelId, keyModelAccessor);
        LinkModel linkModel = readLinkModel(folder, container.getLinkModelDescriptorGroup().getLinkModelDescriptors().get(0));
        groupBy(keyModelId, linkModel);
    }

    private String findAndReadModel(File folder, EList<ElementaryModel> foundModels, EMTypes required, IndexedDataAccessor accessor) {
        for (ElementaryModel model: foundModels){
            if(model.getType().getName().equals(required.modelType)) {
                for(Content alternative: model.getContent()){
                    if(alternative.getFormat().equals(required.format) && alternative.getFormatVersion().equals(required.formatVersion)){
                        readEM(folder, alternative, accessor);
                        return model.getId();
                    }
                }
            }
        }
        return null;
    }

    private LinkModel readLinkModel(File folder, LinkModelDescriptor linkModelDesc) throws MalformedURLException {
        File linkFile = new File(folder, new URL(linkModelDesc.getFile()).getFile());
        return readLinkModel(linkFile);
    }

    private LinkModel readLinkModel(File linkFile) {
        return LinkModelParser.readLinkModel(linkFile).getLinkModel();
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
                readEM(mmFolder, content, accessor);
                return accessor;
            }
        }
        return null;
    }

    private void readEM(File mmFolder, Content content, IndexedDataAccessor accessor) {
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

    public Iterator<LinkedObject<K>> iterator() {
        return groupedElements.iterator();
    }

    public void read(InputStream inputStream, long size) throws IOException {
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

        // elment types must be unique!

        IFC("Object", "ifc", "2x3", EMFIfcParser.EngineEObject.class, true) {
            IndexedDataAccessor createAccessor() {
                return new EMFIfcGeometricAccessor(pm);
            }
        },
        GAEB("BoQ", "gaebxml", "3.1", TgItem.class, true) {
            IndexedDataAccessor createAccessor() {
                return new EMFGaebAccessor();
            }
        },
        QTO("QTO", "xml", "1.0", AnsatzType.class, true) {
            IndexedDataAccessor createAccessor() {
                return new EMFQtoAccessor();
            }
        },
        ACTIVITY10("Activity", "xml", "1.0", cib.mf.schedule.model.activity10.Activity.class, true) {
            @Override
            IndexedDataAccessor createAccessor() {
                return new EMFSchedule10Accessor();
            }
        },
        ACTIVITY11("Activity", "xml", "1.1", Activity.class, true) {
            @Override
            IndexedDataAccessor createAccessor() {
                return new EMFSchedule11Accessor();
            }
        },
        RISK("Risk", "xml", "1.0", RiskList.class, true) {
            @Override
            IndexedDataAccessor createAccessor() {
                return new EMFRiskAccessor();
            }
        },
        IFCHIERARCHIC("Object", "ifc", "2x3", EMFIfcHierarchicAcessor.HierarchicIfc.class, false) {
            @Override
            IndexedDataAccessor createAccessor() {
                return new EMFIfcHierarchicAcessor(pm);
            }
        },
        GAEBHIERARCHIC("BoQ", "gaebxml", "3.1", HierarchicGaebAccessor.HierarchicTgItemBoQCtgy.class, false) {
            @Override
            IndexedDataAccessor createAccessor() {
                return new HierarchicGaebAccessor();
            }
        };

        private String modelType;
        private String format;
        private String formatVersion;
        static PluginManager pm;
        private Class allowedType;
        private boolean preferred;

        EMTypes(String modelType, String format, String formatVersion, Class allowedType, boolean preferred) {
            this.modelType = modelType;
            this.format = format;
            this.formatVersion = formatVersion;
            this.allowedType = allowedType;
            this.preferred = preferred;
        }

        abstract IndexedDataAccessor createAccessor();

        public static EMTypes find(String modelType, String format, String formatVersion) {
            for (EMTypes type : EMTypes.values()) {
                if (type.modelType.equals(modelType) && type.format.equals(format) && type.formatVersion.equals(formatVersion) && type.preferred) {
                    return type;
                }
            }
            for (EMTypes type : EMTypes.values()) {
                if (type.modelType.equals(modelType) && type.format.equals(format) && type.formatVersion.equals(formatVersion)) {
                    return type;
                }
            }
            return null;
        }

        public static EMTypes find(Object element){
            for (EMTypes type: EMTypes.values()){
                if(type.getAllowedType().isInstance(element)) return type;
            }
            return null;
        }

        public Class getAllowedType(){
            return allowedType;
        }
    }

}
