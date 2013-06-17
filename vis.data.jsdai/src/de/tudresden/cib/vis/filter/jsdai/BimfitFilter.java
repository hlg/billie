package de.tudresden.cib.vis.filter.jsdai;

import de.tudresden.bau.cib.model.StepDataModel;
import de.tudresden.cib.vis.filter.Filter;
import jsdai.lang.EEntity;

import java.util.Arrays;
import java.util.Iterator;

public class BimfitFilter implements Filter.ModelEntity<BimfitFilter.BimfitCondition, StepDataModel,EEntity> {
    @Override
    public Iterator<EEntity> filter(BimfitCondition condition, StepDataModel toBeFiltered) {
        return Arrays.asList(condition.filter(toBeFiltered)).iterator();
    }

    public interface BimfitCondition {
        EEntity[] filter(StepDataModel model);
    }
}
