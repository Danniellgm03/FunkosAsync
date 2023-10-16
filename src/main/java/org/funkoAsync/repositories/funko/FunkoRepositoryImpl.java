package org.funkoAsync.repositories.funko;

import org.funkoAsync.enums.Modelo;
import org.funkoAsync.exceptions.funko.FunkoNoEncontradoException;
import org.funkoAsync.exceptions.funko.FunkoNoGuardado;
import org.funkoAsync.models.Funko;
import org.funkoAsync.services.database.DataBaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Implementacion de FunkoRepository
 * @see FunkoRepository
 * @see org.funkoAsync.repositories.base.CrudRepository
 * @see DataBaseManager
 * @author daniel
 */
public class FunkoRepositoryImpl  implements FunkoRepository{

    private static FunkoRepositoryImpl instance;
    private final DataBaseManager db;

    Logger logger = LoggerFactory.getLogger(FunkoRepositoryImpl.class);

    private FunkoRepositoryImpl(DataBaseManager db){
        this.db = db;
    }

    /**
     * Singleton de FunkoRepositoryImpl
     * @param db
     * @return this
     */
    public static FunkoRepositoryImpl getInstance(DataBaseManager db){
        if(instance == null){
            instance = new FunkoRepositoryImpl(db);
        }
        return instance;
    }

    /**
     * Guarda un funko en la base de datos
     * @param funko
     */
    @Override
    public CompletableFuture<Funko> save(Funko funko) {
        return CompletableFuture.supplyAsync(() -> {
            if(funko != null){
                String query = "INSERT INTO funkos (cod, myId, name, model, price, release_date) VALUES(?,?,?,?,?,?)";
                try(
                        Connection conn = db.getConnection();
                        PreparedStatement prepare = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ) {
                    logger.debug("Insertando el funko: " + funko);
                    prepare.setObject(1, funko.getCOD());
                    prepare.setObject(2, funko.getMyId());
                    prepare.setObject(3, funko.getNombre());
                    prepare.setObject(4, funko.getModelo().toString());
                    prepare.setObject(5, funko.getPrecio());
                    prepare.setObject(6, funko.getFecha());

                    int res = prepare.executeUpdate();
                    if(res > 0){
                        logger.debug("Funko insertado");
                        ResultSet gKeys = prepare.getGeneratedKeys();
                        while(gKeys.next()){
                            funko.setId(gKeys.getInt("id"));
                        }
                    } else{
                        logger.error("Funko no insertado");
                        throw new FunkoNoGuardado("Error al insertar el funko");
                    }
                } catch (SQLException| FunkoNoGuardado e) {
                    logger.debug("Error al insertar funko");

                }
                return funko;
            }else{
                logger.error("Funko no insertado, funko is null");
                return null;
            }

        });
    }

    /**
     * Actualiza un funko en la base de datos
     * @param funko
     * @throws SQLException
     */
    @Override
    public CompletableFuture<Funko> update(Funko funko) throws SQLException, SQLException {
        return CompletableFuture.supplyAsync(() -> {
            if(funko != null){
                String query = "UPDATE funkos SET name = ?, model = ?, price = ?, release_date = ?, updated_at = ? WHERE id = ?";
                try(
                        Connection conn = db.getConnection();
                        PreparedStatement prepare = conn.prepareStatement(query);
                ){
                    logger.debug("Actualizando funko con id: " + funko.getId());
                    prepare.setObject(1, funko.getNombre());
                    prepare.setObject(2, funko.getModelo().toString());
                    prepare.setObject(3, funko.getPrecio());
                    prepare.setObject(4, funko.getFecha());
                    prepare.setObject(5, LocalDateTime.now());
                    prepare.setObject(6, funko.getId());
                    int res = prepare.executeUpdate();
                    if(res > 0){
                        logger.debug("Funko actualizado id:" + funko.getId());
                        return this.findById(funko.getId()).get().get();
                    }else{
                        logger.error("Error al actualizar funko");
                        throw new FunkoNoEncontradoException("Funko no encontrado con el id: " + funko.getId());
                    }
                } catch (SQLException e) {
                    logger.error("Error al actualizar funko: " + funko.getId());
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }

                return funko;
            }else{
                logger.error("El funko no se pudo actulizar: funko es null");
                return null;
            }

        });
    }

    /**
     * Busca un funko por su id
     * @param id
     * @throws SQLException
     */
    @Override
    public CompletableFuture<Optional<Funko>> findById(Integer id) throws SQLException {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Funko> funko = Optional.empty();
            String query = "SELECT * FROM funkos WHERE id = ?";

            try(
                    Connection conn = db.getConnection();
                    PreparedStatement prepare = conn.prepareStatement(query);
            ){
                logger.debug("Obteniendo el funko con id: " + id);
                prepare.setObject(1, id);
                ResultSet res = prepare.executeQuery();
                while(res.next()){
                    funko = Optional.of(new Funko(
                            res.getInt("id"),
                            UUID.fromString(res.getString("cod")),
                            res.getLong("myId"),
                            res.getString("name"),
                            Modelo.valueOf(res.getString("model")),
                            res.getDouble("price"),
                            LocalDate.parse(res.getString("release_date")),
                            new Timestamp(res.getTimestamp("created_at").getTime()).toLocalDateTime(),
                            new Timestamp(res.getTimestamp("updated_at").getTime()).toLocalDateTime()
                    ));

                }
            } catch (SQLException e) {
                logger.error("Error al buscar el funko con id: " + id);
            }

            return funko;
        });
    }

    /**
     * Busca todos los funkos de la base de datos
     * @throws SQLException
     */
    @Override
    public CompletableFuture<List<Funko>> findAll() throws SQLException {
        return CompletableFuture.supplyAsync(() -> {
            List<Funko> funkos = new ArrayList<>();
            String query = "SELECT * FROM funkos";

            try(
                    Connection conn = db.getConnection();
                    PreparedStatement prepare = conn.prepareStatement(query);
            ){
                logger.debug("Obteniendo todos los funkos");
                ResultSet res = prepare.executeQuery();
                while(res.next()){
                    Funko funko = new Funko(
                        res.getInt("id"),
                        UUID.fromString(res.getString("cod")),
                        res.getLong("myId"),
                        res.getString("name"),
                        Modelo.valueOf(res.getString("model")),
                        res.getDouble("price"),
                        LocalDate.parse(res.getString("release_date")),
                        new Timestamp(res.getTimestamp("created_at").getTime()).toLocalDateTime(),
                        new Timestamp(res.getTimestamp("updated_at").getTime()).toLocalDateTime()
                    );

                    funkos.add(funko);
                }
            } catch (SQLException e) {
                logger.error("Error al buscar todos los funkos");
            }

            return funkos;
        });
    }

    /**
     * Elimina un funko por su id
     * @param id
     * @throws SQLException
     */
    @Override
    public CompletableFuture<Boolean> deleteById(Integer id) throws SQLException {
        return CompletableFuture.supplyAsync(() -> {
            String query = "DELETE FROM funkos WHERE id = ?";
            boolean isDeleted = false;
            try(
                    Connection conn = db.getConnection();
                    PreparedStatement prepare = conn.prepareStatement(query);
            ){
                logger.debug("Borrando funko con id: " + id);
                prepare.setObject(1, id);
                isDeleted =  prepare.executeUpdate() == 1;
            } catch (SQLException e) {
                logger.error("Error al borrar el funko con id: " + id);
            }
            return isDeleted;
        });
    }

    /**
     * Elimina todos los funkos de la base de datos
     * @throws SQLException
     */
    @Override
    public CompletableFuture<Void> deleteAll() throws SQLException {
        return CompletableFuture.runAsync(() -> {
           String query = "DELETE FROM funkos";

           try(
                Connection conn = db.getConnection();
                PreparedStatement prepare = conn.prepareStatement(query);
           ){
               logger.debug("Borrando todos los funkos de base de datos");
               prepare.executeUpdate();
           } catch (SQLException e) {
                logger.error("Error al borrar todos los funkos de la base de datos");
           }
        });
    }

    /**
     * Busca un funko por su nombre
     * @param name
     * @throws SQLException
     */
    @Override
    public CompletableFuture<List<Funko>> findByNombre(String name) throws SQLException {
        return CompletableFuture.supplyAsync(() -> {
            List<Funko> funkos = new ArrayList<>();
            String query = "SELECT * FROM funkos WHERE name LIKE ?";

            try(
                    Connection conn = db.getConnection();
                    PreparedStatement prepare = conn.prepareStatement(query);
            ){
                logger.debug("Obteniendo los funkos con nombre: " + name);
                prepare.setObject(1, name);
                ResultSet res = prepare.executeQuery();

                while(res.next()){
                    Funko funko = new Funko(
                        res.getInt("id"),
                        UUID.fromString(res.getString("cod")),
                        res.getLong("myId"),
                        res.getString("name"),
                        Modelo.valueOf(res.getString("model")),
                        res.getDouble("price"),
                        LocalDate.parse(res.getString("release_date")),
                        new Timestamp(res.getTimestamp("created_at").getTime()).toLocalDateTime(),
                        new Timestamp(res.getTimestamp("updated_at").getTime()).toLocalDateTime()
                    );

                    funkos.add(funko);
                }

            } catch (SQLException e) {
                logger.error("Error al obtener los funkos con nombre: " + name);
            }
            return funkos;
        });
    }
}
