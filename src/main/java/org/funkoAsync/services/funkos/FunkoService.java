package org.funkoAsync.services.funkos;

import org.funkoAsync.models.Funko;
import org.funkoAsync.repositories.funko.FunkoRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface FunkoService {

    List<Funko> findAll() throws SQLException, ExecutionException, InterruptedException;

    List<Funko> findByNombre(String nombre) throws SQLException, ExecutionException, InterruptedException;

    Optional<Funko> findById(Integer id) throws SQLException, ExecutionException, InterruptedException;

    Funko save(Funko funko) throws SQLException, ExecutionException, InterruptedException;

    Funko update(Funko funko) throws SQLException, ExecutionException, InterruptedException;

    boolean deleteById(Integer id) throws SQLException, ExecutionException, InterruptedException;

    void deleteAll() throws SQLException, ExecutionException, InterruptedException;

    void backup() throws SQLException, ExecutionException, InterruptedException;

    CompletableFuture<List<Funko>> importCsv() throws ExecutionException, InterruptedException;

}
