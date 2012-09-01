package de.tudresden.cib.vis.scene;

import java.util.*;

public class SceneManager {

    private TreeMap<Integer, ChangeMap> timeLines = new TreeMap<Integer, ChangeMap>();
    private Map<Event, ChangeMap> eventMaps = new HashMap<Event, ChangeMap>();

    // TODO: use animation facilities of scene graph library if possible
    // TODO: initial state and reset

    private int advanceFrame(int current, int maxFrame) {
        if(timeLines.containsKey(current)) timeLines.get(current).changeAll();
        return  (current+1 == maxFrame) ? 0 : current+1;
    }

    private int getLongestTimeLine() {
        return timeLines.lastKey();
    }

    private boolean hasAnimations() {
        return !timeLines.isEmpty();
    }

    public void animate(){
        animate(2000);
    }

    private void animate(long delay) {
        if (hasAnimations()) {
            TimerTask animation = new TimerTask() {
                int frame = 0;
                int maxFrame = getLongestTimeLine();

                @Override
                public void run() {
                    frame = advanceFrame(frame, maxFrame);
                }
            };
            new Timer().schedule(animation, delay, 40);   // 1 frame = 1 hour schedule, 1 frame = 40 ms animation -> 1 s animation = 1 day schedule time
        }
    }


    public void jumpToTime(int frame){
        int current = 0;
        int maxFrame = getLongestTimeLine();
        while(current < frame){
            current = advanceFrame(current, maxFrame);
        }
    }

    public <S extends VisFactory2D.GraphObject> void addChange(Event event, S graphObject, Change<S> change) {
        if(!eventMaps.containsKey(event)) eventMaps.put(event, new ChangeMap());
        eventMaps.get(event).addChange(change, graphObject);
    }

    public <S extends VisFactory2D.GraphObject> void addChange(int time, S graphObject, Change<S> change) {
        if(!timeLines.containsKey(time)) timeLines.put(time, new ChangeMap());
        timeLines.get(time).addChange(change, graphObject);
    }

    public List<Change> getChanges(int time, VisFactory2D.GraphObject graph) {
        return (timeLines.containsKey(time) && timeLines.get(time).containsKey(graph))
                ? timeLines.get(time).get(graph)
                : null;
    }
    public List<Change> getChanges(Event event, VisFactory2D.GraphObject graph) {
        return (eventMaps.containsKey(event) && eventMaps.get(event).containsKey(graph))
                ? eventMaps.get(event).get(graph)
                : null;

    }

}
