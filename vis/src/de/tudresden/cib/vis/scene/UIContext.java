package de.tudresden.cib.vis.scene;

import java.util.TreeMap;

public interface UIContext {
    public void runInUIContext(Runnable runnable);
    void animate(TreeMap<Integer, ChangeMap> scheduledChanges);
    void dispose();
}
