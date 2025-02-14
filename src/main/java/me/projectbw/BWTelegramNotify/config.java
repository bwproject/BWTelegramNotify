package me.projectbw.BWTelegramNotify;

import java.io.File;

public class Config {
    private File configFile;
    private String botToken;
    private String chatId;

    public Config() {
        // Инициализация конфигурации
        this.configFile = new File("config.yml");
        loadConfig();
    }

    public void loadConfig() {
        // Реализуйте логику загрузки конфигурации из файла
        // Здесь можно использовать YML или JSON для хранения параметров
        botToken = "YOUR_BOT_TOKEN";
        chatId = "YOUR_CHAT_ID";
    }

    public String getBotToken() {
        return botToken;
    }

    public String getChatId() {
        return chatId;
    }
}