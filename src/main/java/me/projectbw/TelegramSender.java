package me.projectbw;

import org.bukkit.configuration.file.FileConfiguration;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TelegramSender {
    private final String botToken;
    private final String chatId;

    public TelegramSender(FileConfiguration config) {
        this.botToken = config.getString("telegram.bot_token");
        this.chatId = config.getString("telegram.chat_id");
    }

    public TelegramSender() {
        this.botToken = "ВАШ_ТОКЕН_БОТА";
        this.chatId = "ВАШ_CHAT_ID";
    }

    public void sendMessage(String message) {
        if (botToken == null || chatId == null) return;

        try {
            String urlString = "https://api.telegram.org/bot" + botToken + "/sendMessage";
            String jsonPayload = "{\"chat_id\":\"" + chatId + "\", \"text\":\"" + message + "\"}";

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
            os.close();

            conn.getResponseCode();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public boolean checkBotStatus() {
    try {
        String urlString = "https://api.telegram.org/bot" + botToken + "/getMe";
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        conn.disconnect();

        return responseCode == 200;
    } catch (Exception e) {
        return false;
    }
}
