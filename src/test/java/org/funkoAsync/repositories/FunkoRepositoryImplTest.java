package org.funkoAsync.repositories;

import org.funkoAsync.enums.Modelo;
import org.funkoAsync.exceptions.funko.FunkoNoEncontradoException;
import org.funkoAsync.models.Funko;
import org.funkoAsync.repositories.funko.FunkoRepositoryImpl;
import org.funkoAsync.services.database.DataBaseManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;


public class FunkoRepositoryImplTest {


    private DataBaseManager db;

    private FunkoRepositoryImpl repository;

    FunkoRepositoryImplTest() {
        db = DataBaseManager.getInstance();
        repository = FunkoRepositoryImpl.getInstance(db);
    }

    @BeforeEach
    void tearDown() throws SQLException, ExecutionException, InterruptedException {
        repository.deleteAll().get();
    }

    @Test
    void findAll() throws SQLException, ExecutionException, InterruptedException {
        Funko funko = new Funko(null, UUID.randomUUID(), 1L,  "Mi Funko 2", Modelo.ANIME, 55.0, LocalDate.now(),  LocalDateTime.now(), LocalDateTime.now());
        Funko funko2 = new Funko(2, UUID.randomUUID(), 1L,  "Mi Funko 2", Modelo.ANIME, 55.0, LocalDate.now(),  LocalDateTime.now(), LocalDateTime.now());

        repository.save(funko).get();
        repository.save(funko2).get();

        var listFunkos = repository.findAll().get();

        assertAll(
                () -> assertTrue(listFunkos.size() == 2)
        );

    }

    @Test
    void saveTest() {
        Funko funko = new Funko(null, UUID.randomUUID(), 1L,  "Mi Funko 2", Modelo.ANIME, 55.0, LocalDate.now(),  LocalDateTime.now(), LocalDateTime.now());

        assertAll(
                () -> assertEquals(funko, repository.save(funko).get()),
                () -> assertNull(repository.save(null).get())
        );

    }

    @Test
    void updateTest() throws ExecutionException, InterruptedException, SQLException {
        Funko funko = new Funko(null, UUID.randomUUID(), 1L,  "Mi Funko 2", Modelo.ANIME, 55.0, LocalDate.now(),  LocalDateTime.now(), LocalDateTime.now());
        Funko funko2 = new Funko(30, UUID.randomUUID(), 1L,  "Mi Funko 2", Modelo.ANIME, 55.0, LocalDate.now(),  LocalDateTime.now(), LocalDateTime.now());

        repository.save(funko).get();
        funko.setPrecio(30.0);
        System.out.println(funko + " funko");
        Funko funko_updated = repository.update(funko).get();
        System.out.println(funko_updated + " funko actualizado");


        //TODO: TEST DE EXTEPCION
        assertAll(
                () ->  assertEquals(funko.getId(), funko_updated.getId()),
                () ->  assertEquals(funko.getPrecio(), funko_updated.getPrecio()),
                () ->  assertNull(repository.update(null).get())
        );
    }

    @Test
    void findById() throws SQLException, ExecutionException, InterruptedException {
        Funko funko = new Funko(null, UUID.randomUUID(), 1L,  "Mi Funko 2", Modelo.ANIME, 55.0, LocalDate.now(),  LocalDateTime.now(), LocalDateTime.now());

        Funko funko_inserted = repository.save(funko).get();
        assertAll(
                () -> assertEquals(funko_inserted.getId(), repository.findById(funko_inserted.getId()).get().get().getId()),
                () -> assertFalse(repository.findById(3).get().isPresent())
        );
    }

    @Test
    void deleteByIdTest() throws ExecutionException, InterruptedException, SQLException {
        Funko funko = new Funko(null, UUID.randomUUID(), 1L,  "Mi Funko 2", Modelo.ANIME, 55.0, LocalDate.now(),  LocalDateTime.now(), LocalDateTime.now());
        Funko funko2 = new Funko(null, UUID.randomUUID(), 1L,  "Mi Funko 2", Modelo.ANIME, 55.0, LocalDate.now(),  LocalDateTime.now(), LocalDateTime.now());

        repository.save(funko).get();
        Funko funko2_inserted = repository.save(funko2).get();

        assertAll(
                () -> assertTrue(repository.deleteById(funko2_inserted.getId()).get()),
                () -> assertFalse(repository.deleteById(8).get()),
                () -> assertTrue(repository.findAll().get().size() == 1)
        );

    }

    @Test
    void deleteAllTest() throws ExecutionException, InterruptedException, SQLException {
        Funko funko = new Funko(1, UUID.randomUUID(), 1L,  "Mi Funko 2", Modelo.ANIME, 55.0, LocalDate.now(),  LocalDateTime.now(), LocalDateTime.now());
        Funko funko2 = new Funko(2, UUID.randomUUID(), 1L,  "Mi Funko 2", Modelo.ANIME, 55.0, LocalDate.now(),  LocalDateTime.now(), LocalDateTime.now());

        repository.save(funko).get();
        repository.save(funko2).get();

        repository.deleteAll().get();
        assertTrue(repository.findAll().get().size() == 0);
    }

    @Test
    void findByNombre() throws ExecutionException, InterruptedException {
        Funko funko = new Funko(null, UUID.randomUUID(), 1L,  "Mi Funko 2", Modelo.ANIME, 55.0, LocalDate.now(),  LocalDateTime.now(), LocalDateTime.now());

        repository.save(funko).get();

        assertAll(
                () -> assertTrue(repository.findByNombre("Mi Funko 2").get().size() == 1),
                () -> assertTrue(repository.findByNombre("Daniel").get().isEmpty())
        );
    }



}
