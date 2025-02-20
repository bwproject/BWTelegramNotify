package me.projectbw.BWTelegramNotify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TelegramBot {
    private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);
    private final String botToken = "YOUR_BOT_TOKEN";
    private final String chatId = "YOUR_CHAT_ID";

    public void sendMessage(String message) {
        // Логика отправки сообщения в Telegram
        logger.info("Sending message to Telegram: " + message);
    }
}
