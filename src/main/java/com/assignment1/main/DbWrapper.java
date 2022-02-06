package com.assignment1.main;

import java.io.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class DbWrapper {

    public static Properties getConfig(ConfigHandler handler) {
        return handler.readData("config.properties");
    }

    public static List<String[]> fileHandler(String filename) {
        CSVHandler handler = new CSVHandler();
        List<String[]> data = null;
        try {
            data = handler.readData(filename + ".csv");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        assert data != null;
        for (String[] record : data) {
            System.out.println(Arrays.toString(record));
        }
        return data;
    }

    public static void databaseOps(String filename, Properties props, DatabaseHandler db, List<String[]> data) {
        String[] headers = data.get(0);
        data.remove(0);

        db.createDatabase(props.getProperty("databasename"));
        db.createTable(filename, headers);
        db.populateTable(filename, data);
    }

    public static void init(String filename, Properties props, DatabaseHandler db) {
        List<String[]> data = fileHandler(filename);
        databaseOps(filename, props, db, data);
    }

    public static void query(String query, DatabaseHandler db) throws SQLException, IOException {
        System.out.println(query);
        db.queryDriver(query);
    }

    public static void main(String[] args) throws SQLException {
        ConfigHandler handler = new ConfigHandler();
        Properties properties = getConfig(handler);

        DatabaseHandler db = new DatabaseHandler();
        String connStr;


        String command = args[0];
        switch (command) {
            case "init" -> {
                db.generateConnString(properties);
                connStr = db.connString;
                db.getConnection(connStr);
                init(args[1], properties, db);
            }
            case "query" -> {
                db.generateConnString(properties, args[1]);
                connStr = db.connString;
                db.getConnection(connStr);
                try {
                    query(args[2], db);
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                }
            }
            default -> {
                System.out.println("Please enter appropriate command");
                System.exit(0);
            }
        }
    }
}
