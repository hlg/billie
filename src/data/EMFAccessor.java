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

public class EMFAccessor extends DataAccessor<EObject> {

    EObject data;

    public EMFAccessor(URL url) throws IOException {
        URI testGaeb = URI.createFileURI(url.getPath());
        GaebPackage.eINSTANCE.eClass();
        GaebResourceFactoryImpl gaebResourceFactory = new GaebResourceFactoryImpl();
        Resource resource = gaebResourceFactory.createResource(testGaeb);
        resource.load(null);
        data = resource.getContents().get(0);
    }

    public EMFAccessor(InputStream inputStream) throws IOException {
        GaebPackage.eINSTANCE.eClass();
        GaebResourceFactoryImpl gaebResourceFactory = new GaebResourceFactoryImpl();
        Resource resource = gaebResourceFactory.createResource(URI.createURI("inputstream://fake.resource.uri"));
        resource.load(inputStream, null);
        data = resource.getContents().get(0);
    }

    public Iterator<EObject> iterator() {
        return data.eAllContents();
    }
}
