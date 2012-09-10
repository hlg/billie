package de.tudresden.cib.vis.scene;

import de.tudresden.cib.vis.util.MultiBiMap;

import java.util.*;

public class SceneManager<E, S> {

    private TreeMap<Integer, ChangeMap> scheduledChanges = new TreeMap<Integer, ChangeMap>();
    private Map<Event, ChangeMap> triggeredChanges = new HashMap<Event, ChangeMap>();
    private Map<Event, Collection<VisFactory2D.GraphObject>> triggers = new HashMap<Event, Collection<VisFactory2D.GraphObject>>();
    private MultiBiMap<E, VisFactory2D.GraphObject> mapped = MultiBiMap.create();
    private S scene;
    private UIContext uiContext;
    private Timer animationThread = new Timer();

    // TODO: use animation facilities of scene graph library if possible
    // TODO: initial state and reset

    private int advanceFrame(final int current, int maxFrame) {
        if(scheduledChanges.containsKey(current)) {
            uiContext.runInUIContext(new Runnable() {
                public void run() {
                    scheduledChanges.get(current).changeAll();
                }
            });
        }
        return  (current+1 == maxFrame) ? 0 : current+1;
    }

    private int getLongestTimeLine() {
        return scheduledChanges.lastKey();
    }

    private boolean hasAnimations() {
        return !scheduledChanges.isEmpty();
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
            animationThread.schedule(animation, delay, 40);   // 1 frame = 1 hour schedule, 1 frame = 40 ms animation -> 1 s animation = 1 day schedule time
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
        if(!triggeredChanges.containsKey(event)) triggeredChanges.put(event, new ChangeMap());
        triggeredChanges.get(event).addChange(change, graphObject);
    }

    public <S extends VisFactory2D.GraphObject> void addChange(int time, S graphObject, Change<S> change) {
        if(!scheduledChanges.containsKey(time)) scheduledChanges.put(time, new ChangeMap());
        scheduledChanges.get(time).addChange(change, graphObject);
    }

    public List<Change> getChanges(int time, VisFactory2D.GraphObject graph) {
        return (scheduledChanges.containsKey(time) && scheduledChanges.get(time).containsKey(graph))
                ? scheduledChanges.get(time).get(graph)
                : null;
    }

    public List<Change> getChanges(Event event, VisFactory2D.GraphObject graph) {
        return (triggeredChanges.containsKey(event) && triggeredChanges.get(event).containsKey(graph))
                ? triggeredChanges.get(event).get(graph)
                : null;
    }

    public void addTrigger(Event event, VisFactory2D.GraphObject graphObject) {
        if(!triggers.containsKey(event)) triggers.put(event, new HashSet<VisFactory2D.GraphObject>());
        triggers.get(event).add(graphObject);
    }

    public void fire(Event event, VisFactory2D.GraphObject triggerGraph) {
        if(triggers.get(event).contains(triggerGraph)){
            E data = getData(triggerGraph);
            triggeredChanges.get(event).change(getGraphs(data));
        }
    }

    public VisFactory2D.GraphObject getFirstGraph(E data) {
        Collection<VisFactory2D.GraphObject> graphObjects = mapped.get(data);
        return graphObjects.isEmpty() ? null : graphObjects.iterator().next();
    }

    public Collection<VisFactory2D.GraphObject> getGraphs(E data){
        return mapped.get(data);
    }

    public E getData(VisFactory2D.GraphObject graphObject) {
        return mapped.inverse().get(graphObject);
    }

    public void addMapped(E source, VisFactory2D.GraphObject target) {
        mapped.put(source, target);
    }

    public void setScene(S scene) {
        this.scene = scene;
    }

    public S getScene() {
        return scene;
    }

    public void setUiContext(UIContext uiContext) {
        this.uiContext = uiContext;
    }

    public void dispose() {
        animationThread.cancel();
    }
}
