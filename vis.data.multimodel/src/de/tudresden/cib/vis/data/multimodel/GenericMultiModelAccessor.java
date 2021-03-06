package de.tudresden.cib.vis.data.multimodel;

import cib.mm.multimodel.*;
import cib.mm.multimodel.util.MultimodelResourceFactoryImpl;
import de.tudresden.cib.vis.data.DataAccessException;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.IndexedDataAccessor;
import de.tudresden.cib.vis.data.bimserver.SimplePluginManager;
import de.tudresden.cib.vis.filter.Condition;
import de.tudresden.cib.vis.filter.ConditionFilter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.*;

public class GenericMultiModelAccessor<K> extends DataAccessor<LinkedObject<K>, Condition<LinkedObject<K>>> {

    private Map<ElementaryModel, IndexedDataAccessor> elementaryModels = new HashMap<ElementaryModel, IndexedDataAccessor>();
    private Collection<LinkedObject<K>> groupedElements = new HashSet<LinkedObject<K>>();
    private ConditionFilter<LinkedObject<K>> filter = new ConditionFilter<LinkedObject<K>>();
    private URL mmUrl = null;

    public GenericMultiModelAccessor(SimplePluginManager pm) {
        EMTypes.pm = pm;
    }

    @Override
    public void read(URL url) throws IOException, DataAccessException {
        this.mmUrl = url;
        MultiModel model = readMultiModelMeta(url);
        LinkModel firstLinkModel = model.getLinkModels().get(0);
        for (ElementaryModel elementaryModel : firstLinkModel.getLinkedModels()) readElementaryModel(elementaryModel, null);
        ElementaryModel firstElementaryModel = model.getElementaryModels().get(0);
        groupBy(firstElementaryModel, firstLinkModel);
    }

    public List<String> read(URL url, LMCondition linkModelCondition, EMCondition keyModelCondition, EMCondition... requiredModelConditions) throws IOException, DataAccessException {
        this.mmUrl = url;
        MultiModel multiModel = readMultiModelMeta(url);
        LinkModel linkModel = getLinkModel(linkModelCondition, multiModel.getLinkModels());
        List<ElementaryModel> models = getElementaryModels(multiModel, keyModelCondition, requiredModelConditions);
        Map<ElementaryModel, String> generatedIds = groupBy(models.get(0), linkModel);
        List<String> ids = new LinkedList<String>();
        for(ElementaryModel model: models){
            ids.add(generatedIds.get(model));
        }
        return ids;
    }

    public List<String> read(URL mmUrl, EMCondition keyModelCondition, EMCondition... requiredModelConditions) throws IOException, DataAccessException {
        return read(mmUrl, new LMCondition.First(), keyModelCondition, requiredModelConditions);
    }

    private MultiModel readMultiModelMeta(URL url) throws IOException {
        MultimodelPackage.eINSTANCE.getClass();
        Resource.Factory resourceFactory = new MultimodelResourceFactoryImpl();
        Resource resource = resourceFactory.createResource(URI.createURI(url.toString()));
        resource.load(null);
        return (MultiModel) resource.getContents().get(0);
    }

    private List<ElementaryModel> getElementaryModels(MultiModel multiModel, EMCondition keyModelCondition, EMCondition[] requiredModelConditions) throws DataAccessException, IOException {
        List<ElementaryModel> foundModels = new LinkedList<ElementaryModel>();
        ElementaryModel keyModelCandidate = findAndReadElementaryModel(multiModel, keyModelCondition);
        foundModels.add(keyModelCandidate);
        for (EMCondition condition : requiredModelConditions) {
            ElementaryModel required = findAndReadElementaryModel(multiModel, condition);
            foundModels.add(required);
        }
        return foundModels;
    }

    private ElementaryModel findAndReadElementaryModel(MultiModel multiModel, EMCondition modelCondition) throws DataAccessException, IOException {
        List<ElementaryModel> modelCandidates = new LinkedList<ElementaryModel>();
        for(ElementaryModel elementaryModel: multiModel.getElementaryModels()){
            if (modelCondition.isValidFor(elementaryModel)) modelCandidates.add(elementaryModel);
        }
        if (modelCandidates.size() > 1) throw new DataAccessException("ambiguous model conditions");
        if (modelCandidates.size() == 0) throw new DataAccessException("no model found");
        readElementaryModel(modelCandidates.get(0), modelCondition);
        return modelCandidates.get(0);
    }

    private URL getElementaryModelUrl(final ElementaryModel elementaryModel) throws IOException, DataAccessException  {
        if (elementaryModel instanceof UriElementaryModel) {
            try {
                java.net.URI absolute = (mmUrl != null && !new java.net.URI(((UriElementaryModel) elementaryModel).getUri()).isAbsolute()) ?
                        mmUrl.toURI().resolve(new java.net.URI(((UriElementaryModel) elementaryModel).getUri())):
                        new java.net.URI(((UriElementaryModel) elementaryModel).getUri());
                return absolute.toURL();
            } catch (URISyntaxException e) {
                throw new DataAccessException("could not parse URI", e);
            }
        } else if (elementaryModel instanceof EmbeddedElementaryModel) {
            final byte[] data = ((EmbeddedElementaryModel) elementaryModel).getData();
            return new URL("test","aadsf",23,"asdf", new URLStreamHandler(){
                @Override
                protected URLConnection openConnection(URL u) throws IOException {
                    return new URLConnection(u) {
                        @Override
                        public void connect() throws IOException {

                        }

                        @Override
                        public InputStream getInputStream() throws IOException {
                            return new ByteArrayInputStream(data);
                        }

                        @Override
                        public int getContentLength() {
                            return data.length;
                        }
                    };
                }
            });
        } else {
            throw new DataAccessException("unknown elementary model type, don't know how to access");
        }
    }

    private void readElementaryModel(ElementaryModel elementaryModel, EMCondition emCondition) throws DataAccessException, IOException {
        if (elementaryModels.containsKey(elementaryModel)) return; // TODO: howto read one EM with multiple accessors?
        String typeCode = getMeta(elementaryModel,"mmaa.model.type");
        EMTypes emType = (emCondition instanceof EMTypeCondition) ?
                ((EMTypeCondition)emCondition).required :
                EMTypes.find(typeCode);
        if (emType == null) throw new DataAccessException("no matching accessor found for " + typeCode);
        else {
            IndexedDataAccessor accessor = emType.createAccessor();
            URL elementaryModelUrl = getElementaryModelUrl(elementaryModel);
            accessor.read(elementaryModelUrl);
            accessor.index();
            elementaryModels.put(elementaryModel, accessor);
        }
    }

    private LinkModel getLinkModel(LMCondition linkModelCondition, EList<LinkModel> linkModels) throws DataAccessException {
        List<LinkModel> linkModelCandidates = new LinkedList<LinkModel>();
        for (LinkModel linkModel : linkModels) {
            if (linkModelCondition.isValidFor(linkModel)) linkModelCandidates.add(linkModel);
        }
        if (linkModelCandidates.size() > 1) throw new DataAccessException("ambiguous link model conditions");
        if (linkModelCandidates.size() == 0) throw new DataAccessException("no matching link model found");
        return linkModelCandidates.get(0);
    }

    private Map<ElementaryModel, String> groupBy(ElementaryModel keyModel, LinkModel linkModel) {
        Map<ElementaryModel, String> generatedModelIds = new HashMap<ElementaryModel, String>();
        for (ElementaryModel elementaryModel : elementaryModels.keySet()) {
            generatedModelIds.put(elementaryModel, Integer.toString(elementaryModel.hashCode()));
        }
        Map<K, LinkedObject<K>> trackMap = new HashMap<K, LinkedObject<K>>();
        for (Link link : linkModel.getLinks()) {
            K key = null;
            LinkedObject.ResolvedLink resolvedLink = new LinkedObject.ResolvedLink();
            for (LinkedElement linkedElement : link.getLinkedElements()) {
                if (elementaryModels.containsKey(linkedElement.getElementaryModel())) {
                    Object object = elementaryModels.get(linkedElement.getElementaryModel()).getIndexed(linkedElement.getElementID());
                    if (linkedElement.getElementaryModel().equals(keyModel))
                        key = (K) object; // TODO make sure accessor matches!
                    resolvedLink.addObject(generatedModelIds.get(linkedElement.getElementaryModel()), object);
                }
            }
            if (!trackMap.containsKey(key)) {
                trackMap.put(key, new LinkedObject<K>(key));
            }
            trackMap.get(key).addLink(resolvedLink);
        }
        for (LinkedObject<K> linkedObject : trackMap.values()) groupedElements.add(linkedObject);
        return generatedModelIds;
    }

    @Override
    public Iterable<? extends LinkedObject<K>> filter(Condition<LinkedObject<K>> condition) {
        return filter.filter(condition, this);
    }

    @Override
    public Condition<LinkedObject<K>> getDefaultCondition() {
        return new Condition<LinkedObject<K>>() {
            @Override
            public boolean matches(LinkedObject<K> data) {
                return true;
            }
        };
    }

    @Override
    public Iterator<LinkedObject<K>> iterator() {
        return groupedElements.iterator();
    }

    public interface EMCondition {
        boolean isValidFor(ElementaryModel model);
    }

    public static class EMTypeCondition implements EMCondition {

        private final EMTypes required;

        public EMTypeCondition(EMTypes required) {
            this.required = required;
        }

        @Override
        public boolean isValidFor(ElementaryModel model) {
            return this.required.typeFormatVersion.equals(getMeta(model,"mmaa.model.type"));
        }
    }

    public static class EMByName implements EMCondition {

        private String name;

        public EMByName(String name) {
            this.name = name;
        }

        @Override
        public boolean isValidFor(ElementaryModel model) {
            return getMeta(model, "mmaa.model.name").contains(name);
        }
    }

    interface LMCondition {
        boolean isValidFor(LinkModel linkModel);

        class First implements LMCondition {
            private boolean firstApplication = true;
            @Override
            public boolean isValidFor(LinkModel linkModel) {
                boolean returnValue= firstApplication;
                firstApplication = false;
                return returnValue;
            }
        }

        class LMByName implements LMCondition {

            private String name;

            public LMByName(String name) {
                this.name = name;
            }

            @Override
            public boolean isValidFor(LinkModel linkModel) {
                return getMeta(linkModel,"mmaa.linkmodel.name").contains(name);
            }
        }
    }

    static class MetaDataMap extends HashMap<String, String> {
        MetaDataMap(EList<MetaDataEntry> metaDataEntryList) {
            for (MetaDataEntry metaDataEntry : metaDataEntryList) {
                this.put(metaDataEntry.getKey(), metaDataEntry.getValue());
            }
        }
    }

    static String getMeta(Annotatable annotatable, String key) {
        for (MetaDataEntry metaDataEntry : annotatable.getMetaDataEntries()) {
            if (metaDataEntry.getKey().equals(key)) {
                return metaDataEntry.getValue();
            }
        }
        return null;
    }
}
