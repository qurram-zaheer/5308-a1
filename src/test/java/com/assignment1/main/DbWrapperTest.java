package com.assignment1.main;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class DbWrapperTest {
    private ConfigHandler mockHandler;
    private Properties props, returnProps;
    private DatabaseHandler mockDbHandler;

    @Mock
    private List<String[]> dataList;

    @BeforeEach
    void firstSetup() {
        props = new Properties();
        props.setProperty("databasename", "value1");
        props.setProperty("key2", "value2");
    }

    @BeforeEach
    void setUp() {
        mockHandler = mock(ConfigHandler.class);
        returnProps = new Properties();
        mockDbHandler = mock(DatabaseHandler.class);
        dataList = new ArrayList<>();
        dataList.add(new String[]{"a", "b", "c"});
        dataList.add(new String[]{"d", "e", "f"});
    }

    @AfterEach
    void tearDown() {
        mockHandler = null;
        mockDbHandler = null;
    }

    @Test
    void getConfig() {
        when(mockHandler.readData(anyString())).thenReturn(props);

        DbWrapper dbWrapper = new DbWrapper();
        returnProps = DbWrapper.getConfig(mockHandler);
        verify(mockHandler, times(1)).readData("config.properties");
    }

    @Test
    void databaseOps() {
        DbWrapper dbWrapper = new DbWrapper();

        DbWrapper.databaseOps("test", props, mockDbHandler, dataList);

        verify(mockDbHandler, times(1)).createDatabase(props.getProperty("databasename"));
        verify(mockDbHandler, times(1)).createTable("test", new String[]{"a", "b", "c"});
        verify(mockDbHandler, times(1)).populateTable("test", dataList);
    }
}

