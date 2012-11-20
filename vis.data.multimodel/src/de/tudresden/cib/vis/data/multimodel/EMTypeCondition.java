package de.tudresden.cib.vis.data.multimodel;

import de.mefisto.model.container.Content;
import de.mefisto.model.container.ElementaryModel;

public class EMTypeCondition implements BaseMultiModelAccessor.EMCondition {
    EMTypes required;

    public EMTypeCondition(EMTypes required) {
        this.required = required;
    }

    @Override
    public boolean isValidFor(ElementaryModel model) {
        return model.getType().getName().equals(required.modelType);
    }
    @Override
    public boolean isValidFor(Content alternative) {
        return alternative.getFormat().equals(required.format) && alternative.getFormatVersion().equals(required.formatVersion);
    }

    public String getErrorMessage() {
        return String.format("missing model: type=%s, format=%s, version=%s", required.modelType, required.format, required.formatVersion);
    }
}
