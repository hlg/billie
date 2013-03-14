package de.tudresden.cib.vis.configurations;

import de.tudresden.cib.vis.data.bimserver.EMFIfcParser;
import de.tudresden.cib.vis.mapping.Configuration;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.VisFactory2D;
import org.bimserver.models.ifc2x3tc1.*;
import org.eclipse.emf.common.util.EList;

import java.util.*;

public class Ifc_2D<S> extends Configuration<EMFIfcParser.EngineEObject, S> {

    public Ifc_2D(Mapper<EMFIfcParser.EngineEObject, ?, S> mapper) {
        super(mapper);
    }

    public void configSemantic() {
        mapper.addMapping(new PropertyMap<EMFIfcParser.EngineEObject, VisFactory2D.Rectangle>() {
            @Override
            protected void configure() {
                final int scale = 10;
                for (IfcRepresentation repr : ((IfcColumn) data.getObject()).getRepresentation().getRepresentations()) {
                    if (repr.getRepresentationType().equals("SweptSolid")) {
                        for (IfcRepresentationItem item : repr.getItems()) {
                            if (item instanceof IfcSweptAreaSolid) {
                                IfcProfileDef profile = ((IfcSweptAreaSolid) item).getSweptArea();
                                if (profile instanceof IfcRectangleProfileDef) {
                                    IfcRectangleProfileDef rect = (IfcRectangleProfileDef) profile;
                                    graphObject.setHeight((int) rect.getYDim() / scale);
                                    graphObject.setWidth((int) rect.getXDim() / scale);
                                    EList<Double> coordinates = rect.getPosition().getLocation().getCoordinates();
                                    graphObject.setLeft(coordinates.get(0).intValue() / scale);
                                    graphObject.setTop(coordinates.get(1).intValue() / scale);
                                }
                            }
                        }
                    }
                }
            }
        });

    }

    public void config() {
        final double scale = 0.02;
        final int offsetX = 200;
        final int offsetY = 200;
        final int level = 100;
        mapper.addMapping(new PropertyMap<EMFIfcParser.EngineEObject, VisFactory2D.Polyline>() {
            @Override
            protected boolean condition() {
                return data.getObject() instanceof IfcColumn;
            }

            @Override
            protected void configure() {
                Set<Integer> above = new TreeSet<Integer>();
                for (int i = 0, iz = 2; iz < data.getGeometry().vertizes.size(); i++, iz += 3) {
                    if (data.getGeometry().vertizes.get(iz) > level) above.add(i);
                }
                Set<List<List<Integer>>> cuttingEdges = new HashSet<List<List<Integer>>>();
                for (int i = 0; i < data.getGeometry().indizes.size(); i += 3) {
                    List<Integer> aboveCt = new ArrayList<Integer>();
                    List<Integer> belowCt = new ArrayList<Integer>();
                    for (int pt = i; pt < 3 + i; pt++) {
                        Integer ind = data.getGeometry().indizes.get(pt);
                        if (above.contains(ind)) aboveCt.add(ind);
                        else belowCt.add(ind);
                    }
                    if (!(aboveCt.isEmpty() || belowCt.isEmpty())) {
                        List<List<Integer>> lines = new ArrayList<List<Integer>>(2);
                        lines.add(Arrays.asList(aboveCt.get(0), belowCt.get(0)));
                        if (aboveCt.size() == 1) {
                            lines.add(Arrays.asList(aboveCt.get(0), belowCt.get(1)));
                        } else {
                            lines.add(Arrays.asList(aboveCt.get(1), belowCt.get(0)));
                        }
                        cuttingEdges.add(lines);
                    }
                }
                List<Integer> current = cuttingEdges.iterator().next().get(0);
                while (current != null) {
                    double[] pt = interpolateXY(data.getGeometry().vertizes, current.get(0) * 3, current.get(1) * 3, level);
                    graphObject.addPoint((int) (pt[0] * scale + offsetX), (int) (pt[1] * scale + offsetY));
                    List<Integer> newCurrent = null;
                    for (List<List<Integer>> edge : cuttingEdges) {
                        if (edge.get(0) != current && edge.get(0).equals(current)) {
                            newCurrent = edge.get(1);
                            break;
                        }
                        if (edge.get(1) != current && edge.get(1).equals(current)) {
                            newCurrent = edge.get(0);
                            break;
                        }
                    }
                    current = newCurrent;
                }
            }

            private double[] interpolateXY(List<Float> vertizes, Integer i1, Integer i2, int zLevel) {
                Float z1 = vertizes.get(i1 + 2);
                Float z2 = vertizes.get(i2 + 2);
                double interpol = (z1 - zLevel) / (z1 - z2);
                Float y1 = vertizes.get(i1 + 1);
                Float y2 = vertizes.get(i2 + 1);
                double y = (y1 - y2) * interpol + y2;
                Float x1 = vertizes.get(i1);
                Float x2 = vertizes.get(i2);
                double x = (x1 - x2) * interpol + x2;
                return new double[]{x, y};
            }

            private double interpolate(int i, int single, int other1, double interpol, int dim) {
                Float yo1 = data.getGeometry().vertizes.get(i + other1 + dim);
                return yo1 / (yo1 - data.getGeometry().vertizes.get(i + single + dim)) / interpol;
            }
        });
    }

}
