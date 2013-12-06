package de.tudresden.cib.vis.data.multimodel;

import cib.lib.gaeb.model.gaeb.TgItem;
import cib.lib.gaeb.model.gaeb.TgQtySplit;
import cib.mf.qto.model.AnsatzType;
import cib.mf.risk.model.risk.RiskList;
import cib.mf.schedule.model.activity11.Activity;
import de.tudresden.cib.vis.data.DataAccessException;
import de.tudresden.cib.vis.data.IndexedDataAccessor;
import de.tudresden.cib.vis.data.bimserver.EMFIfcGeometricAccessor;
import de.tudresden.cib.vis.data.bimserver.EMFIfcHierarchicAcessor;
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser;
import org.bimserver.plugins.PluginManager;

public enum EMTypes {

    // elment types must be unique!

    IFC("Object", "ifc", "2x3", EMFIfcParser.EngineEObject.class, true, "bim-ifc-2x3") {
        IndexedDataAccessor createAccessor() throws DataAccessException {
            return new EMFIfcGeometricAccessor(pm, true);
        }
    },
    GAEB("BoQ", "gaebxml", "3.1", TgItem.class, true, "gaeb-da81-83-v2009") {
        IndexedDataAccessor createAccessor() {
            return new EMFGaebAccessor();
        }
    },
    GAEBSPLIT("BoQ","gaebxml","3.1", TgQtySplit.class,true, "gaeb-da81-83-v2009") {
        @Override
        IndexedDataAccessor createAccessor() throws DataAccessException {
            return new EMFGaebSplitAccessor();
        }
    },
    QTO("QTO", "xml", "1.0", AnsatzType.class, true, "mefisto-qto-1.0") {
        IndexedDataAccessor createAccessor() {
            return new EMFQtoAccessor();
        }
    },
    ACTIVITY10("Activity", "xml", "1.0", cib.mf.schedule.model.activity10.Activity.class, true, "mefisto-schedule-1.0") {
        @Override
        IndexedDataAccessor createAccessor() {
            return new EMFSchedule10Accessor();
        }
    },
    ACTIVITY11("Activity", "xml", "1.1", Activity.class, true, "mefisto-schedule-1.1") {
        @Override
        IndexedDataAccessor createAccessor() {
            return new EMFSchedule11Accessor();
        }
    },
    RISK("Risk", "xml", "1.0", RiskList.class, true, "risk-1.0") {
        @Override
        IndexedDataAccessor createAccessor() {
            return new EMFRiskAccessor();
        }
    },
    IFCHIERARCHIC("Object", "ifc", "2x3", EMFIfcHierarchicAcessor.HierarchicIfc.class, false, "bim-ifc-2x3") {
        @Override
        IndexedDataAccessor createAccessor() throws DataAccessException {
            return new EMFIfcHierarchicAcessor(pm);
        }
    },
    GAEBHIERARCHIC("BoQ", "gaebxml", "3.1", HierarchicGaebAccessor.HierarchicTgItemBoQCtgy.class, false, "gaeb-da81-83-v2009") {
        @Override
        IndexedDataAccessor createAccessor() {
            return new HierarchicGaebAccessor();
        }
    };

    protected String modelType;
    protected String format;
    String formatVersion;
    protected final String typeFormatVersion; // modelType, format, formatversion from mf-container combined for generic mmaa
    static PluginManager pm;
    private Class allowedType;
    private boolean preferred;

    EMTypes(String modelType, String format, String formatVersion, Class allowedType, boolean preferred, String typeFormatVersion) {
        this.modelType = modelType;
        this.format = format;
        this.formatVersion = formatVersion;
        this.allowedType = allowedType;
        this.preferred = preferred;
        this.typeFormatVersion = typeFormatVersion;
    }

    abstract IndexedDataAccessor createAccessor() throws DataAccessException;

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

    public static EMTypes find(String typeCode){
        for  (EMTypes type:EMTypes.values()){
            if(type.typeFormatVersion.equals(typeCode)) return type;
        }
        return null; // TODO: exceptions!
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

