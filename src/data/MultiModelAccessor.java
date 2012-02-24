package data;

import de.mefisto.model.container.*;
import de.mefisto.model.linkModel.Link;
import de.mefisto.model.linkModel.LinkModel;
import de.mefisto.model.linkModel.LinkObject;
import de.mefisto.model.parser.ContainerModelParser;
import de.mefisto.model.parser.LinkModelParser;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class MultiModelAccessor implements DataAccessor<Link> {

    public MultiModelAccessor() {
        File containerFile = new File(this.getClass().getResource("/carport.zip").getFile());
        // TODO unzip
        File mmFolder = new File(this.getClass().getResource("/carport").getFile());
        File mmFile = new File(mmFolder, "MultiModel.xml");
        assert mmFolder.exists() && mmFile.exists();
        Container container = ContainerModelParser.readContainerModel(mmFile).getContainer();
        Map<String, Collection<DataAccessor>> elementaryModels = new HashMap<String, Collection<DataAccessor>>();
        for (ElementaryModel elementaryModel : container.getElementaryModelGroup().getElementaryModels()) {
            Collection<DataAccessor> modelAccessors = new ArrayList<DataAccessor>();
            elementaryModels.put(elementaryModel.getId(), modelAccessors);
            for (Content content : elementaryModel.getContent()) {
                for (ContainerFile contentFile : content.getFiles()) {
                    try {
                        File file = new File(mmFolder, new URL(contentFile.getValue()).getFile());
                        EMTypes recognizedType = EMTypes.find(elementaryModel.getType().getName(), content.getFormat());
                        if (recognizedType != null) {
                            DataAccessor accessor = recognizedType.createAccessor();
                            accessor.setInput(file);
                            modelAccessors.add(accessor);
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
        }
        for (LinkModelDescriptor linkModelDesc : container.getLinkModelDescriptorGroup().getLinkModelDescriptors()) {
            try {
                File linkFile = new File(mmFile.getParent(), new URL(linkModelDesc.getFile()).getFile());
                LinkModel linkModel = LinkModelParser.readLinkModel(linkFile).getLinkModel();
                for (LinkObject link : linkModel.getLinkObjects()) {
                    for (Link linkedElement : link.getLinks()) {
                        linkedElement.getModelID();
                        linkedElement.getObjectID();
                        linkedElement.getContentID(); // WTF?
                        linkedElement.getI(); // WTF ???
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            for (ElementaryModelReference ref : linkModelDesc.getModels().getElementaryModelReferences()) {

            }
        }


    }

    public Iterator iterator() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setInput(File file) {

    }

    enum EMTypes {
        IFC("Object", "ifc") {
            DataAccessor createAccessor() {
                return new EMFIfcAccessor();
            }
        },
        GAEB("BoQ", "gaebxml") {
            DataAccessor createAccessor() {
                return new EMFGaebAccessor();
            }
        };

        private String modelType;
        private String format;

        EMTypes(String modelType, String format) {
            this.modelType = modelType;
            this.format = format;
        }

        abstract DataAccessor createAccessor();

        static EMTypes find(String modelType, String format) {
            for (EMTypes type : EMTypes.values()) {
                if (type.modelType.equals(modelType) && type.format.equals(format)) {
                    return type;
                }
            }
            return null;
        }
    }
}
