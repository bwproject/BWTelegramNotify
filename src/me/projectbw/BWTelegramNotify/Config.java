package me.projectbw.BWTelegramNotify;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private Properties properties = new Properties();

    public void load() {
        try (FileInputStream fis = new FileInputStream(new File("config.properties"))) {
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }
}
