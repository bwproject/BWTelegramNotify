package me.projectbw.BWTelegramNotify;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramBot extends AbsSender {

    @Override
    public <T> void sendApiMethod(T method) throws TelegramApiException {
        if (method instanceof SendMessage) {
            // Реализация отправки сообщения в Telegram
            // Используйте ваше подключение и логику для отправки.
            SendMessage message = (SendMessage) method;
            // Логика отправки сообщения
            System.out.println("Sending message: " + message.getText());
        }
    }
}