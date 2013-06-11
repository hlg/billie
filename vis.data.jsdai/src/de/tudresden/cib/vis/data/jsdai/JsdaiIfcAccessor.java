package de.tudresden.cib.vis.data.jsdai;

import de.tudresden.bau.cib.exceptions.parser.ParsingException;
import de.tudresden.bau.cib.model.StepDataModel;
import de.tudresden.bau.cib.parser.StepParser;
import de.tudresden.cib.vis.data.DataAccessException;
import de.tudresden.cib.vis.data.IndexedDataAccessor;
import jsdai.lang.EEntity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;

public class JsdaiIfcAccessor extends IndexedDataAccessor<EEntity> {

    StepParser parser = new StepParser(new File(System.getProperty("java.io.tmpdir"),"JSDAIrepo").getAbsolutePath());
    StepDataModel data;

    @Override
    public void index() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public EEntity getIndexed(String objectID) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterator<EEntity> iterator() {
        return Arrays.asList(data.getEntities()).iterator();
    }
}
