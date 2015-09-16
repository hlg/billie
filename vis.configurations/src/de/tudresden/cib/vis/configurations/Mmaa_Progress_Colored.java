package de.tudresden.cib.vis.configurations;

import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.bimserver.EMFIfcParser;
import de.tudresden.cib.vis.data.multimodel.LinkedObject;
import de.tudresden.cib.vis.filter.Condition;
import de.tudresden.cib.vis.mapping.Configuration;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.Change;
import de.tudresden.cib.vis.scene.VisFactory3D;
import net.fortuna.ical4j.model.component.VEvent;
import org.bimserver.models.ifc2x3tc1.IfcRoot;

import java.util.Collection;
import java.util.List;

public class Mmaa_Progress_Colored extends Configuration<LinkedObject<EMFIfcParser.EngineEObject>, Condition<LinkedObject<EMFIfcParser.EngineEObject>>> {
    private final String scheduleId;
    private final String reportId;
    private final String guid;
    public int scale = 3600000*5;

    public Mmaa_Progress_Colored(List<String> ids, String guid) {
        this.scheduleId = ids.get(1);
        this.reportId = ids.get(2);
        this.guid = guid;
    }

    @Override
    public void config() {
        this.addStatistics("earliestStart", new DataAccessor.Folding<LinkedObject<EMFIfcParser.EngineEObject>, Long>(Long.MAX_VALUE) {
            @Override
            public Long function(Long aLong, LinkedObject<EMFIfcParser.EngineEObject> engineEObjectLinkedObject) {
                Collection<LinkedObject.ResolvedLink> links = engineEObjectLinkedObject.getResolvedLinks();
                for (LinkedObject.ResolvedLink link : links) {
                    for (VEvent event : link.getAllLinkedEvents(scheduleId)) {
                        aLong = Math.min(event.getStartDate().getDate().getTime(), aLong);
                    }
                }
                return aLong;
            }
        });
        this.addStatistics("latestEnd", new DataAccessor.Folding<LinkedObject<EMFIfcParser.EngineEObject>, Long>(Long.MIN_VALUE) {
            @Override
            public Long function(Long aLong, LinkedObject<EMFIfcParser.EngineEObject> engineEObjectLinkedObject) {
                Collection<LinkedObject.ResolvedLink> links = engineEObjectLinkedObject.getResolvedLinks();
                for (LinkedObject.ResolvedLink link : links) {
                    for (VEvent event : link.getAllLinkedEvents(scheduleId)) {
                        aLong = Math.max(event.getEndDate().getDate().getTime(), aLong);
                    }
                }
                return aLong;
            }
        });
        final Change reset = new Change<VisFactory3D.Polyeder>() {
            @Override
            protected void configure() {
                graph.setColor(0, 0, 0, 255);
            }
        };
        final Change<VisFactory3D.Polyeder> red = new Change<VisFactory3D.Polyeder>() {
            @Override
            protected void configure() {
                graph.setColor(255,0,0,150);
            }
        };
        final Change<VisFactory3D.Polyeder> yellow = new Change<VisFactory3D.Polyeder>() {
            @Override
            protected void configure() {
                graph.setColor(255,255,0,150);
            }
        };
        final Change<VisFactory3D.Polyeder> green = new Change<VisFactory3D.Polyeder>() {
            @Override
            protected void configure() {
                graph.setColor(0,255,0,150);
            }
        };
        this.addMapping(new Condition<LinkedObject<EMFIfcParser.EngineEObject>>() {
                              @Override
                              public boolean matches(LinkedObject<EMFIfcParser.EngineEObject> data) {
                                  return !data.getResolvedLinks().isEmpty();
                              }
                          }, new PropertyMap<LinkedObject<EMFIfcParser.EngineEObject>, VisFactory3D.Polyeder>() {
                              @Override
                              protected void configure() {
                                  graphObject.setNormals(data.getKeyObject().getGeometry().normals);
                                  graphObject.setVertizes(data.getKeyObject().getGeometry().vertizes);
                                  graphObject.setIndizes(data.getKeyObject().getGeometry().indizes);
                                  long earliestStart = getStats("earliestStart").longValue();
                                  long latestEnd = getStats("latestEnd").longValue();
                                  long duration = latestEnd - earliestStart + scale;
                                  graphObject.setColor(0, 0, 0, 255);
/*
                                  addChange(0, reset);
                                  addChange((int) (duration / scale), reset);
                                  addChange((int) (duration * 2 / scale), reset);
*/
                                  for(LinkedObject.ResolvedLink link: data.getResolvedLinks()){
                                      Collection<VEvent> reports = link.getAllLinkedEvents(reportId);
                                      Collection<VEvent> schedules = link.getAllLinkedEvents(scheduleId);
                                      if(reports!= null && !reports.isEmpty() && schedules!=null && !schedules.isEmpty()){
                                          assert reports.size()<=1;
                                          assert schedules.size()<=1;
                                          VEvent report = reports.iterator().next();
                                          long reportPoint = report.getStartDate().getDate().getTime(); // TODO: reports should have no duration, hence no end date
                                          VEvent schedule = schedules.iterator().next();
                                          long scheduleStart = schedule.getStartDate().getDate().getTime();
                                          long scheduleEnd = schedule.getEndDate().getDate().getTime();
                                          assert scheduleStart<=scheduleEnd;
                                          int time = (int) ((reportPoint - earliestStart) / scale);
                                          if(reportPoint<scheduleStart){
                                              // addChange(time, green);
                                              graphObject.setColor(0,255,0,150);
                                          } else if (reportPoint>scheduleEnd){
                                              // addChange(time, red);
                                              graphObject.setColor(255,0,0,150);
                                          } else {
                                              // addChange(time, yellow);
                                              graphObject.setColor(255,255,0,150);
                                          }
                                      }
                                  }

                                  if(((IfcRoot)data.getKeyObject().getObject()).getGlobalId().getWrappedValue().equals(guid)){
                                              graphObject.setColor(0,0,255,0);
                                  }
                              }
                          }
        );
    }
}
