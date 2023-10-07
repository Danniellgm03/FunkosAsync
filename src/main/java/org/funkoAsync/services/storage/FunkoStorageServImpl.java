package org.funkoAsync.services.storage;

import org.funkoAsync.models.Funko;
import org.funkoAsync.services.files.CsvManager;
import org.funkoAsync.services.files.JsonManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FunkoStorageServImpl implements FunkoStorageServ{

    private static FunkoStorageServImpl instance;

    private final CsvManager csvManager;
    private final JsonManager jsonManager;

    private File backupDirectory;

    private Logger logger = LoggerFactory.getLogger(FunkoStorageServImpl.class);

    private FunkoStorageServImpl(CsvManager csvManager, JsonManager jsonManager) {
        this.csvManager = csvManager;
        this.jsonManager = jsonManager;

        System.out.println("INIT");
        System.out.println(initDirectories());

    }

    private boolean initDirectories(){
        String absolute_path = Paths.get("").toAbsolutePath().toString();
        backupDirectory = new File(absolute_path + File.separator + "backups");
        boolean existDirectory = backupDirectory.exists();
        System.out.println(backupDirectory.toString());
        if(!existDirectory){
            logger.debug("Creamos directorio para backups");
            return backupDirectory.mkdirs();
        }
        return existDirectory;
    }

    public static FunkoStorageServImpl getInstance(CsvManager csvManager, JsonManager jsonManager) {
        if(instance == null){
            instance = new FunkoStorageServImpl(csvManager, jsonManager);
        }
        return instance;
    }

    @Override
    public Void exportToJsonAsync(List<Funko> data) throws ExecutionException, InterruptedException {
        logger.debug("Exportando a datos a JSON");
        String name_file = File.separator + "backupJsonFunkos_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".json";
        return jsonManager.writeFunkosToJson(data, backupDirectory.toString() + name_file).get();
    }

    @Override
    public List<Funko> importFromCsvAsync(String filePath) throws ExecutionException, InterruptedException {
        logger.debug("Importando datos desde csv");
        return csvManager.readCsv(filePath).get();
    }
}
