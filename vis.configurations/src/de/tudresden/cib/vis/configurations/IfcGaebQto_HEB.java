package de.tudresden.cib.vis.configurations;

import cib.lib.gaeb.model.gaeb.TgBoQCtgy;
import cib.mf.qto.model.AnsatzType;
import de.tudresden.cib.vis.TriggerListener;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.Hierarchic;
import de.tudresden.cib.vis.data.multimodel.HierarchicGaebAccessor;
import de.tudresden.cib.vis.data.multimodel.LinkedObject;
import de.tudresden.cib.vis.filter.Condition;
import de.tudresden.cib.vis.mapping.Configuration;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.Change;
import de.tudresden.cib.vis.scene.DefaultEvent;
import de.tudresden.cib.vis.scene.VisFactory2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IfcGaebQto_HEB<S> extends Configuration<LinkedObject.ResolvedLink, Condition<LinkedObject.ResolvedLink>, S> {

    private int ifcScale;
    private int gaebScale;
    private static int SMALLSIZE = 10;
    private static double BUNDLING = 0.5;
    private static boolean SKIP_LAST_LEVEL = false;

    public IfcGaebQto_HEB(Mapper<LinkedObject.ResolvedLink, Condition<LinkedObject.ResolvedLink>, ?, S> mapper) {
        super(mapper);
    }

    @Override
    public void config() {
        mapper.addStatistics("maxIfcPos", new DataAccessor.Folding<LinkedObject.ResolvedLink, Integer>(0) {
            @Override
            public Integer function(Integer number, LinkedObject.ResolvedLink link) {
                Hierarchic ifc = link.getLinkedHierarchicIfc().values().iterator().next();
                if (SKIP_LAST_LEVEL) ifc = ifc.getParent();
                int ifcPos = ifc.getNodesBefore() + ifc.getNodeSize() / 2;
                return Math.max(number, ifcPos);
            }
        });
        mapper.addStatistics("maxGaebPos", new DataAccessor.Folding<LinkedObject.ResolvedLink, Integer>(0) {
            @Override
            public Integer function(Integer integer, LinkedObject.ResolvedLink link) {
                HierarchicGaebAccessor.HierarchicTgItemBoQCtgy gaeb = link.getLinkedHierarchicGaeb().values().iterator().next();
                int gaebPos = gaeb.getNodesBefore() + gaeb.getNodeSize() / 2;
                return Math.max(integer, gaebPos);
            }
        });
        mapper.addGlobal("icycleRatio", new Mapper.PreProcessing<Double>() {
            @Override
            public Double getResult() {
                double ratio = Double.valueOf((Integer) mp.getStats("maxIfcPos")) / (Integer) mp.getStats("maxGaebPos");
                boolean topBigger = ratio > 1;
                ifcScale = topBigger ? SMALLSIZE : (int) (1. / ratio * SMALLSIZE);
                gaebScale = topBigger ? (int) (ratio * SMALLSIZE) : SMALLSIZE;
                return ratio;
            }
        });
        mapper.addMapping(new PropertyMap<LinkedObject.ResolvedLink, VisFactory2D.Bezier>() {
            @Override
            protected void configure() {
                Hierarchic currentIfc = data.getLinkedHierarchicIfc().values().iterator().next();
                Hierarchic currentGaeb = data.getLinkedHierarchicGaeb().values().iterator().next();
                // TODO: sum up GAEB links
                assert currentGaeb.getNodeSize() == 1;
                assert currentIfc.getNodeSize() == 1;
                List<Point> layouted = new ArrayList<Point>();
                if (!SKIP_LAST_LEVEL) layouted.add(pointFor(currentIfc, ifcScale, -100, 300));
                while (currentIfc.getParent() != null) {
                    currentIfc = currentIfc.getParent();
                    if (currentIfc.getParent() != null)
                        layouted.add(pointFor(currentIfc, ifcScale, -100, 300)); // skip root
                }
                List<Hierarchic> gaebPath = new ArrayList<Hierarchic>();
                gaebPath.add(currentGaeb);
                while (currentGaeb.getParent() != null) {
                    currentGaeb = currentGaeb.getParent();
                    if (currentGaeb.getParent() != null) gaebPath.add(currentGaeb); //skip root
                }
                for (int i = gaebPath.size() - 1; i >= 0; i--) {
                    layouted.add(pointFor(gaebPath.get(i), gaebScale, 100, 300));
                }

                Point first = layouted.get(0);
                int n = layouted.size();
                Point last = layouted.get(n - 1);
                double ax = (1 - BUNDLING) * (last.x - first.x) / (n - 1);
                double ay = (1 - BUNDLING) * (last.y - first.y) / (n - 1);
                double cx = (1 - BUNDLING) * first.x;
                double cy = (1 - BUNDLING) * first.y;
                for (int i = 0; i < layouted.size(); i++) {
                    Point p = layouted.get(i);
                    graphObject.addPoint((int) (BUNDLING * p.x + cx + i * ax), (int) (BUNDLING * p.y + cy + i * ay));
                }
                // colorBoQCategory(data, graphObject);
                // colorStartDate(data, graphObject);
                // colorCompletion(data, graphObject);

                Change<VisFactory2D.Bezier> highlight = new Change<VisFactory2D.Bezier>() {
                    private LinkedObject.ResolvedLink d = data;
                    @Override
                    protected void configure() {
                        graph.setColor(150, 0, 0);
                        graph.setForeground();
                        for (TriggerListener<LinkedObject.ResolvedLink> listener: listeners) listener.notify(d);

                    }
                };
                addTrigger(DefaultEvent.CLICK);
                addChange(DefaultEvent.CLICK, highlight);
                addTrigger(DefaultEvent.DRAG);
                addChange(DefaultEvent.DRAG, highlight);
            }
        });
    }

    private void colorCompletion(LinkedObject.ResolvedLink data, VisFactory2D.Bezier graphObject) {
        double finished = 0;
        for(String lmid: new String[]{"FM5","FM6","FM7","FM8","FM9"}){
            Collection<AnsatzType> allLinkedQtos = data.getAllLinkedQtos(lmid);
            if(allLinkedQtos!=null) for (AnsatzType ansatz: allLinkedQtos) if (ansatz!=null)finished += ansatz.getResult();
        }
        double planned = 0;
        for (AnsatzType ansatzType : data.getAllLinkedQtos("FM3")){
            planned += ansatzType.getResult();
        }
        double completion = finished / planned;
        if (completion>1) completion = 1; // cutoff surplus quantities, TODO: check beforehand
        int v = 150 - (int)(completion*150);
        graphObject.setColor(v,150,0);
        if(finished==0) { graphObject.setColor(220,220,220); graphObject.setBackground(); }
    }

    private void colorBoQCategory(LinkedObject.ResolvedLink data, VisFactory2D.GraphObject2D graphObject) {
        TgBoQCtgy parent = ((TgBoQCtgy) data.getLinkedHierarchicGaeb().values().iterator().next().getParent().getObject());
        if (parent.getID().equals("ILAGFNBA")) graphObject.setColor(150, 0, 0); else graphObject.setBackground();
    }

    private void colorStartDate(LinkedObject.ResolvedLink data, VisFactory2D.GraphObject2D graphObject) {
        String lmMonthSTartd = monthStarted(data);
        if (lmMonthSTartd==null) {
            graphObject.setColor(200,200,200);
            graphObject.setBackground();
        }
        else if (lmMonthSTartd.equals("FM5"))graphObject.setColor(0,150,0);
        else if (lmMonthSTartd.equals("FM6"))graphObject.setColor(150,150,0);
        else if (lmMonthSTartd.equals("FM7"))graphObject.setColor(150,0,0);
        else if (lmMonthSTartd.equals("FM8"))graphObject.setColor(150, 0,150);
        else if (lmMonthSTartd.equals("FM9"))graphObject.setColor(0,0,150);
    }

    private String monthStarted(LinkedObject.ResolvedLink data) {
        for(String lmid: new String[]{"FM5","FM6","FM7","FM8","FM9"}){
            if(data.getLinkedQto().get(lmid)!=null) { return lmid; }
        }
        return null;
    }

    private Point pointFor(Hierarchic current, int scale, int distance, int offset) {
        return new Point(current.getNodesBefore() * scale + current.getNodeSize() * scale / 2, current.getDepth() * distance + offset);
    }

    public int getIfcScale() {
        return ifcScale;
    }

    public int getGaebScale() {
        return gaebScale;
    }

    private class Point {
        int x;
        int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }



}
