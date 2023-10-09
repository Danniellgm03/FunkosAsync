package org.funkoAsync.services;

import org.funkoAsync.enums.Modelo;
import org.funkoAsync.exceptions.funko.FunkoNoEncontradoException;
import org.funkoAsync.models.Funko;
import org.funkoAsync.repositories.funko.FunkoRepositoryImpl;
import org.funkoAsync.services.funkos.FunkoCache;
import org.funkoAsync.services.funkos.FunkoServiceImpl;
import org.funkoAsync.services.storage.FunkoStorageServ;
import org.funkoAsync.services.storage.FunkoStorageServImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.nio.file.Path;
import java.rmi.server.ExportException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import static org.junit.jupiter.api.Assertions.*;


import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FunkoServiceImplTest {


    @Mock
    FunkoRepositoryImpl repository;

    @Mock
    FunkoCache cache;

    @Mock
    FunkoStorageServImpl storageFunko;

    @InjectMocks
    FunkoServiceImpl service;


    @Test
    void findAllTest() throws SQLException, ExecutionException, InterruptedException {
        List<Funko> funkos = new ArrayList<>();
        funkos.add(new Funko(null, UUID.randomUUID(), 1L,  "Mi Funko 2", Modelo.ANIME, 55.0, LocalDate.now(),  LocalDateTime.now(), LocalDateTime.now()));
        funkos.add(new Funko(null, UUID.randomUUID(), 12L,  "Mi Funko prueba", Modelo.MARVEL, 25.0, LocalDate.now(),  LocalDateTime.now(), LocalDateTime.now()));

        when(repository.findAll()).thenReturn(CompletableFuture.completedFuture(funkos));

        var res = service.findAll();

        assertAll(
                () -> assertTrue(res.size() == 2),
                () -> assertEquals(res.get(1).getNombre(), "Mi Funko prueba")


        );

    }

    @Test
    void saveTest() throws SQLException, ExecutionException, InterruptedException {
        Funko funko = new Funko(1, UUID.randomUUID(), 1L,  "Mi Funko 2", Modelo.ANIME, 55.0, LocalDate.now(),  LocalDateTime.now(), LocalDateTime.now());

        when(repository.save(funko)).thenReturn(CompletableFuture.completedFuture(funko));

        var res = service.save(funko);

        assertAll(
                () -> assertEquals(res.getNombre(), funko.getNombre())
        );

        verify(repository, times(1)).save(funko);
    }

    @Test
    void updateTest() throws SQLException, ExecutionException, InterruptedException {
        Funko funko = new Funko(2, UUID.randomUUID(), 1L,  "Mi Funko 2", Modelo.ANIME, 55.0, LocalDate.now(),  LocalDateTime.now(), LocalDateTime.now());

        when(repository.update(funko)).thenReturn(CompletableFuture.completedFuture(funko));

        var res = service.update(funko);
        assertAll(
                () -> assertEquals(funko.getNombre(), res.getNombre()),
                () -> assertEquals(funko.getId(), res.getId())
        );

        verify(repository, times(1)).update(funko);
    }

    @Test
    void updateNotExist() throws SQLException {
        Funko funko = new Funko(2, UUID.randomUUID(), 1L,  "Mi Funko 2", Modelo.ANIME, 55.0, LocalDate.now(),  LocalDateTime.now(), LocalDateTime.now());

        when(repository.update(funko)).thenThrow(new FunkoNoEncontradoException("Funko no encontrado con el id: " + funko.getId()));

        try {
            var res = service.update(funko);
        } catch (ExecutionException | InterruptedException | FunkoNoEncontradoException e) {
            assertEquals(e.getMessage(), "Funko no encontrado con el id: " + funko.getId());
        }
        verify(repository, times(1)).update(funko);
    }

    @Test
    void findById() throws SQLException, ExecutionException, InterruptedException {
        Funko funko = new Funko(2, UUID.randomUUID(), 1L,  "Mi Funko 2", Modelo.ANIME, 55.0, LocalDate.now(),  LocalDateTime.now(), LocalDateTime.now());

        when(repository.findById(2)).thenReturn(CompletableFuture.completedFuture(Optional.of(funko)));

        var res = service.findById(2);

        assertAll(
                () -> assertEquals(res.get().getNombre(), funko.getNombre())
        );

        verify(repository, times(1)).findById(2);
    }

    @Test
    void findByIdNotExists() throws SQLException, ExecutionException, InterruptedException {
        when(repository.findById(2)).thenReturn(CompletableFuture.completedFuture(Optional.empty()));

        var res = service.findById(2);

        assertAll(
                () -> assertFalse(res.isPresent())
        );

        verify(repository, times(1)).findById(2);
    }


    @Test
    void deleteById() throws SQLException, ExecutionException, InterruptedException {
        when(repository.deleteById(2)).thenReturn(CompletableFuture.completedFuture(true));

        var res = service.deleteById(2);

        assertTrue(res);

        verify(repository, times(1)).deleteById(2);
    }

    @Test
    void deleteByIdNotExist() throws SQLException, ExecutionException, InterruptedException {
        when(repository.deleteById(2)).thenReturn(CompletableFuture.completedFuture(false));

        var res = service.deleteById(2);

        assertFalse(res);

        verify(repository, times(1)).deleteById(2);
    }

    @Test
    void deleteAllTest() throws SQLException, ExecutionException, InterruptedException {
        when(repository.deleteAll()).thenReturn(CompletableFuture.completedFuture(null));

        service.deleteAll();

        verify(repository, times(1)).deleteAll();

    }

    @Test
    void findByNombreTest() throws SQLException, ExecutionException, InterruptedException {
        Funko funko = new Funko(2, UUID.randomUUID(), 1L,  "Mi Funko 2", Modelo.ANIME, 55.0, LocalDate.now(),  LocalDateTime.now(), LocalDateTime.now());

        when(repository.findByNombre("Mi Funko 2")).thenReturn(CompletableFuture.completedFuture(List.of(funko)));

        var res = service.findByNombre("Mi Funko 2");

        assertAll(
                () -> assertTrue(res.size() == 1),
                () -> assertEquals(funko.getNombre(), res.get(0).getNombre())
        );
    }

    @Test
    void backupTest() throws ExecutionException, InterruptedException, SQLException, ExportException {
        List<Funko> funkos = new ArrayList<>();
        funkos.add(new Funko(2, UUID.randomUUID(), 1L,  "Mi Funko 2", Modelo.ANIME, 55.0, LocalDate.now(),  LocalDateTime.now(), LocalDateTime.now()));
        funkos.add(new Funko(3, UUID.randomUUID(), 13L,  "Mi Funko 3", Modelo.ANIME, 55.0, LocalDate.now(),  LocalDateTime.now(), LocalDateTime.now()));

        when(repository.findAll()).thenReturn(CompletableFuture.completedFuture(funkos));
        when(storageFunko.exportToJsonAsync(funkos)).thenReturn(true);
        service.backup();

        verify(storageFunko, times(1)).exportToJsonAsync(funkos);
    }

    @Test
    void importCsvTest() throws ExecutionException, InterruptedException {
        List<Funko> funkos = new ArrayList<>();
        Funko funko = new Funko(2, UUID.randomUUID(), 1L,  "Mi Funko 2", Modelo.ANIME, 55.0, LocalDate.now(),  LocalDateTime.now(), LocalDateTime.now());
        Funko funko2 = new Funko(3, UUID.randomUUID(), 13L,  "Mi Funko 3", Modelo.ANIME, 55.0, LocalDate.now(),  LocalDateTime.now(), LocalDateTime.now());

        funkos.add(funko);
        funkos.add(funko2);

        Path path = Path.of("");
        String path_directory = path.toAbsolutePath().toString() + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "data" + File.separator;
        String file = path_directory + "funkos.csv";
        when(storageFunko.importFromCsvAsync(file)).thenReturn(funkos);
        when(repository.save(funko)).thenReturn(CompletableFuture.completedFuture(funko));
        when(repository.save(funko2)).thenReturn(CompletableFuture.completedFuture(funko2));

        var res = service.importCsv();
        var funkosRes = res.get();

        assertAll(
                () -> assertTrue(funkosRes.size() == 2),
                () -> assertEquals(funko.getNombre(), funkosRes.get(0).getNombre()),
                () -> assertEquals(funko2.getNombre(), funkosRes.get(1).getNombre())
        );

    }


}
