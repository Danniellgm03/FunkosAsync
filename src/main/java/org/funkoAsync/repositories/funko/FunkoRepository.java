package org.funkoAsync.repositories.funko;

import org.funkoAsync.models.Funko;
import org.funkoAsync.repositories.base.CrudRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface FunkoRepository extends CrudRepository<Funko, Integer, SQLException> {

    CompletableFuture<List<Funko>> findByNombre(String name) throws SQLException;

}
