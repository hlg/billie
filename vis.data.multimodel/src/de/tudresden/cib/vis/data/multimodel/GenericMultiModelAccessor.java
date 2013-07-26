package de.tudresden.cib.vis.data.multimodel;

import cib.mmaa.multimodel.*;
import cib.mmaa.multimodel.util.MultimodelResourceFactoryImpl;
import de.tudresden.cib.vis.data.DataAccessException;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.filter.Condition;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

public class GenericMultiModelAccessor extends DataAccessor<LinkedObject.ResolvedLink, Condition<LinkedObject.ResolvedLink>> {
    @Override
    public void read(InputStream inputStream, long size) throws IOException, DataAccessException {
        MultimodelPackage.eINSTANCE.getClass();
        Resource.Factory resourceFactory = new MultimodelResourceFactoryImpl();
        Resource resource = resourceFactory.createResource(URI.createURI("inputstream://fake.resource.uri"));
        resource.load(inputStream, null);
        MultiModel model = (MultiModel) resource.getContents().get(0);
        ElementaryModel first = model.getElementaryModels().get(0);
        if(first instanceof UriElementaryModel){
           new URL(((UriElementaryModel) first).getUri()).openStream();
        } else {
            new ByteArrayInputStream(((EmbeddedElementaryModel)first).getData());
        }

    }

    @Override
    public void readFromFolder(File directory) throws DataAccessException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterable<? extends LinkedObject.ResolvedLink> filter(Condition<LinkedObject.ResolvedLink> condition) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Condition<LinkedObject.ResolvedLink> getDefaultCondition() {
        return new Condition<LinkedObject.ResolvedLink>() {
            @Override
            public boolean matches(LinkedObject.ResolvedLink data) {
                return true;
            }
        };
    }

    @Override
    public Iterator<LinkedObject.ResolvedLink> iterator() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
