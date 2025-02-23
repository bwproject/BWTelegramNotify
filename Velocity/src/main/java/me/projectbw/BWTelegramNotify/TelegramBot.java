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
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);

            User botInfo = execute(new GetMe());
            this.botName = botInfo.getFirstName();
            this.botUsername = botInfo.getUserName();

            System.out.println("Telegram-бот запущен: " + botUsername + " (@" + botUsername + ")");
        } catch (TelegramApiException e) {
            System.err.println("Ошибка при запуске Telegram-бота:");
            e.printStackTrace();
        }
    }

    public void sendMessage(String text) {
        for (String chatId : chatIds) {
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId)); // Преобразование chatId в String
            message.setText(text);
            message.enableMarkdown(true);

            try {
                execute(message);
            } catch (TelegramApiException e) {
                System.err.println("Ошибка при отправке сообщения в Telegram:");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Метод нужен для получения сообщений, но он не используется в этом боте
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public String getBotName() {
        return botName;
    }
}
