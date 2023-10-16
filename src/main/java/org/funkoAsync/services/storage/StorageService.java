package org.funkoAsync.services.storage;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Interfaz que se encarga de definir los métodos que se van a utilizar para el almacenamiento
 * @param <T>
 */
public interface StorageService<T> {

    /**
     * Método que se encarga de exportar los datos a un fichero JSON
     * @param data
     */
    Boolean exportToJsonAsync(List<T> data) throws ExecutionException, InterruptedException;

    /**
     * Método que se encarga de exportar los datos a un fichero CSV
     * @param filePath
     */
    List<T> importFromCsvAsync(String filePath) throws ExecutionException, InterruptedException;


}
