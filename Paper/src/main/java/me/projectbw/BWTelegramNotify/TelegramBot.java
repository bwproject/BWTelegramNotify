package me.projectbw.BWTelegramNotify;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TelegramBot {
    private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);
    private final String botToken;
    private final List<String> chatIds;

    public TelegramBot(String botToken, List<String> chatIds) {
        this.botToken = botToken;
        this.chatIds = chatIds;
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
                    os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
                }

                if (conn.getResponseCode() != 200) {
                    logger.warn("Ошибка отправки в Telegram (чат " + chatId + "): " + conn.getResponseMessage());
                }
            } catch (Exception e) {
                logger.warn("Ошибка отправки в Telegram (чат " + chatId + "): " + e.getMessage());
            }
        }
    }
}