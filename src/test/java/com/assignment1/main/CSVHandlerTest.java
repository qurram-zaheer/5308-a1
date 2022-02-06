package com.assignment1.main;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CSVHandlerTest {
    private CSVHandler csvHandler;
    List<String[]> dataList;
    static List<String[]> testList1;

    @BeforeEach
    public void setUp() {
        csvHandler = new CSVHandler();
    }

    @BeforeAll
    static void initTestArray() {
        testList1 = new ArrayList<>();
        testList1.add(new String[]{"Header 1", "Header 2", "Header 3"});
        testList1.add(new String[]{"a", "b", "c"});
    }

    @AfterEach
    public void teardown() {
        csvHandler = null;
    }

    @Test
    public void testCsvIntoList() {
        try {
            dataList = csvHandler.readData("test.csv");
            assertListEquals(testList1, dataList);
        } catch (IOException e) {
            fail("Exception in reading file");
        }
    }

    @Test
    public void testEmptyCsv() {
        assertThrows(IndexOutOfBoundsException.class, () -> csvHandler.readData("emptyCsvTest.csv"));
    }

    @Test
    public void whiteSpaceCsv() {
        assertThrows(IndexOutOfBoundsException.class, () -> csvHandler.readData("whitespaceRows.csv"));
    }

    @Test
    public void blankHeaderCsv() {
        assertThrows(IOException.class, () -> csvHandler.readData("blankHeader.csv"));
    }

    private void assertListEquals(List<String[]> testList, List<String[]> innerDataList) {
        if (testList.size() != innerDataList.size())
            fail("Sizes not same, failed");
        for (int i = 0; i < testList.size(); i++) {
            String[] testRecord = testList.get(i);
            String[] dataRecord = innerDataList.get(i);
            assertArrayEquals(testRecord, dataRecord);
        }
    }
}

