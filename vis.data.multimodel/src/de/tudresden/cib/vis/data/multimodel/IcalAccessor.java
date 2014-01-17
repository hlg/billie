package de.tudresden.cib.vis.data.multimodel;

import de.tudresden.cib.vis.data.DataAccessException;
import de.tudresden.cib.vis.data.IndexedDataAccessor;
import de.tudresden.cib.vis.filter.Condition;
import de.tudresden.cib.vis.filter.ConditionFilter;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class IcalAccessor extends IndexedDataAccessor<VEvent, Condition<VEvent>> {

    private Calendar data;
    private Map<String, VEvent> index = new HashMap<String, VEvent>();
    private ConditionFilter<VEvent> filter = new ConditionFilter<VEvent>();

    @Override
    public void index() throws DataAccessException {
        for(Object component: data.getComponents()){
            if(component instanceof VEvent) {
                VEvent event = (VEvent) component;
                index.put(event.getUid().getValue(), event);
            }
        }
    }

    @Override
    public VEvent getIndexed(String objectID) {
        return index.get(objectID);
    }

    @Override
    public void read(InputStream inputStream, long size) throws IOException, DataAccessException {
        try {
            data = new CalendarBuilder().build(inputStream);
        } catch (ParserException e) {
            throw new DataAccessException("error during ical parsing", e);
        }
        index();
    }

    @Override
    public void readFromFolder(File directory) throws DataAccessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterable<? extends VEvent> filter(Condition<VEvent> condition) {
        return filter.filter(condition, this);
    }

    @Override
    public Condition<VEvent> getDefaultCondition() {
        return new Condition<VEvent>() {
            @Override
            public boolean matches(VEvent data) {
                return true;
            }
        };
    }

    @Override
    public Iterator<VEvent> iterator() {
        return index.values().iterator();
    }
}
