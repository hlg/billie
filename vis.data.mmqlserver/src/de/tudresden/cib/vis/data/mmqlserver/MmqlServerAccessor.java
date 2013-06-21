package de.tudresden.cib.vis.data.mmqlserver;

import cib.mmaa.language.mmql.transfer.MMQLResultSet;
import de.tudresden.cib.vis.data.DataAccessException;
import de.tudresden.cib.vis.data.DataAccessor;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MmqlServerAccessor extends DataAccessor<MmqlServerAccessor.MMQLRow, String> {

    MMQLResultSet data;

    public void read(String query) throws IOException, DataAccessException {
        Socket s = new Socket("localhost", 29919);
        s.setSoTimeout(5000);
        ObjectOutputStream socketOut = new ObjectOutputStream(s.getOutputStream());
        socketOut.writeObject(query);
        socketOut.flush();
        ObjectInputStream socketIn = new ObjectInputStream(s.getInputStream());
        try {
            data = (MMQLResultSet) socketIn.readObject();
        } catch (ClassNotFoundException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public void read(InputStream inputStream, long size) throws IOException, DataAccessException {
        read(IOUtils.toString(inputStream, "UTF-8"));
    }

    @Override
    public void readFromFolder(File directory) throws DataAccessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterable<? extends MMQLRow> filter(String condition) {
        // TODO: make read only connect and filter by query?
        return null;
    }

    @Override
    public Iterator<MMQLRow> iterator() {
        return new Iterator<MMQLRow>() {
            int i=0;

            @Override
            public boolean hasNext() {
                return i<data.getNumRows()-1;
            }

            @Override
            public MMQLRow next() {
                i++;
                return new MMQLRow(i, data);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

        };
    }

    public static class MMQLRow {
        private int row;
        private MMQLResultSet data;
        private Map<String, Integer> headers = new HashMap<String, Integer>();

        public MMQLRow(int row, MMQLResultSet resultSet) {
            this.row = row;
            this.data = resultSet;
            for(int i=0; i<data.getNumColums(); i++) headers.put(data.getHeader(i),i);
        }
        public String getCell(int column){
            return data.getCell(row, column);
        }

        public String getCell(String column){
            return data.getCell(row, headers.get(column));
        }

    }
}
