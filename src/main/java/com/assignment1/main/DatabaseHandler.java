package com.assignment1.main;

import java.io.IOException;
import java.sql.*;
import java.util.*;

public class DatabaseHandler {
    Connection instanceConn = null;
    String connString;
    String instanceDbName;
    CSVHandler csvHandler;


    public void getConnection(String inpConnString) throws SQLException {
        System.out.println("Connection String: " + inpConnString);
        instanceConn = DriverManager.getConnection(inpConnString);
        connString = inpConnString;
    }

    public void generateConnString(Properties props) {
        generateConnString(props, "");
    }

    public void generateConnString(Properties props, String databasename) {
        instanceDbName = databasename;
        connString = String.format("jdbc:mysql://%s:%s/%s?user=%s&password=%s", props.getProperty("host"), props.getProperty("port"), databasename, props.getProperty("user"), props.getProperty("password"));
    }


    public void closeConnection() {
        try {
            if (null != instanceConn) {
                instanceConn.close();
                instanceConn = null;
                System.out.println("Success: Connection closed");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private boolean checkDatabaseExistence(String dbName) {
        ResultSet rs;
        try {
            rs = instanceConn.getMetaData().getCatalogs();
            while (rs.next()) {
                String foundName = rs.getString(1);
                if (foundName.equals(dbName.toLowerCase())) {
                    return true;
                }
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void dropDatabase(String dbName) {
        String query = "DROP DATABASE " + dbName;
        try {
            Statement stmt = instanceConn.createStatement();
            stmt.executeUpdate(query);
            System.out.println("Warning: Existing database overwritten");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void createDatabase(String inpName) {
        if (checkDatabaseExistence(inpName)) {
            dropDatabase(inpName);
        }

        try {
            Statement stmt = instanceConn.createStatement();
            String creationQuery = "CREATE DATABASE " + inpName;
            stmt.executeUpdate(creationQuery);
            instanceDbName = inpName;
            System.out.println("Success: Database creation");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private boolean checkTableExistence(String tableName) {
        try {
            DatabaseMetaData md = instanceConn.getMetaData();
            ResultSet rs = md.getTables(null, instanceDbName, null, new String[]{"TABLE"});
            while (rs.next()) {
                String name = rs.getString("TABLE_NAME");
                if (name.equals(tableName.toLowerCase())) {
                    return true;
                }
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void dropTable(String tableName) {
        String query = "DROP TABLE " + tableName;
        try {
            Statement stmt = instanceConn.createStatement();
            stmt.executeUpdate(query);
            System.out.println("Warning: Existing table overwritten");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTable(String tableName, String[] headers) {
        if (checkTableExistence(tableName)) {
            dropTable(tableName);
        }
        StringBuilder sb = new StringBuilder("CREATE TABLE " + instanceDbName + "." + tableName + " (" +
                "id INTEGER NOT NULL AUTO_INCREMENT,");

        for (String header : headers) {
            sb.append(header).append(" varchar(40), ");
        }
        // Since MySQL usage has been mentioned under assumptions
        sb.append("PRIMARY KEY (id))");
        String query = sb.toString();
        try {
            Statement stmt = instanceConn.createStatement();
            stmt.executeUpdate(query);
            System.out.println("Success: Table " + tableName + " creation");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void populateTable(String tableName, List<String[]> data) {
        try {
            instanceConn.setAutoCommit(false);
            StringBuilder sql = new StringBuilder("INSERT INTO " + instanceDbName + "." + tableName + " VALUES ");
            StringJoiner sj = new StringJoiner(",", "(", ")");
            for (int i = 0; i < data.get(0).length + 1; i++) {
                sj.add("?");
            }
            sql.append(sj);
            String templateString = sql.toString();

            PreparedStatement pstmt = instanceConn.prepareStatement(templateString);

            for (String[] record : data) {
                pstmt.setString(1, null);
                for (int i = 0; i < record.length; i++) {
                    pstmt.setString(i + 2, record[i]);
                }
                pstmt.addBatch();
            }

            pstmt.executeBatch();
            instanceConn.commit();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public List<String[]> queryExecution(String query) throws SQLException {
        List<String[]> resultList = new ArrayList<>();

        Statement stmt = instanceConn.createStatement();
        boolean isResultSet = stmt.execute(query);
        if (isResultSet) {
            ResultSet rs = stmt.getResultSet();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            String[] row = new String[columnCount];

            for (int i = 1; i <= columnCount; i++) {
                row[i - 1] = metaData.getColumnName(i);
            }
            resultList.add(row);

            while (rs.next()) {
                String[] temp = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    temp[i - 1] = rs.getString(i);
                }
                resultList.add(temp);
                temp = null;
            }
        }
        return resultList;
    }

    public void queryDriver(String query) throws SQLException, IOException {
        csvHandler = new CSVHandler();
        List<String[]> resultList = queryExecution(query);
        csvHandler.writeListToCSV(resultList);
    }

}