package com.assignment1.main;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

public class CSVHandler {
    public List<String[]> readData(String filename) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(filename);
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + filename);
        }

        return readIntoList(inputStream);
    }

    private static List<String[]> readIntoList(InputStream is) throws IOException {
        List<String[]> data = new ArrayList<>();
        InputStreamReader streamReader =
                new InputStreamReader(is, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.equals(""))
                continue;

            String[] lineArr = line.strip().split(",");

            for (int j = 0; j < lineArr.length; j++) {
                lineArr[j] = lineArr[j].strip();
            }
            data.add(lineArr);
        }
        if (data.size() <= 1) {
            throw new IndexOutOfBoundsException("Input CSV needs at least one header row and one data row");
        }
        for (String headerRecord : data.get(0)) {
            if (headerRecord.equals(""))
                throw new IOException("Blank headers not allowed in CSV");
        }
        return data;
    }

    public void writeListToCSV(List<String[]> resultList) throws IOException {
        FileWriter file = new FileWriter("results.csv");
        PrintWriter write = new PrintWriter(file);
        StringBuilder sb = new StringBuilder();
        for (String[] record : resultList) {
            StringJoiner sj = new StringJoiner(",");
            for (String r : record) {
                sj.add(r);
            }
            sb.append(sj.toString()).append("\n");
        }
        write.write(sb.toString());
        write.close();
        file.close();
    }
}
