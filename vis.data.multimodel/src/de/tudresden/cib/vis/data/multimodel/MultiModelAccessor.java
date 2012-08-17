package de.tudresden.cib.vis.data.multimodel;

import cib.lib.gaeb.model.gaeb.TgItem;
import cib.mf.qto.model.AnsatzType;
import cib.mf.schedule.model.activity.Activity;
import de.mefisto.model.container.*;
import de.mefisto.model.linkModel.Link;
import de.mefisto.model.linkModel.LinkModel;
import de.mefisto.model.linkModel.LinkObject;
import de.mefisto.model.parser.ContainerModelParser;
import de.mefisto.model.parser.LinkModelParser;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.IndexedDataAccessor;
import de.tudresden.cib.vis.data.bimserver.EMFIfcAccessor;
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MultiModelAccessor<K> extends DataAccessor<MultiModelAccessor.LinkedObject<K>> {

    private Map<String, IndexedDataAccessor> elementaryModels = new HashMap<String, IndexedDataAccessor>();
    private Collection<LinkedObject<K>> groupedElements;

    private File mmFolder;

    public MultiModelAccessor(URL resource) {
        // TODO unzip, move to setInput
        this(new File(resource.getFile()));
    }

    public MultiModelAccessor(File folder){
        mmFolder = folder;
        File mmFile = new File(mmFolder, "MultiModel.xml");
        assert mmFolder.exists() && mmFile.exists();
        Container container = ContainerModelParser.readContainerModel(mmFile).getContainer();
        ElementaryModelType keyModelType = ElementaryModelType.OBJECT;
        String firstAccesibleKeyModelId = null;
        for (ElementaryModel elementaryModel : container.getElementaryModelGroup().getElementaryModels()) {
            IndexedDataAccessor accessor = firstAccessible(mmFolder, elementaryModel);
            if (accessor != null) {
                elementaryModels.put(elementaryModel.getId(), accessor);
                if (elementaryModel.getType().equals(keyModelType))
                    firstAccesibleKeyModelId = elementaryModel.getId();
            }
        }
        LinkModelDescriptor linkModelDesc = container.getLinkModelDescriptorGroup().getLinkModelDescriptors().get(0);
        if (firstAccesibleKeyModelId != null) groupBy(firstAccesibleKeyModelId, linkModelDesc);
    }

    private void groupBy(String groupingModelId, LinkModelDescriptor linkModelDesc) {
        Map<K, LinkedObject<K>> trackMap = new HashMap<K, LinkedObject<K>>();
        try {
            File linkFile = new File(mmFolder, new URL(linkModelDesc.getFile()).getFile());
            LinkModel linkModel = LinkModelParser.readLinkModel(linkFile).getLinkModel();
            for (LinkObject link : linkModel.getLinkObjects()) {
                K keyObject = resolveKey(link, groupingModelId);
                LinkedObject<K> relations;
                if (trackMap.containsKey(keyObject)) {
                    relations = trackMap.get(keyObject);
                } else {
                    relations = new LinkedObject<K>(keyObject);
                    trackMap.put(keyObject, relations);
                }
                relations.addLink(resolveLink(link, groupingModelId));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        groupedElements = trackMap.values();
    }

    private ResolvedLink resolveLink(LinkObject link, String groupingModelId) {
        ResolvedLink resolved = new ResolvedLink();
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

    private <I> void resolveAndAddObject(IndexedDataAccessor<I> dataAccessor, Link linkedElement, ResolvedLink resolved) {
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
            EMTypes recognizedType = EMTypes.find(elementaryModel.getType().getName(), content.getFormat());
            if (recognizedType != null) {
                IndexedDataAccessor accessor = recognizedType.createAccessor();
                for (ContainerFile contentFile : content.getFiles()) {
                    try {
                        File file = new File(mmFolder, new URL(contentFile.getValue()).getFile());
                        accessor.setInput(new FileInputStream(file), contentFile.getNamespace()); // TODO: accessor should join multiple sucessively set/added files
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

    public void setInput(InputStream inputStream) {
        // TODO: read from zip
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


    enum AccessModes {
        PURE,
        GROUPED
    }

    public enum EMTypes {
        IFC("Object", "ifc") {
            IndexedDataAccessor createAccessor() {
                return new EMFIfcAccessor();
            }
        },
        GAEB("BoQ", "gaebxml") {
            IndexedDataAccessor createAccessor() {
                return new EMFGaebAccessor();
            }
        },
        QTO("QTO", "xml") {
            IndexedDataAccessor createAccessor() {
                return new EMFQtoAccessor();
            }
        },
        ACTIVITY("Activity", "xml") {
            @Override
            IndexedDataAccessor createAccessor() {
                return new EMFScheduleAccessor();
            }
        };

        private String modelType;
        private String format;

        EMTypes(String modelType, String format) {
            this.modelType = modelType;
            this.format = format;
        }

        abstract IndexedDataAccessor createAccessor();

        static EMTypes find(String modelType, String format) {
            for (EMTypes type : EMTypes.values()) {
                if (type.modelType.equals(modelType) && type.format.equals(format)) {
                    return type;
                }
            }
            return null;
        }
    }

    public static class LinkedObject<T> {
        T keyObject;
        Collection<ResolvedLink> links = new HashSet<ResolvedLink>();

        public LinkedObject(T keyObject) {
            this.keyObject = keyObject;
        }

        public Collection<ResolvedLink> getResolvedLinks() {
            return links;
        }

        public T getKeyObject() {
            return keyObject;
        }

        public void addLink(ResolvedLink link) {
            links.add(link);
        }
    }

    public static class ResolvedLink {
        private Map<String, EMFIfcParser.EngineEObject> ifcObjects = new HashMap<String, EMFIfcParser.EngineEObject>();
        private Map<String, TgItem> gaebObjects = new HashMap<String, TgItem>();
        private Map<String, AnsatzType> qtoObjects = new HashMap<String, AnsatzType>();
        private Map<String, Activity> scheduleObjects = new HashMap<String, Activity>();

        public Map<String, AnsatzType> getLinkedQto() {
            return qtoObjects;
        }

        public Map<String, TgItem> getLinkedBoQ() {
            return gaebObjects;
        }

        public Map<String, EMFIfcParser.EngineEObject> getLinkedObject() {
            return ifcObjects;
        }

        public Map<String, Activity> getScheduleObjects() {
            return scheduleObjects;
        }

        public Map<String, ?> getLinksOfType(ElementaryModelType elementaryModelType) {
            if (elementaryModelType.equals(ElementaryModelType.BO_Q)) return gaebObjects;
            if (elementaryModelType.equals(ElementaryModelType.OBJECT)) return ifcObjects;
            if (elementaryModelType.equals(ElementaryModelType.QTO)) return qtoObjects;
            if (elementaryModelType.equals(ElementaryModelType.ACTIVITY)) return scheduleObjects;
            return null;
        }

        public void addObject(String modelId, Object object) {
            if (object instanceof TgItem) gaebObjects.put(modelId, (TgItem) object);
            if (object instanceof EMFIfcParser.EngineEObject)
                ifcObjects.put(modelId, (EMFIfcParser.EngineEObject) object);
            if (object instanceof AnsatzType) qtoObjects.put(modelId, (AnsatzType) object);
            if (object instanceof Activity) scheduleObjects.put(modelId, (Activity) object);
        }

    }
}
