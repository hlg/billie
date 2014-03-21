package de.tudresden.cib.vis.configurations;

import de.tudresden.cib.vis.data.bimserver.EMFIfcParser;
import de.tudresden.cib.vis.data.multimodel.LinkedObject;
import de.tudresden.cib.vis.filter.Condition;
import de.tudresden.cib.vis.mapping.Configuration;
import de.tudresden.cib.vis.mapping.Mapper;

import java.util.List;

public class Mmaa_Progress_Colored4D<S> extends Configuration<LinkedObject<EMFIfcParser.EngineEObject>, Condition<LinkedObject<EMFIfcParser.EngineEObject>>, S> {

    private final String scheduleId;
    private final String reportId;

    public Mmaa_Progress_Colored4D(Mapper<LinkedObject<EMFIfcParser.EngineEObject>, Condition<LinkedObject<EMFIfcParser.EngineEObject>>, ?, S> mapper, List<String> ids) {
        super(mapper);
        this.scheduleId = ids.get(1);
        this.reportId = ids.get(2);
    }

    @Override
    public void config() {

    }
}
