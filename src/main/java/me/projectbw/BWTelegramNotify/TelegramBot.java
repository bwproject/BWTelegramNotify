package me.projectbw.BWTelegramNotify;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class TelegramBot extends AbsSender {

    private String botToken;
    private List<String> chats;

    public TelegramBot(String botToken, List<String> chats) {
        this.botToken = botToken;
        this.chats = chats;
    }

    @Override
    public String getBotUsername() {
        return "MyTelegramBot";
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public void sendMessage(String message) {
        for (String chatId : chats) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(message);
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        // Инициализация бота
    }

    public void stop() {
        // Остановить бота
    }
}
