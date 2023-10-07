package org.funkoAsync.services.storage;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface StorageService<T> {

    Void exportToJsonAsync(List<T> data) throws ExecutionException, InterruptedException;

    List<T> importFromCsvAsync(String filePath) throws ExecutionException, InterruptedException;


}
