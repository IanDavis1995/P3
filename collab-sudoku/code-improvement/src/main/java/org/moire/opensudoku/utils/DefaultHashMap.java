package org.moire.opensudoku.utils;

import java.util.HashMap;

/**
 * Create a HashMap that supports a default value parameter.
 * Created by ian on 2/17/17.
 */
public class DefaultHashMap<K,V> extends HashMap<K,V> {
    /**
     * Overload the default HashMap get method with an additional defaultValue parameter.
     * @param key The key to get from the map
     * @param defaultValue The value that will be returned if the key doesn't exist.
     * @return The value for the given key, or defaultValue if the key doesn't exist.
     */
    public V get(Object key, V defaultValue) {
        return containsKey(key) ? super.get(key): defaultValue;
    }
}
