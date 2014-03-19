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

    public void read(URL url, LMCondition linkModelCondition, EMCondition keyModelCondition, EMCondition... requiredModelConditions) throws IOException, DataAccessException {
        this.mmUrl = url;
        MultiModel multiModel = readMultiModelMeta(url);
        LinkModel linkModel = getLinkModel(linkModelCondition, multiModel.getLinkModels());
        ElementaryModel keyModel = getElementaryModels(multiModel, keyModelCondition, requiredModelConditions);
        groupBy(keyModel, linkModel);
    }

    public void read(URL mmUrl, EMCondition keyModelCondition, EMCondition... requiredModelConditions) throws IOException, DataAccessException {
        this.mmUrl = mmUrl;
        MultiModel multiModel = readMultiModelMeta(mmUrl);
        LinkModel firstLinkModel = multiModel.getLinkModels().get(0);
        ElementaryModel keyModel = getElementaryModels(multiModel, keyModelCondition, requiredModelConditions);
        groupBy(keyModel, firstLinkModel);
    }

    private MultiModel readMultiModelMeta(URL url) throws IOException {
        MultimodelPackage.eINSTANCE.getClass();
        Resource.Factory resourceFactory = new MultimodelResourceFactoryImpl();
        Resource resource = resourceFactory.createResource(URI.createURI(url.toString()));
        resource.load(null);
        return (MultiModel) resource.getContents().get(0);
    }

    private ElementaryModel getElementaryModels(MultiModel multiModel, EMCondition keyModelCondition, EMCondition[] requiredModelConditions) throws DataAccessException, IOException {
        List<ElementaryModel> keyModelCandidates = new LinkedList<ElementaryModel>();
        for (ElementaryModel elementaryModel : multiModel.getElementaryModels()) {
            if (keyModelCondition.isValidFor(elementaryModel)) keyModelCandidates.add(elementaryModel);
            for (EMCondition condition : requiredModelConditions) {
                if (condition.isValidFor(elementaryModel)) {
                    readElementaryModel(elementaryModel, condition);
                }
            }
        }
        if (keyModelCandidates.size() > 1) throw new DataAccessException("ambiguous key model conditions");
        if (keyModelCandidates.size() == 0) throw new DataAccessException("no key model found");
        readElementaryModel(keyModelCandidates.get(0), keyModelCondition);
        return keyModelCandidates.get(0);
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

    private void groupBy(ElementaryModel keyModel, LinkModel linkModel) {
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

    interface LMCondition {
        boolean isValidFor(LinkModel linkModel);

        class First implements LMCondition {
            @Override
            public boolean isValidFor(LinkModel linkModel) {
                return true;
            }
        }

        class ByName implements LMCondition {

            private String name;

            public ByName(String name) {
                this.name = name;
            }

            @Override
            public boolean isValidFor(LinkModel linkModel) {
                return name.equals(getMeta(linkModel,"mmaa.linkmodel.name"));
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
