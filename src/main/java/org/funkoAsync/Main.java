package org.funkoAsync;

import org.funkoAsync.models.Funko;
import org.funkoAsync.services.files.CsvManager;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Main {


    public static void main(String[] args) {
        CsvManager csvManager = new CsvManager();
        Path path = Path.of("");
        String path_directory = path.toAbsolutePath().toString() + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "data" + File.separator;
        String file = path_directory + "funkos.csv";
        System.out.println(file);
        try {
            List<Funko> funkos = csvManager.readCsv(file).get();

        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
