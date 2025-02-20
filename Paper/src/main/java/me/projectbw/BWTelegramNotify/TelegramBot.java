package me.projectbw.BWTelegramNotify;

import org.bukkit.Bukkit;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TelegramBot {
    private final String botToken;
    private final String chatId;

    public TelegramBot(String botToken, String chatId) {
        this.botToken = botToken;
        this.chatId = chatId;
    }

    public void sendMessage(String message) {
        try {
            String urlString = "https://api.telegram.org/bot" + botToken + "/sendMessage";
            String jsonPayload = "{\"chat_id\":\"" + chatId + "\",\"text\":\"" + message + "\"}";

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
            }

            if (conn.getResponseCode() != 200) {
                Bukkit.getLogger().warning("Ошибка отправки сообщения в Telegram: " + conn.getResponseMessage());
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Не удалось отправить сообщение в Telegram: " + e.getMessage());
        }
    }
}
