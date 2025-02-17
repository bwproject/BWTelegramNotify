package me.projectbw.BWTelegramNotify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramBot extends TelegramWebhookBot {
    private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);
    private final String botToken;
    private final String botUsername;
    private final String webhookUrl;

    public TelegramBot(String botToken, String botUsername, String webhookUrl) {
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.webhookUrl = webhookUrl;
        setWebhook();
    }

    private void setWebhook() {
        try {
            SetWebhook setWebhook = SetWebhook.builder().url(webhookUrl).build();
            execute(setWebhook);
            logger.info("Webhook set to: " + webhookUrl);
        } catch (TelegramApiException e) {
            logger.error("Failed to set webhook", e);
        }
    }

    public void sendMessage(String chatId, String message) {
        try {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text(message)
                    .build();
            execute(sendMessage);
            logger.info("Message sent: " + message);
        } catch (TelegramApiException e) {
            logger.error("Failed to send message", e);
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
    public String getBotPath() {
        return webhookUrl;
    }

    @Override
    public void onWebhookUpdateReceived(Update update) {
        logger.info("Received update: " + update);
    }
}