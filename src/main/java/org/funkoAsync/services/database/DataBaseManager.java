package org.funkoAsync.services.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.funkoAsync.services.files.CsvManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.*;
import java.util.Optional;
import java.util.Properties;

/**
 * DataBaseManager
 * Clase que gestiona la conexión con la base de datos
 * @author daniel
 */
public class DataBaseManager {

    private static DataBaseManager instance;
    private Connection conn;

    private String url;
    private Boolean initTables;

    private PreparedStatement preparedStatement;

    private HikariDataSource hikary;
    Logger logger =  LoggerFactory.getLogger(DataBaseManager.class);

    private  DataBaseManager(){
        initConfig();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        hikary = new HikariDataSource(config);
        try {
            conn = hikary.getConnection();

           if(initTables){
                String sqlFile = ClassLoader.getSystemResource("init.sql").getFile();
                this.initData(sqlFile, conn);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

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

}
