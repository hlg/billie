package de.tudresden.cib.vis.scene;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SceneManager {

    private Map<Class, TimeLine> timeLines = new HashMap<Class, TimeLine>();
    // TODO: initial state and reset

    public <T extends VisFactory2D.GraphObject> TimeLine<T> getTimeLine(Class<T> graphClass) {
        if (!timeLines.containsKey(graphClass)) timeLines.put(graphClass, new TimeLine<T>());
        return timeLines.get(graphClass);
    }

    private int advanceFrame(int current, int maxFrame) {
        for(TimeLine timeLine: timeLines.values()){
            timeLine.changeAll(current);
        }
        return  (current+1 == maxFrame) ? 0 : current+1;
    }

    private int getLongestTimeLine() {
        int longest = 0;
        for (TimeLine<?> timeLine : this.timeLines.values()) {
            longest = Math.max(timeLine.lastKey(), longest);
        }
        return longest;
    }

    private boolean hasAnimations() {
        for (TimeLine timeLine : timeLines.values()) {
            if (!timeLine.isEmpty()) return true;
        }
        return false;
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


}
