package me.projectbw;

import org.bukkit.configuration.file.FileConfiguration;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TelegramSender {
    private String botToken;
    private String chatId;

    // Конструктор для Paper
    public TelegramSender(FileConfiguration config) {
        this.botToken = config.getString("telegram.bot_token", "");
        this.chatId = config.getString("telegram.chat_id", "");
    }

    // Конструктор для Velocity (без конфигурации)
    public TelegramSender() {
        this.botToken = "ВАШ_ТОКЕН_БОТА"; // Укажите токен бота вручную, если нет конфига
        this.chatId = "ВАШ_CHAT_ID"; // Укажите ID чата/группы
    }

    public void sendMessage(String message) {
        try {
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/sendMessage";
            String jsonPayload = "{\"chat_id\":\"" + chatId + "\",\"text\":\"" + message + "\"}";

            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
            }

            connection.getResponseCode(); // Отправляем запрос
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}