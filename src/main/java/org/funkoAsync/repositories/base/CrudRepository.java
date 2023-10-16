package org.funkoAsync.repositories.base;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * CrudRepository con genericos, interfaz para un crud asincrono
 * @param <T>
 * @param <ID>
 * @param <EX>
 */
public interface CrudRepository<T, ID, EX extends Exception> {

    /**
     * Guarda un T en la base de datos
     * @param t
     * @return CompletableFuture<T>
     * @throws SQLException
     * @throws EX
     */
    CompletableFuture<T> save(T t) throws SQLException, EX;

    /**
     * Actualiza un T en la base de datos
     * @param t
     * @return CompletableFuture<T>
     * @throws SQLException
     * @throws EX
     */
    CompletableFuture<T> update(T t) throws SQLException, EX;

    /**
     * Busca un T por su id
     * @param id
     * @return CompletableFuture<Optional<T>>
     * @throws SQLException
     */
    CompletableFuture<Optional<T>> findById(ID id) throws SQLException;

    /**
     * Busca todos los T de la base de datos
     * @return CompletableFuture<List<T>>
     * @throws SQLException
     */
    CompletableFuture<List<T>> findAll() throws SQLException;

    /**
     * Elimina un T por su id
     * @param id
     * @return CompletableFuture<Boolean>
     * @throws SQLException
     */
    CompletableFuture<Boolean> deleteById(ID id) throws SQLException;

    /**
     * Elimina todos los T de la base de datos
     * @return CompletableFuture<Void>
     * @throws SQLException
     */
    CompletableFuture<Void> deleteAll() throws SQLException;

}
