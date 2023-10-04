package org.funkoAsync.services.database;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.funkoAsync.services.files.CsvManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.*;
import java.util.Optional;
import java.util.Properties;

public class DataBaseManager {

    private static DataBaseManager instance;
    private Connection conn;

    private String url;
    private Boolean initTables;

    private PreparedStatement preparedStatement;
    Logger logger =  LoggerFactory.getLogger(DataBaseManager.class);

    private DataBaseManager(){
        initConfig();
    }

    public static DataBaseManager getInstance(){
        if(instance == null){
            instance = new DataBaseManager();
        }
        return instance;
    }

    private ResultSet executeQuery(String query, Object... params) throws SQLException {
        this.openConnection();
        preparedStatement = conn.prepareStatement(query);
        for(int i = 0; i < params.length; i++){
            preparedStatement.setObject(i + 1, params[i]);
        }
        return preparedStatement.executeQuery();
    }

    public Optional<ResultSet> select(String query, Object... params) throws SQLException {
        return Optional.of(executeQuery(query, params));
    }

    public int insert(String query, Object... params) throws SQLException {
        this.openConnection();
        preparedStatement = conn.prepareStatement(query);

        for(int i = 0; i < params.length; i++){
            preparedStatement.setObject(i + 1, params[i]);
        }

        return preparedStatement.executeUpdate();
    }



    private void initConfig() {

        String propertiesFile = ClassLoader.getSystemResource("config.properties").getFile();
        Properties props = new Properties();

        try {
            props.load(new FileInputStream(propertiesFile));
            url = props.getProperty("database.url");
            initTables = props.getProperty("database.initTables").equals("true");
            url = props.getProperty("database.url");

            if(initTables){
                String sqlFile = ClassLoader.getSystemResource("init.sql").getFile();
                this.initData(sqlFile, true);
            }

        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void openConnection() throws SQLException {
        if(conn != null && !conn.isClosed()){
            return;
        }
        logger.debug("Open connecion");
        conn = DriverManager.getConnection(url);
        System.out.println(conn);
    }

    private void initData(String sqlFile, boolean logWriter) throws SQLException, FileNotFoundException {
        this.openConnection();

        var sr = new ScriptRunner(conn);
        var reader = new BufferedReader(new FileReader(sqlFile));
        if(logWriter){
            sr.setLogWriter(new PrintWriter(System.out));
        }else{
            sr.setLogWriter(null);
        }
        sr.runScript(reader);

    }

}
