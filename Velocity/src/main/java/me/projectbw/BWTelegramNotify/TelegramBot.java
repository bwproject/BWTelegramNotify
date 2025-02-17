package me.projectbw.BWTelegramNotify;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TelegramBot extends TelegramWebhookBot {
    private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);
    private final String botToken;
    private final String botUsername;
    private final String webhookUrl;

    public TelegramBot(String botToken, String botUsername, String webhookUrl) {
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.webhookUrl = webhookUrl;
    }

    public void sendMessage(String chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);

        try {
            execute(sendMessage);
            logger.info("Message sent: " + message);
        } catch (TelegramApiException e) {
            logger.error("Failed to send message: ", e);
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getWebhookPath() {
        return webhookUrl;
    }

    @Override
    public void onUpdateReceived(Update update) {
        logger.info("Received update: " + update);
    }

    public void setWebhook() {
        String apiUrl = "https://api.telegram.org/bot" + botToken + "/setWebhook";
        
        Map<String, String> requestData = new HashMap<>();
        requestData.put("url", webhookUrl);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(apiUrl);
            request.setHeader("Content-Type", "application/json");
            
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(requestData);
            
            request.setEntity(new StringEntity(json, StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = client.execute(request)) {
                logger.info("Webhook set response: " + response.getCode());
            }
        } catch (IOException e) {
            logger.error("Failed to set webhook", e);
        }
    }
}