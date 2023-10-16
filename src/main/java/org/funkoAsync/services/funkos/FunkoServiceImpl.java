package org.funkoAsync.services.funkos;

import org.funkoAsync.exceptions.cache.CachePutNullKeyException;
import org.funkoAsync.exceptions.storage.ImportException;
import org.funkoAsync.models.Funko;
import org.funkoAsync.repositories.funko.FunkoRepository;
import org.funkoAsync.repositories.funko.FunkoRepositoryImpl;
import org.funkoAsync.services.storage.FunkoStorageServ;
import org.funkoAsync.services.storage.FunkoStorageServImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.server.ExportException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Clase que implementa la interfaz FunkoService
 * @see org.funkoAsync.services.funkos.FunkoService
 * @version 1.0
 * @author daniel
 */
public class FunkoServiceImpl implements FunkoService{

    private static FunkoServiceImpl instance;
    private Logger logger = LoggerFactory.getLogger(FunkoServiceImpl.class);

    private final FunkoRepository repository;

    private final FunkoCache cache;

    private final FunkoStorageServ storageFunko;

    /**
     * Constructor de la clase FunkoServiceImpl
     * @param repositoryFunko
     * @param cache
     * @param storageFunko
     */
    private FunkoServiceImpl(FunkoRepository repositoryFunko, FunkoCache cache, FunkoStorageServImpl storageFunko){
        this.repository = repositoryFunko;
        this.cache = cache;
        this.storageFunko = storageFunko;
    }

    /**
     * Metodo que devuelve una instancia de la clase FunkoServiceImpl
     * @param repositoryFunko
     * @param cache
     * @param storageFunko
     * @return
     */
    public static FunkoServiceImpl getInstance(FunkoRepository repositoryFunko, FunkoCache cache, FunkoStorageServImpl storageFunko){
        if(instance == null){
            instance = new FunkoServiceImpl(repositoryFunko, cache, storageFunko);
        }
        return instance;
    }

    /**
     * Metodo que devuelve todos los funkos de la base de datos
     * @return
     * @throws SQLException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Override
    public List<Funko> findAll() throws SQLException, ExecutionException, InterruptedException {
        logger.debug("Obteniendo todos los funkos");
        return repository.findAll().get();
    }

    /**
     * Metodo que devuelve todos los funkos de la base de datos por nombre
     * @param nombre
     * @return
     * @throws SQLException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Override
    public List<Funko> findByNombre(String nombre) throws SQLException, ExecutionException, InterruptedException {
        logger.debug("Obteniendo funkos por nombre");
        return repository.findByNombre(nombre).get();
    }

    /**
     * Metodo que devuelve todos los funkos de la base de datos por id
     * @param id
     * @return
     * @throws SQLException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Override
    public Optional<Funko> findById(Integer id) throws SQLException, ExecutionException, InterruptedException {
        Funko funko = cache.get(id);
        if(funko != null){
            logger.debug("Funko obtenido en cache");
            return Optional.of(funko);
        }else{
            logger.debug("Funko obtenido de la base de datos");
            return repository.findById(id).get();
        }
    }

    /**
     * Metodo que guarda un funko en la base de datos
     * @param funko
     * @return Funko
     */
    @Override
    public Funko save(Funko funko) throws SQLException, ExecutionException, InterruptedException {
        logger.debug("Insertamos funko en la base de datos");
        funko = repository.save(funko).get();
        try {
            cache.put(funko.getId(), funko);
        } catch (Exception  e) {
            logger.error("No se puede almacenar en la cache un funko con id null");
        }
        return funko;
    }

    /**
     * Metodo que actualiza un funko en la base de datos
     * @param funko
     * @return Funko
     */
    @Override
    public Funko update(Funko funko) throws SQLException, ExecutionException, InterruptedException {
        logger.debug("Actualiando funko");
        funko = repository.update(funko).get();
        try {
            cache.put(funko.getId(), funko);
        } catch (Exception e) {
            logger.error("No se puede almacenar en la cache un funko con id null");
        }
        return funko;
    }

    /**
     * Metodo que borra un funko de la base de datos por id
     * @param id
     * @return boolean
     */
    @Override
    public boolean deleteById(Integer id) throws SQLException, ExecutionException, InterruptedException {
        logger.debug("Borrando funko por id");
        boolean isDeleted = repository.deleteById(id).get();
        if(isDeleted){
            logger.debug("Funko borrado, borrando de cache");
            cache.delete(id);
        }
        return isDeleted;
    }

    /**
     * Metodo que borra todos los funkos de la base de datos
     */
    @Override
    public void deleteAll() throws SQLException, ExecutionException, InterruptedException {
        repository.deleteAll().get();
    }

    /**
     * Metodo que exporta los funkos de la base de datos a un fichero json
     */
    @Override
    public void backup() throws SQLException, ExecutionException, InterruptedException, ExportException {
        boolean isExported = storageFunko.exportToJsonAsync(this.findAll());
        if(!isExported){
            throw new ExportException("Error al exportar datos bd a json");
        }
    }

    /**
     * Metodo que importa los funkos de un fichero csv a la base de datos
     */
    @Override
    public CompletableFuture<List<Funko>> importCsv()  {
        Path path = Path.of("");
        String path_directory = path.toAbsolutePath().toString() + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "data" + File.separator;
        String file = path_directory + "funkos.csv";
        return CompletableFuture.supplyAsync(() -> {
            List<Funko> funkos_added = new ArrayList<>();
            try {
                List<Funko> funkos = storageFunko.importFromCsvAsync(file);
                if(funkos.isEmpty()){
                    throw new ImportException("No hay funkos para importar");
                }
                for (Funko funko: funkos) {
                    funkos_added.add(this.save(funko));
                }
            } catch (ExecutionException | InterruptedException | ImportException | SQLException e) {
                throw new RuntimeException(e);
            }
            return funkos_added;
        });
    }

    public void stopCleaner(){
        cache.shutdown();
    }

}
