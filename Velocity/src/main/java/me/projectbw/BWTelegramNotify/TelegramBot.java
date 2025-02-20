package me.projectbw.BWTelegramNotify;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TelegramBot {
    private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);
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
                logger.warn("Ошибка отправки сообщения в Telegram: " + conn.getResponseMessage());
            }
        } catch (Exception e) {
            logger.warn("Не удалось отправить сообщение в Telegram: " + e.getMessage());
        }
    }
}
