package org.funkoAsync.services.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.funkoAsync.models.Funko;
import org.funkoAsync.utils.adapters.LocalDateAdapter;
import org.funkoAsync.utils.adapters.LocalDateTimeAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Clase que se encarga de escribir los funkos en un json
 * @author daniel
 * @see Funko
 */
public class JsonManager {

    private Logger logger = LoggerFactory.getLogger(JsonManager.class);

    /**
     * Metodo que se encarga de escribir una lista de funkos en un json
     * @param funkos
     * @param path_output
     * @return
     */
    public CompletableFuture<Boolean> writeFunkosToJson(List<Funko> funkos, String path_output)  {
        return CompletableFuture.supplyAsync(() -> {
            if(funkos == null){
                logger.error("No se puede exportar funkos porque es null");
                return false;
            }
            if(Files.exists(Path.of(path_output))){
                logger.error("No se puede expotar funkos a " + path_output + " porque no existe");
                return false;
            }

            logger.debug("Escribiendo funkos en un json");
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .setPrettyPrinting()
                    .create();
            String json = gson.toJson(funkos);
            try {
                Path path_new_file = Files.writeString(new File(path_output).toPath(), json);
                return Files.exists(path_new_file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
