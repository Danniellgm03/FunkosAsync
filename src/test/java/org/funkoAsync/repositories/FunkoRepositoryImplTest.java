package org.funkoAsync.repositories;

import org.funkoAsync.repositories.funko.FunkoRepositoryImpl;
import org.funkoAsync.services.database.DataBaseManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class FunkoRepositoryImplTest {


    private DataBaseManager db;

    private FunkoRepositoryImpl repository;

    @BeforeAll
    void setUp() {
        db = DataBaseManager.getInstance();
        repository = FunkoRepositoryImpl.getInstance(db);
    }

    @BeforeEach
    void tearDown() throws SQLException, ExecutionException, InterruptedException {
        repository.deleteAll().get();
    }

    @Test
    void findAll() throws SQLException, ExecutionException, InterruptedException {


    }

}
