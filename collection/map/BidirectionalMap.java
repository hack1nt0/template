package template.collection.map;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by dy on 2017/1/29.
 */
public class BidirectionalMap<K, V> {
    Map<K, V> keyValue = new HashMap<K, V>();
    Map<V, K> valueKey = new HashMap<V, K>();

    public void put(K key, V value) {
        if (keyValue.containsKey(key) || valueKey.containsKey(value))
            throw new IllegalArgumentException();
        keyValue.put(key, value);
        valueKey.put(value, key);
    }

    public void putValue(V value, K key) {
        put(key, value);
    }

    public V get(K key) {
        return keyValue.get(key);
    }

    public K getFromValue(V value) {
        return valueKey.get(value);
    }

    public Set<K> keys() {
        return keyValue.keySet();
    }

    public Set<V> values() {
        return valueKey.keySet();
    }

    public int size() {
        return keyValue.size();
    }
}
