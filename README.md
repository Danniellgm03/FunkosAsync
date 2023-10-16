# FunkoAsync

## Autor 
Daniel Garrido Muros

## Descripción
Este proyecto para aprender asincronia con un crud de funkos.
Y utilizando HikariCP


### Repositorio de funkos asincrono

En este proyecto como tenemos que utilizar asincronia para 
obtener los datos de la base de datos de manera asincrona deberemos adaptar
nuestro repositorio para que sea asincrono.

``` java
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
```


Ademas deberemos adaptar el manegador de base de datos para que se sincronicen los metodos
    
``` java
/**
     * Obtener la instancia de la clase Singleton
     * @return this
     */
    public synchronized static DataBaseManager getInstance(){
        if(instance == null){
            instance = new DataBaseManager();
        }
        return instance;
    }


    /**
     * Obtener las datos de la base de datos
     *
     */
    private synchronized void initConfig() {

        String propertiesFile = ClassLoader.getSystemResource("config.properties").getFile();
        Properties props = new Properties();

        try {
            props.load(new FileInputStream(propertiesFile));
            url = props.getProperty("database.url");
            initTables = props.getProperty("database.initTables").equals("true");
            url = props.getProperty("database.url");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Obtener la conexión con la base de datos
     * @return Connection
     * @throws SQLException
     */
    public synchronized Connection getConnection() throws SQLException {
        System.out.println(hikary);
        return hikary.getConnection();
    }

    /**
     * Clase para iniciar la base de datos y sus datos
     * @param sqlFile
     * @param conn
     */
    private synchronized void initData(String sqlFile, Connection conn)  {
        try {
            executeScript(conn, sqlFile, true);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Ejecutar un script de SQL
     * @param conn
     * @param file
     * @param logWriter
     * @throws FileNotFoundException
     */
    public synchronized void executeScript(Connection conn, String file, boolean logWriter) throws FileNotFoundException {
        ScriptRunner sr = new ScriptRunner(conn);
        logger.debug("Ejecutando script de SQL " + file);
        Reader reader = new BufferedReader(new FileReader(file));
        sr.setLogWriter(logWriter ? new PrintWriter(System.out) : null);
        sr.runScript(reader);
    }
```


## Servicio asincrono

Tambien tenemos el servicio asincrono que hace uso del repositorio de funkos
con cache, que deberemos adaptar el servicio y la cache 
para que sea asincrono

``` java
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
```


## Almacenamiento asincrono

Como ultima cosa por resaltar tenemos la clase storage que se encargar de 
hacer los backup y las importaciones de la base de datos

``` java

    /**
     * Método que se encarga de crear el directorio para almacenar los backups
     */
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

    /**
     * Método que se encarga de crear una instancia de la clase
     * @param csvManager
     * @param jsonManager
     */
    public static FunkoStorageServImpl getInstance(CsvManager csvManager, JsonManager jsonManager) {
        if(instance == null){
            instance = new FunkoStorageServImpl(csvManager, jsonManager);
        }
        return instance;
    }

    /**
     * Metodo que se encarga de exportar los datos a un archivo JSON
     * @param data
     */
    @Override
    public Boolean exportToJsonAsync(List<Funko> data) throws ExecutionException, InterruptedException {
        logger.debug("Exportando a datos a JSON");
        String name_file = File.separator + "backupJsonFunkos_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".json";
        return jsonManager.writeFunkosToJson(data, backupDirectory.toString() + name_file).get();
    }

    /**
     * Metodo que se encarga de importar los datos a un archivo CSV
     * @param filePath
     */
    @Override
    public List<Funko> importFromCsvAsync(String filePath) throws ExecutionException, InterruptedException {
        logger.debug("Importando datos desde csv");
        return csvManager.readCsv(filePath).get();
    }
```

