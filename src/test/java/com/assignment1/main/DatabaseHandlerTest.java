package com.assignment1.main;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseHandlerTest {
    static Properties props;
    static DatabaseHandler db;
    @Mock
    Connection conn;
    private AutoCloseable autoCloseable;

    @BeforeEach
    public void initDbHandler() {
        db = new DatabaseHandler();
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @BeforeAll
    public static void initializeProps() {
        String host = "localhost", port = "3306", user = "zaheer", password = "abcd";

        props = new Properties();
        props.setProperty("host", host);
        props.setProperty("port", port);
        props.setProperty("user", user);
        props.setProperty("password", password);
    }

    @AfterEach
    public void teardownInstance() throws Exception {
        autoCloseable.close();
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
        String correctString = "jdbc:mysql://localhost:3306/?user=zaheer&password=abcd";
        db.generateConnString(props);
        String generatedString = db.connString;
        assertEquals(generatedString, correctString);
    }
}