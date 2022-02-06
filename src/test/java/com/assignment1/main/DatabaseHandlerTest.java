package com.assignment1.main;

import org.junit.jupiter.api.*;
import org.mockito.MockitoAnnotations;


import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DatabaseHandlerTest {
    static Properties props;
    static DatabaseHandler db, dbTest;
    private CSVHandler csvHandler;
    private static List<String[]> resultList;
    private AutoCloseable autoCloseable;

    @BeforeEach
    public void initDbHandler() {
        db = new DatabaseHandler();
        autoCloseable = MockitoAnnotations.openMocks(this);
        csvHandler = mock(CSVHandler.class);
        resultList = new ArrayList<>();
    }

    @BeforeAll
    public static void initializeProps() throws SQLException {
        String host = "localhost", port = "3306", user = "root", password = "qurram", databasename = "Assignment1";

        props = new Properties();
        props.setProperty("host", host);
        props.setProperty("port", port);
        props.setProperty("user", user);
        props.setProperty("password", password);
        dbTest = new DatabaseHandler();
        dbTest.getConnection("jdbc:mysql://localhost:3306/?user=root&password=qurram");
        dbTest.createDatabase("testingDb");
        dbTest.createTable("testingTable", new String[]{"Heading1", "Heading2", "Heading3"});
    }

    @AfterEach
    public void teardownInstance() throws Exception {
        autoCloseable.close();
        resultList = null;
    }

    @Test
    public void getConnectionCorrect() {
        String connStr = "jdbc:mysql://localhost:3306/?" + "user=root&password=qurram";
        try {
            db.getConnection(connStr);
            assertNotNull(db.instanceConn);
        } catch (SQLException e) {
            fail("Test failed due to Exception");
        }
    }

    @Test
    public void getConnectionWrong() {
        String connStr = "jdbc:mysql://localhost:3306/?" + "user=root&password=zx";
        assertThrows(SQLException.class, () -> db.getConnection(connStr));
    }

    @Test
    public void closeConnectionCorrect() {
        String connStr = "jdbc:mysql://localhost:3306/?" + "user=root&password=qurram";
        try {
            db.getConnection(connStr);
            db.closeConnection();
            assertNull(db.instanceConn);
        } catch (SQLException e) {
            fail("Closing connection test failed");
        }
    }

    @Test
    public void getConnectionString() {
        String correctString = "jdbc:mysql://localhost:3306/?user=root&password=qurram";
        db.generateConnString(props);
        String generatedString = db.connString;
        assertEquals(generatedString, correctString);
    }

    @Test
    public void queryExecutionTest() {
        String query = "SELECT * FROM testingTable";
        List<String[]> result = null;
        dbTest.generateConnString(props, "testingDb");

        try {
            dbTest.getConnection(dbTest.connString);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try {
            result = dbTest.queryExecution(query);
            assertNotNull(result);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try {
            assert result != null;
            assertTrue(result.size() > 0);
        } catch (NullPointerException ex) {
            fail("Result is null");
        }
    }

    @Test
    public void queryExecutionTestNoRowsReturned() {
        String query = "alter table testingTable modify column Heading1 varchar(60)";
        List<String[]> result = null;
        dbTest.generateConnString(props, "testingDb");

        try {
            dbTest.getConnection(dbTest.connString);
        } catch (SQLException ex) {
            ex.printStackTrace();
            fail("Connection failed");
        }

        try {
            when(csvHandler.readData("results.csv")).thenReturn(resultList);
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Results file read failed");
        }
        try {
            result = dbTest.queryExecution(query);
            assertNotNull(result);
        } catch (SQLException ex) {
            ex.printStackTrace();
            fail("Query execution failed");
        }
        try {
            assertListEquals(csvHandler.readData("results.csv"), result);
        } catch (IOException e) {
            fail("File read failed");
        } catch (NullPointerException ex) {
            fail("Result is null");
        }
    }

    @Test
    public void queryExecutionTestInsert() {
        String query = "INSERT INTO testingTable (Heading1,Heading2,Heading3) VALUES ('title1','Rebecca White','Lion')";
        List<String[]> result = null;
        dbTest.generateConnString(props, "testingDb");

        try {
            dbTest.getConnection(dbTest.connString);
        } catch (SQLException ex) {
            ex.printStackTrace();
            fail("Connection failed");
        }

        try {
            result = dbTest.queryExecution(query);
            assertEquals(0, result.size());
        } catch (SQLException ex) {
            ex.printStackTrace();
            fail("Execution failed");
        }
    }


    private void assertListEquals(List<String[]> testList, List<String[]> innerDataList) {
        if (testList.size() != innerDataList.size())
            fail("Sizes not same, failed");
        System.out.println("Test");
        for (String[] r : testList) {
            System.out.println(Arrays.toString(r));
        }
        for (String[] r : innerDataList) {
            System.out.println(Arrays.toString(r));
        }
        for (int i = 0; i < testList.size(); i++) {
            String[] testRecord = testList.get(i);
            String[] dataRecord = innerDataList.get(i);
            assertArrayEquals(testRecord, dataRecord);
        }
    }
}