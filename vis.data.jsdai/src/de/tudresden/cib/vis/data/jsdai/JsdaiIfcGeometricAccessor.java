package de.tudresden.cib.vis.data.jsdai;

import de.tudresden.bau.cib.exceptions.parser.ParsingException;
import de.tudresden.bau.cib.model.StepDataModel;
import de.tudresden.bau.cib.parser.StepParser;
import de.tudresden.cib.vis.data.DataAccessException;
import de.tudresden.cib.vis.data.IndexedDataAccessor;
import de.tudresden.cib.vis.filter.Condition;
import de.tudresden.cib.vis.filter.ConditionFilter;
import jsdai.SIfc2x3.EIfcproduct;
import jsdai.lang.EEntity;
import jsdai.lang.SdaiException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsdaiIfcGeometricAccessor extends IndexedDataAccessor<JsdaiIfcGeometricAccessor.GeomtricIfc, Condition<JsdaiIfcGeometricAccessor.GeomtricIfc>>{

    Map<String, GeomtricIfc> wrapped = new HashMap<String, GeomtricIfc>();
    StepParser parser = new StepParser(new File(System.getProperty("java.io.tmpdir"),"JSDAIrepo").getAbsolutePath());
    private ConditionFilter<GeomtricIfc> filter = new ConditionFilter<GeomtricIfc>();

    @Override
    public void index() throws DataAccessException {
        // indexing is done while reading and wrapping
    }

    @Override
    public GeomtricIfc getIndexed(String objectID) {
        return wrapped.get(objectID);
    }

    @Override
    public void read(InputStream inputStream, long size) throws IOException, DataAccessException {
        try {
            wrapped.clear();
            StepDataModel data = parser.loadStream(inputStream);
            for(EIfcproduct product: data.getEntitiesOf(EIfcproduct.class)){
                wrapped.put(product.getGlobalid(null), new GeomtricIfc(product));
            }

        } catch (ParsingException e) {
            throw new DataAccessException(e);
        } catch (SdaiException e) {
            throw new DataAccessException(e);
        }

    }

    @Override
    public void readFromFolder(File directory) throws DataAccessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterable<? extends GeomtricIfc> filter(Condition<GeomtricIfc> condition) {
        return filter.filter(condition, this);
    }

    @Override
    public Condition<GeomtricIfc> getDefaultCondition() {
        return new Condition<GeomtricIfc>() {
            @Override
            public boolean matches(GeomtricIfc data) {
                return true;
            }
        } ;
    }

    @Override
    public Iterator<GeomtricIfc> iterator() {
        return wrapped.values().iterator();
    }

    class GeomtricIfc {
        EEntity oject;

        public GeomtricIfc(EIfcproduct product) {
            oject =  product;
            // TODO -> fetch geometry
        }
    }
}

