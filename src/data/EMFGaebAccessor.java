package data;

import cib.lib.gaeb.model.gaeb.GaebPackage;
import cib.lib.gaeb.model.gaeb.util.GaebResourceFactoryImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

public class EMFGaebAccessor extends DataAccessor<EObject> {

    EObject data;

    public EMFGaebAccessor(URL url) throws IOException {
        URI fileUri = URI.createFileURI(url.getPath());
        Resource resource = createResource(fileUri);
        resource.load(null);
        data = resource.getContents().get(0);
    }

    public EMFGaebAccessor(InputStream inputStream) throws IOException {
        URI fakeUri = URI.createURI("inputstream://fake.resource.uri");
        Resource resource = createResource(fakeUri);
        resource.load(inputStream, null);
        data = resource.getContents().get(0);
    }

    private Resource createResource(URI uri) {
        GaebPackage.eINSTANCE.eClass();
        GaebResourceFactoryImpl gaebResourceFactory = new GaebResourceFactoryImpl();
        return gaebResourceFactory.createResource(uri);
    }

    public Iterator<EObject> iterator() {
        return data.eAllContents();
    }
}
