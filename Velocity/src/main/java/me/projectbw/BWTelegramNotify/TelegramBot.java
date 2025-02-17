package me.projectbw.BWTelegramNotify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.bots.LongPollingBot;

public class TelegramBot extends LongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);
    private String botToken;
    private String chatId;

    // Метод для установки токена бота и chatId
    public void setConfig(String botToken, String chatId) {
        this.botToken = botToken;
        this.chatId = chatId;
    }

    // Метод для отправки сообщения в Telegram
    public void sendMessage(String message) {
        if (botToken == null || chatId == null) {
            logger.error("Bot token or chat ID is not set.");
            return;
        }

        // Создание объекта сообщения
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);

        try {
            // Отправка сообщения
            execute(sendMessage);
            logger.info("Message sent: " + message);
        } catch (TelegramApiException e) {
            logger.error("Failed to send message: ", e);
        }
    }

    @Override
    public String getBotUsername() {
        return "YOUR_BOT_USERNAME"; // Укажите имя вашего бота
    }

    @Override
    public String getBotToken() {
        return botToken; // Используем токен, переданный в setConfig()
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Обработчик входящих обновлений (если нужно)
        logger.info("Received update: " + update);
    }

    // Метод для регистрации бота в TelegramBotsApi
    public void registerBot() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi();
            botsApi.registerBot(this); // Регистрируем текущий экземпляр бота
            logger.info("Bot successfully registered.");
        } catch (TelegramApiException e) {
            logger.error("Failed to register bot: ", e);
        }
    }
}