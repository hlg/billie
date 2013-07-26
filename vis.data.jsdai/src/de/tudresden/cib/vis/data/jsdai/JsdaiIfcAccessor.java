package de.tudresden.cib.vis.data.jsdai;

import de.tudresden.bau.cib.exceptions.parser.ParsingException;
import de.tudresden.bau.cib.model.StepDataModel;
import de.tudresden.bau.cib.parser.StepParser;
import de.tudresden.cib.vis.data.DataAccessException;
import de.tudresden.cib.vis.data.IndexedDataAccessor;
import de.tudresden.cib.vis.filter.jsdai.BimfitFilter;
import jsdai.SIfc2x3.EIfcroot;
import jsdai.lang.EEntity;
import jsdai.lang.SdaiException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsdaiIfcAccessor extends IndexedDataAccessor<EEntity, BimfitFilter.BimfitCondition> {

    private final BimfitFilter filter;
    StepParser parser = new StepParser(new File(System.getProperty("java.io.tmpdir"),"JSDAIrepo").getAbsolutePath());
    StepDataModel data;
    Map<String, EEntity> index = new HashMap<String, EEntity>();

    @Override
    public Iterable<? extends EEntity> filter(BimfitFilter.BimfitCondition condition) {
        return filter.filter(condition, data);
    }

    @Override
    public BimfitFilter.BimfitCondition getDefaultCondition() {
        return new BimfitFilter.BimfitCondition() {
            @Override
            public EEntity[] filter(StepDataModel model) {
                return model.getEntities();
            }
        };
    }

    public JsdaiIfcAccessor() {
        filter = new BimfitFilter();
    }

    @Override
    public void index() throws DataAccessException {
        index.clear();
        try {
            for(EIfcroot root : data.getEntitiesOf(EIfcroot.class)){
                index.put(root.getGlobalid(null), root);
            }
        } catch (SdaiException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public EEntity getIndexed(String objectID) {
        return index.get(objectID);
    }

    @Override
    public void read(InputStream inputStream, long size) throws IOException, DataAccessException {
        try {
            data = parser.loadStream(inputStream);
        } catch (ParsingException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public void readFromFolder(File directory) throws DataAccessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<EEntity> iterator() {
        return Arrays.asList(data.getEntities()).iterator();
    }

    protected void dispose() throws SdaiException {
        parser.stop();
    }
}
