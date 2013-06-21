package de.tudresden.cib.vis.filter.bimserver;

import de.tudresden.cib.vis.filter.CodeFilter;
import org.bimserver.plugins.serializers.IfcModelInterface;

public class BimQLFilter implements CodeFilter.ModelModel<IfcModelInterface> {
    @Override
    public IfcModelInterface filter(String condition, IfcModelInterface toBeFiltered) {
        return null;  // TODO: integrate
    }

}
