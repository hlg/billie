package de.tudresden.cib.vis.filter;

public interface Condition<T> {
    public boolean matches(T data);
}
