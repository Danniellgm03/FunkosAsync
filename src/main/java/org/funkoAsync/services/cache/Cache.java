package org.funkoAsync.services.cache;

public interface Cache<K, V> {

    void put(K key, V value);

    V get(K key);

    void delete(K key);

    void clear();

    void shutdown();

}
