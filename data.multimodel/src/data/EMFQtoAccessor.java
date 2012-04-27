package data;

import cib.mmaa.qto.elementaryModel.Qto.*;
import cib.mmaa.qto.elementaryModel.Qto.util.QtoResourceFactoryImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

import java.util.HashMap;
import java.util.Map;

public class EMFQtoAccessor extends EMFGenericAccessor<AnsatzType> {

    @Override
    protected Resource createResource(URI uri) {
        QtoPackage.eINSTANCE.eClass();
        QtoResourceFactoryImpl qtoResourceFactory = new QtoResourceFactoryImpl();
        return qtoResourceFactory.createResource(uri);
    }

    @Override
    protected Map<String, AnsatzType> collectLookUp() {
        Map<String, AnsatzType> result = new HashMap<String, AnsatzType>();
        AufmassType qto = ((DocumentRoot) data).getAufmass();
        for (AnsatzType ansatz : qto.getAnsaetze().getAnsatz()) {
            EList<ZeileType> zeilen = ansatz.getAnsatzZeilen().getZeile();
            assert zeilen.size() == 1;
            result.put(namespace + zeilen.get(0).getAdresse(), ansatz);
        }
        return result;
    }
}
