package me.projectbw.BWTelegramNotify;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Config {

    private final File configFile;
    private final Properties properties;

    private String telegramBotToken;
    private String telegramChatId;
    private int tpsWarningThreshold;
    private int tpsCheckInterval;
    private boolean enableLogging;

    public Config(File dataFolder) {
        this.configFile = new File(dataFolder, "config.properties");
        this.properties = new Properties();

        // Загружаем конфигурацию при запуске плагина
        loadConfig();
    }

    private void loadConfig() {
        if (!configFile.exists()) {
            // Если конфиг не существует, создаем его с дефолтными значениями
            setDefaults();
            saveConfig();
        } else {
            try {
                properties.load(Files.newBufferedReader(configFile.toPath()));
                telegramBotToken = properties.getProperty("telegramBotToken", "YOUR_BOT_TOKEN");
                telegramChatId = properties.getProperty("telegramChatId", "YOUR_CHAT_ID");
                tpsWarningThreshold = Integer.parseInt(properties.getProperty("tpsWarningThreshold", "18"));
                tpsCheckInterval = Integer.parseInt(properties.getProperty("tpsCheckInterval", "5"));
                enableLogging = Boolean.parseBoolean(properties.getProperty("enableLogging", "true"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setDefaults() {
        // Устанавливаем дефолтные значения
        properties.setProperty("telegramBotToken", "YOUR_BOT_TOKEN");
        properties.setProperty("telegramChatId", "YOUR_CHAT_ID");
        properties.setProperty("tpsWarningThreshold", "18");
        properties.setProperty("tpsCheckInterval", "5");
        properties.setProperty("enableLogging", "true");
    }

    public void saveConfig() {
        try {
            properties.store(Files.newBufferedWriter(configFile.toPath()), "BWTelegramNotify Configuration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Геттеры для параметров конфигурации
    public String getTelegramBotToken() {
        return telegramBotToken;
    }

    public String getTelegramChatId() {
        return telegramChatId;
    }

    public int getTpsWarningThreshold() {
        return tpsWarningThreshold;
    }

    public int getTpsCheckInterval() {
        return tpsCheckInterval;
    }

    public boolean isEnableLogging() {
        return enableLogging;
    }

    // Утилиты для изменения конфигурации во время работы
    public void setTelegramBotToken(String token) {
        this.telegramBotToken = token;
        properties.setProperty("telegramBotToken", token);
        saveConfig();
    }

    public void setTelegramChatId(String chatId) {
        this.telegramChatId = chatId;
        properties.setProperty("telegramChatId", chatId);
        saveConfig();
    }

    public void setTpsWarningThreshold(int threshold) {
        this.tpsWarningThreshold = threshold;
        properties.setProperty("tpsWarningThreshold", String.valueOf(threshold));
        saveConfig();
    }

    public void setTpsCheckInterval(int interval) {
        this.tpsCheckInterval = interval;
        properties.setProperty("tpsCheckInterval", String.valueOf(interval));
        saveConfig();
    }

    public void setEnableLogging(boolean enableLogging) {
        this.enableLogging = enableLogging;
        properties.setProperty("enableLogging", String.valueOf(enableLogging));
        saveConfig();
    }
}
