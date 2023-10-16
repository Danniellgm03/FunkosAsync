package org.funkoAsync.services.funkos;

import org.funkoAsync.models.Funko;
import org.funkoAsync.repositories.funko.FunkoRepository;

import java.rmi.server.ExportException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author daniel
 * Interfaz de FunkoService
 */
public interface FunkoService {

    /**
     * Método que devuelve todos los funkos
     */
    List<Funko> findAll() throws SQLException, ExecutionException, InterruptedException;

    /**
     * Método que devuelve todos los funkos que coincidan con el nombre
     * @param nombre
     */
    List<Funko> findByNombre(String nombre) throws SQLException, ExecutionException, InterruptedException;

    /**
     * Método que devuelve todos los funkos que coincidan con el id
     * @param id
     */
    Optional<Funko> findById(Integer id) throws SQLException, ExecutionException, InterruptedException;

    /**
     * Método que guarda un funko
     * @param funko
     */
    Funko save(Funko funko) throws SQLException, ExecutionException, InterruptedException;

    /**
     * Método que actualiza un funko
     * @param funko
     */
    Funko update(Funko funko) throws SQLException, ExecutionException, InterruptedException;

    /**
     * Método que elimina un funko por id
     * @param id

     */
    boolean deleteById(Integer id) throws SQLException, ExecutionException, InterruptedException;

    /**
     * Método que elimina todos los funkos
     */
    void deleteAll() throws SQLException, ExecutionException, InterruptedException;

    /**
     * Método que devuelve el número de funkos
     */
    void backup() throws SQLException, ExecutionException, InterruptedException, ExportException;

    /**
     * Método que importa los funkos desde un csv
     */
    CompletableFuture<List<Funko>> importCsv() throws ExecutionException, InterruptedException;

}
