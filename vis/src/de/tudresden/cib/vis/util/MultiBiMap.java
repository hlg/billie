package de.tudresden.cib.vis.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MultiBiMap<K,V> implements Multimap<K,V> {

    // TODO: alternative Multimaps.invertFrom(source, target) once after putting everything into the original map (make sure its immutuable then)

    private Multimap<K,V> delegate;
    private Map<V,K> inverse;

    private MultiBiMap(){
        delegate = ArrayListMultimap.create();
        inverse = new HashMap<V, K>();
    }

    public int size() {
        return delegate.size();
    }

    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    public boolean containsEntry(Object key, Object value) {
        return delegate.containsEntry(key, value);
    }

    public boolean put(K key, V value) {
        inverse.put(value, key);
        return delegate.put(key, value);
    }

    public boolean remove(Object key, Object value) {
        inverse.remove(value);
        return delegate.remove(key, value);
    }

    public boolean putAll(K key, Iterable<? extends V> values) {
        for (V value : values){
            inverse.put(value,  key);
        }
        return delegate.putAll(key, values) ;
    }

    public boolean putAll(Multimap<? extends K, ? extends V> multimap) {
        for(Map.Entry<? extends K, ? extends V> entry: multimap.entries()){
            inverse.put(entry.getValue(), entry.getKey());
        }
        return delegate.putAll(multimap);
    }

    public Collection<V> replaceValues(K key, Iterable<? extends V> values) {
        removeAllValues(inverse, key);
        for(V value: values) {
            inverse.put(value, key);
        }
        return delegate.replaceValues(key, values);
    }

    public Collection<V> removeAll(Object key) {
        removeAllValues(inverse, (K) key);
        return delegate.removeAll(key);
    }

    private <K_, V_> void removeAllValues(Map<V_, K_> map, K_ value) {
        for(Map.Entry<V_, K_> entry:map.entrySet()){
            if(entry.getValue().equals(value)) map.remove(entry.getKey());
        }
    }

    public void clear() {
        inverse.clear();
        delegate.clear();
    }

    public Collection<V> get(K key) {
        return delegate.get(key);
    }

    public Set<K> keySet() {
        return delegate.keySet();
    }

    public Multiset<K> keys() {
        return delegate.keys();
    }

    public Collection<V> values() {
        return delegate.values();
    }

    public Collection<Map.Entry<K, V>> entries() {
        return delegate.entries();
    }

    public Map<K, Collection<V>> asMap() {
        return delegate.asMap();
    }

    public static <K,V> MultiBiMap<K,V> create(){
        return new MultiBiMap<K, V>();
    }

    public Map<V, K> inverse() {
        return inverse;
    }
}
