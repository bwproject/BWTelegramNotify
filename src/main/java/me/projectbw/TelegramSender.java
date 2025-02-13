package me.projectbw;

import org.bukkit.configuration.file.FileConfiguration;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.util.List;

public class TelegramSender {
    private final String botToken;
    private final List<String> chatIds;

    public TelegramSender(FileConfiguration config) {
        this.botToken = config.getString("telegram.token");
        this.chatIds = config.getStringList("telegram.chat_ids");
    }

    public void sendMessage(String message) {
        for (String chatId : chatIds) {
            try {
                String urlString = "https://api.telegram.org/bot" + botToken + "/sendMessage";
                String jsonPayload = "{\"chat_id\":\"" + chatId + "\",\"text\":\"" + message + "\"}";
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(jsonPayload.getBytes());
                    os.flush();
                }
                conn.getResponseCode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}