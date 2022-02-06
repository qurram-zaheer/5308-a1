package com.assignment1.main;

import java.io.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class DbWrapper {

    public static Properties getConfig() {
        ConfigHandler handler = new ConfigHandler();
        Properties properties = handler.readData("config.properties");
        return properties;
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

    public static void main(String[] args) throws SQLException {
        Properties properties = getConfig();

        DatabaseHandler db = new DatabaseHandler();
        db.generateConnString(properties);
        String connStr = db.connString;
        db.getConnection(connStr);

        String command = args[0];
        switch (command) {
            case "init":
                init(args[1], properties, db);
            case "query":
                System.out.println("query");
        }
    }
}
