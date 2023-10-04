package org.funkoAsync.services.files;


import org.funkoAsync.enums.Modelo;
import org.funkoAsync.models.Funko;
import org.funkoAsync.utils.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CsvManager {

    Logger logger = LoggerFactory.getLogger(CsvManager.class);
    public CompletableFuture<List<Funko>>  readCsv(String path) throws ExecutionException, InterruptedException {
        IdGenerator idGenerator = IdGenerator.getInstance();
        return CompletableFuture.supplyAsync(() -> {
            logger.debug("Reading csv file from path: " + path);
            List<Funko> funkos = new ArrayList<>();
            try {
                List<String> lines = Files.readAllLines(Path.of(path));

                lines.remove(0);
                for (String line: lines) {
                    String[] lines_split = line.split(",");

                    Funko funko = new Funko(
                            UUID.fromString(lines_split[0].substring(0, 35)),
                            idGenerator.getAndIncrementeId(),
                            lines_split[1],
                            Modelo.valueOf(lines_split[2]),
                            Double.parseDouble(lines_split[3]),
                            LocalDate.parse(lines_split[4]),
                            LocalDateTime.now(),
                            LocalDateTime.now()
                    );
                    funkos.add(funko);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return funkos;
        });
    }

}
