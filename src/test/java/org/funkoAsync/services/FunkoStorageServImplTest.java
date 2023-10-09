package org.funkoAsync.services;

import org.funkoAsync.enums.Modelo;
import org.funkoAsync.models.Funko;
import org.funkoAsync.services.files.CsvManager;
import org.funkoAsync.services.files.JsonManager;
import org.funkoAsync.services.storage.FunkoStorageServImpl;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;


public class FunkoStorageServImplTest {

    private CsvManager csvManager;

    private JsonManager jsonManager;
    private FunkoStorageServImpl storageServ;

    FunkoStorageServImplTest(){
        csvManager = new CsvManager();
        jsonManager = new JsonManager();
        storageServ = FunkoStorageServImpl.getInstance(csvManager, jsonManager);
    }


    @Test
    void exportToJsonAsyncTest() throws ExecutionException, InterruptedException {
        List<Funko> funkos = new ArrayList<>();
        funkos.add(new Funko(null, UUID.randomUUID(), 1L,  "Mi Funko 2", Modelo.ANIME, 55.0, LocalDate.now(),  LocalDateTime.now(), LocalDateTime.now()));
        funkos.add(new Funko(null, UUID.randomUUID(), 12L,  "Mi Funko prueba", Modelo.MARVEL, 25.0, LocalDate.now(),  LocalDateTime.now(), LocalDateTime.now()));

        assertTrue(storageServ.exportToJsonAsync(funkos));
    }

    @Test
    void exportToJsonAsyncTestNotSucces() throws ExecutionException, InterruptedException {
        assertFalse(storageServ.exportToJsonAsync(null));
    }

    @Test
    void importFromCsvAsyncTest() throws ExecutionException, InterruptedException {
        Path path = Path.of("");
        String path_directory = path.toAbsolutePath().toString() + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "data" + File.separator;
        String file = path_directory + "funkos.csv";
        assertTrue(!storageServ.importFromCsvAsync(file).isEmpty());
    }


}
