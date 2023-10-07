package org.funkoAsync;

import org.funkoAsync.enums.Modelo;
import org.funkoAsync.models.Funko;
import org.funkoAsync.repositories.funko.FunkoRepositoryImpl;
import org.funkoAsync.services.database.DataBaseManager;
import org.funkoAsync.services.files.CsvManager;
import org.funkoAsync.services.files.JsonManager;
import org.funkoAsync.services.funkos.FunkoCacheImpl;
import org.funkoAsync.services.funkos.FunkoServiceImpl;
import org.funkoAsync.services.storage.FunkoStorageServImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        FunkoServiceImpl funkoService = FunkoServiceImpl.getInstance(
                FunkoRepositoryImpl.getInstance(DataBaseManager.getInstance()),
                new FunkoCacheImpl(10),
                FunkoStorageServImpl.getInstance(
                        new CsvManager(),
                        new JsonManager()
                )
        );



        logger.info("IMPORTAMOS TODOS LOS FUNKOS A LA BASE DE DATOS");
        try {
            funkoService.importCsv().get(2, TimeUnit.MINUTES);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("La importación no se completo");
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            logger.error("La importacion del csv a base de datos supero el tiempo limite");
            throw new RuntimeException(e);
        }

        List<Funko> allFunkos;
        try {
            allFunkos = funkoService.findAll();
        } catch (SQLException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }


        var funkoMostExpensive = getFunkoMostExpensive(allFunkos);
        var avgPricesFunkos = getAvgPricesFunkos(allFunkos);
        var funkosGroupByModel = getFunkosGroupByModel(allFunkos);
        var countFunkosGroupByModel = getCountFunkosGroupByModel(allFunkos);
        var funksoRelease2003 = getFunkosRelease2023(allFunkos);
        var funkosWithNameStichANDCount= getFunkosWithNameStichANDCount(allFunkos);

        try {
            logger.info("FUNKO MAS CARO");
            logger.info("El funko mas caro es " + funkoMostExpensive.get());

            System.out.println();
            logger.info("Obteniendo la media del precio de todos los funkos");
            logger.info("La media de los precios de los funkos es: " + avgPricesFunkos.get());

            System.out.println();
            logger.info("Obteniendo los funkos agrupados por modelo");
            System.out.println(funkosGroupByModel.get());

            System.out.println();
            logger.info("Obteniendo el numero de funkos por modelo");
            System.out.println(countFunkosGroupByModel.get());

            System.out.println();
            logger.info("Obteniendo funkos lanzados en 2023");
            System.out.println(funksoRelease2003.get());

            System.out.println();
            logger.info("Obteniendo los diferentes funkos de stich con su conteo");
            System.out.println(funkosWithNameStichANDCount.get());

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        funkoService.stopCleaner();

    }


    private static CompletableFuture<Funko> getFunkoMostExpensive(List<Funko> allFunkos){
        return CompletableFuture.supplyAsync(() -> allFunkos.stream()
                    .max(Comparator.comparingDouble(Funko::getPrecio)).orElse(null));
    }

    private static CompletableFuture<Double> getAvgPricesFunkos(List<Funko> allFunkos){
        return CompletableFuture.supplyAsync(() -> allFunkos.stream()
                .mapToDouble(Funko::getPrecio)
                .average().orElse(0));
    }

    private static CompletableFuture<Map<Modelo, List<Funko>>> getFunkosGroupByModel(List<Funko> allFunkos){
        return CompletableFuture.supplyAsync(() -> allFunkos.stream()
                .collect(Collectors.groupingBy(Funko::getModelo)));
    }

    private static CompletableFuture<Map<Modelo, Long>> getCountFunkosGroupByModel(List<Funko> allFunkos){
        return CompletableFuture.supplyAsync(() -> allFunkos.stream()
                .collect(Collectors.groupingBy(Funko::getModelo, Collectors.counting())));
    }

    private static CompletableFuture<List<Funko>> getFunkosRelease2023(List<Funko> allFunkos){
        return CompletableFuture.supplyAsync(() -> allFunkos.stream()
                .filter(funko -> funko.getFecha().getYear() == 2023)
                .collect(Collectors.toList()));
    }

    private static CompletableFuture<Map<String, Long>> getFunkosWithNameStichANDCount(List<Funko> allFunkos){
        return CompletableFuture.supplyAsync(() -> allFunkos.stream()
                .filter(funko -> funko.getNombre().contains("Stitch"))
                .collect(Collectors.groupingBy(Funko::getNombre, Collectors.counting())));
    }



}
