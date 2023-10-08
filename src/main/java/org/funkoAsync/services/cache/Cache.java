package org.funkoAsync.services.cache;

import org.funkoAsync.models.Funko;

public interface Cache<K, V> {

    void put(K key, V value) throws Exception;

    V get(K key);

    Funko delete(K key);

    void clear();

    void shutdown();

}
