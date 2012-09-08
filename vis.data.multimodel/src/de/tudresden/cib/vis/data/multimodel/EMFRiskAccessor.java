package de.tudresden.cib.vis.data.multimodel;

import cib.mf.risk.model.risk.RiskList;
import cib.mf.risk.model.risk.RiskPackage;
import cib.mf.risk.model.risk.Root;
import cib.mf.risk.model.risk.util.RiskResourceFactoryImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

import java.util.HashMap;
import java.util.Map;

public class EMFRiskAccessor extends EMFGenericAccessor<RiskList> {
    @Override
    protected Resource createResource(URI uri) {
        RiskPackage.eINSTANCE.eClass();
        RiskResourceFactoryImpl riskResourceFactory = new RiskResourceFactoryImpl();
        return riskResourceFactory.createResource(uri);
    }

    @Override
    protected Map<String, RiskList> collectLookUp() {
        Map<String, RiskList>  lookup = new HashMap<String, RiskList>();
        Root root = (Root) data;
        for(RiskList risk: root.getRiskList()){
            lookup.put(risk.getID(), risk);
        }
        return lookup;
    }
}
