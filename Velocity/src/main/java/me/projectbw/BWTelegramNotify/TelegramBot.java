package me.projectbw.BWTelegramNotify;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;

public class TelegramBot extends TelegramLongPollingBot {
    private final String botToken;
    private final List<String> chatIds;
    private String botName = "Unknown";
    private String botUsername = "Unknown";

    public TelegramBot(String botToken, List<String> chatIds) {
        this.botToken = botToken;
        this.chatIds = chatIds;

        try {
            // Регистрация бота через TelegramBotsApi
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);

            // Получение информации о боте (имя и username)
            User botInfo = execute(new GetMe());
            this.botName = botInfo.getFirstName();
            this.botUsername = botInfo.getUserName();
            System.out.println("Bot registered successfully: " + botName + " (" + botUsername + ")");
        } catch (TelegramApiException e) {
            System.err.println("Ошибка при запуске Telegram-бота: " + e.getMessage());
        }
    }

    public void sendMessage(String text) {
        // Отправка сообщений в каждый чат
        for (String chatId : chatIds) {
            SendMessage message = new SendMessage();
            try {
                message.setChatId(Long.parseLong(chatId));  // Преобразование chatId в Long
                message.setText(text);
                message.enableMarkdown(true);
                System.out.println("Отправка сообщения: chatId=" + chatId + " текст=" + text);
                execute(message);  // Отправка сообщения
            } catch (NumberFormatException e) {
                System.err.println("Неверный формат chatId: " + chatId);
            } catch (TelegramApiException e) {
                System.err.println("Ошибка при отправке сообщения в Telegram: " + e.getMessage());
            }
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Этот метод обязателен, но не нужен для отправки сообщений, поэтому оставляем пустым
    }

    @Override
    public String getBotUsername() {
        return botUsername;  // Возвращаем username бота
    }

    @Override
    public String getBotToken() {
        return botToken;  // Возвращаем токен бота
    }

    public String getBotName() {
        return botName;  // Возвращаем имя бота
    }
}
