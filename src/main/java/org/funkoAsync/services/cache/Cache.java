package org.funkoAsync.services.cache;

import org.funkoAsync.models.Funko;

/**
 * Cache interface
 * @param <K>
 * @param <V>
 */
public interface Cache<K, V> {

    /**
     * Insertar un K y un V en el cache
     * @param key
     * @param value
     * @throws Exception
     */
    void put(K key, V value) throws Exception;

    /**
     * Obtener un V dado un K
     * @param key
     * @return
     */
    V get(K key);

    /**
     * Borrar un V dado un K
     * @param key
     * @return
     */
    Funko delete(K key);

    /**
     * Borrar el cache
     */
    void clear();

    /**
     * Cerrar la cache
     */
    void shutdown();

}
