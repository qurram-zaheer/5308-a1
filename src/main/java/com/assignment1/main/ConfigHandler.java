package com.assignment1.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigHandler {
    public Properties readData(String filename) {
        Properties props = new Properties();
        try {
            String configPath = filename;
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream is = classLoader.getResourceAsStream(configPath);
            props.load(is);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return props;
    }
}
