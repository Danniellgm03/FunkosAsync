package org.funkoAsync.repositories.base;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface CrudRepository<T, ID, EX extends Exception> {

    CompletableFuture<T> save(T t) throws SQLException, EX;

    CompletableFuture<T> update(T t) throws SQLException, EX;

    CompletableFuture<Optional<T>> findById(ID id) throws SQLException;

    CompletableFuture<List<T>> findAll() throws SQLException;

    CompletableFuture<Boolean> deleteById(ID id) throws SQLException;

    CompletableFuture<Void> deleteAll() throws SQLException;

}
