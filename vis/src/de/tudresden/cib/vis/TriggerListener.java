package de.tudresden.cib.vis;

import java.util.EventListener;

public interface TriggerListener<E> extends EventListener {
    void notify(E data);
}
