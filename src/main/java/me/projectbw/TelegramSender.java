package me.projectbw;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TelegramSender {
    private final String botToken;
    private final String[] chatIds;

    public TelegramSender(FileConfiguration config) {
        this.botToken = config.getString("telegram.botToken", "");
        this.chatIds = config.getStringList("telegram.chatIds").toArray(new String[0]);
    }

    public boolean sendMessage(String message) {
        boolean success = false;
        for (String chatId : chatIds) {
            try {
                String urlString = "https://api.telegram.org/bot" + botToken + "/sendMessage";
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String jsonPayload = "{\"chat_id\":\"" + chatId + "\",\"text\":\"" + message + "\"}";
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    success = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    public boolean checkBotStatus() {
        try {
            URL url = new URL("https://api.telegram.org/bot" + botToken + "/getMe");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            return (responseCode == 200);
        } catch (IOException e) {
            return false;
        }
    }
}
