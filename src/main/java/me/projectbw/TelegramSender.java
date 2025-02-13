package me.projectbw;

import org.bukkit.configuration.file.FileConfiguration;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.io.OutputStream;

public class TelegramSender {
    private final String botToken;
    private final String chatId;

    public TelegramSender(FileConfiguration config) {
        this.botToken = config.getString("telegram.bot-token");
        this.chatId = config.getString("telegram.chat-id");
    }

    public void sendMessage(String message) {
        if (botToken == null || chatId == null) {
            System.out.println("[TelegramSender] Ошибка: botToken или chatId не установлены!");
            return;
        }

        String urlString = "https://api.telegram.org/bot" + botToken + "/sendMessage";
        String jsonPayload = "{\"chat_id\":\"" + chatId + "\",\"text\":\"" + message + "\"}";

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            conn.disconnect();

            if (responseCode != 200) {
                System.out.println("[TelegramSender] Ошибка отправки сообщения в Telegram. Код ответа: " + responseCode);
            }
        } catch (IOException e) {
            System.out.println("[TelegramSender] Ошибка: " + e.getMessage());
        }
    }

    public boolean checkBotStatus() {
        if (botToken == null) {
            return false;
        }

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
}
