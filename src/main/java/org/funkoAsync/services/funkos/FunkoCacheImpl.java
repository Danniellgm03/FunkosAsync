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

public class FunkoCacheImpl implements FunkoCache{

    private final int maxSize;

    private Logger logger = LoggerFactory.getLogger(FunkoCacheImpl.class);

    private final Map<Integer, Funko> cache;

    private final ScheduledExecutorService cleaner;

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

    @Override
    public Funko get(Integer key) {
        logger.debug("Obteniendo funko de la cache con id: " + key);
        return cache.get(key);
    }

    @Override
    public Funko delete(Integer key) {
        logger.debug("Eliminando funko de la cache con id: "+ key);
        return cache.remove(key);
    }

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

    @Override
    public void shutdown() {
        cleaner.shutdown();
    }


}
