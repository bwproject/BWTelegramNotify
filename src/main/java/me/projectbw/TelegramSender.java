package me.projectbw;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.List;

public class TelegramSender extends TelegramLongPollingBot {
    private final List<String> chatIds;

    public TelegramSender(String botToken, List<String> chatIds) {
        super(botToken);
        this.chatIds = chatIds;
    }

    @Override
    public String getBotUsername() {
        return "YourBotUsername";
    }

    @Override
    public String getBotToken() {
        return "YOUR_BOT_TOKEN";
    }

    public void sendMessage(String messageText) {
        SendMessage message = new SendMessage();
        message.setText(messageText);

        for (String chatId : chatIds) {
            message.setChatId(chatId);
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessageToChatId(String chatId, String messageText) {
        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setText(messageText);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}