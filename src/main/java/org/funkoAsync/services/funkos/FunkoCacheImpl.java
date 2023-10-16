package org.funkoAsync.services.funkos;

import org.funkoAsync.exceptions.cache.CachePutNullKeyException;
import org.funkoAsync.exceptions.cache.CachePutNullValueException;
import org.funkoAsync.models.Funko;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Implementación de la interfaz FunkoCache
 * Extiende de LinkedHashMap para poder tener un control de la cache
 * y poder eliminar los elementos mas antiguos
 * Implementa la interfaz ScheduledExecutorService para poder limpiar la cache
 * cada cierto tiempo
 * @author daniel
 */
public class FunkoCacheImpl implements FunkoCache{

    private final int maxSize;

    private Logger logger = LoggerFactory.getLogger(FunkoCacheImpl.class);

    private final Map<Integer, Funko> cache;

    private final ScheduledExecutorService cleaner;

    /**
     * Constructor de la clase FunkoCacheImpl
     * @param maxSize
     * @param initDelay
     * @param period
     * @param timeUnit
     */
    public FunkoCacheImpl(int maxSize, int initDelay, int period, TimeUnit timeUnit){
        this.maxSize = maxSize;
        this.cache =  new LinkedHashMap<>(maxSize, 0.75f, true ){
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, Funko> eldest) {
                return size() > maxSize;
            }

        };
        this.cleaner = Executors.newSingleThreadScheduledExecutor();
        this.cleaner.scheduleAtFixedRate(this::clear, initDelay, period, timeUnit);
    }


    /**
     * Método que añade un funko a la cache
     * @param key
     * @param value
     * @throws CachePutNullKeyException
     * @throws CachePutNullValueException
     */
    @Override
    public void put(Integer key, Funko value) throws CachePutNullKeyException, CachePutNullValueException {
        logger.debug("Añadiendo funko a la cache");
        if(key == null){
            throw new CachePutNullKeyException("No se pudo insertar en la cache por la key(id) es null");
        }else if(value == null){
            throw new CachePutNullValueException("El funo no se inserto a la cache debido a que es un null");
        }

        cache.put(key, value);
    }

    /**
     * Método que obtiene un funko de la cache
     * @param key
     * @return
     */
    @Override
    public Funko get(Integer key) {
        logger.debug("Obteniendo funko de la cache con id: " + key);
        return cache.get(key);
    }

    /**
     * Método que elimina un funko de la cache
     * @param key
     * @return
     */
    @Override
    public Funko delete(Integer key) {
        logger.debug("Eliminando funko de la cache con id: "+ key);
        return cache.remove(key);
    }

    /**
     * Método que limpia la cache
     */
    @Override
    public void clear() {
        cache.entrySet().removeIf(entry -> {
            boolean shoulRemove = entry.getValue().getUpdated_at().plusMinutes(2).isBefore(LocalDateTime.now());
            if(shoulRemove){
                logger.debug("Eliminación automatica por caducidad del funko con id: " + entry.getKey());
            }
            return shoulRemove;
        });
    }

    /**
     * Método que cierra el cleaner
     */
    @Override
    public void shutdown() {
        cleaner.shutdown();
    }


}
