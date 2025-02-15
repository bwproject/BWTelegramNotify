package java.me.projectbw.BWTelegramNotify;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Notifier {
    private String botToken;
    private String[] chatIds;

    public Notifier(String botToken, String[] chatIds) {
        this.botToken = botToken;
        this.chatIds = chatIds;
    }

    public void sendMessage(String message) {
        // Пример отправки сообщения
    }
}
