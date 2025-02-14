package me.projectbw;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramSender extends TelegramLongPollingBot {
    private final String botToken;
    private final String chatId;

    public TelegramSender(String botToken, String chatId) {
        this.botToken = botToken;
        this.chatId = chatId;
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Можно обработать команды из Telegram
    }

    @Override
    public String getBotUsername() {
        return "YourBotUsername"; // Укажите имя бота
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public void sendMessage(String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String checkBotStatus() {
        return "Бот работает";
    }
}