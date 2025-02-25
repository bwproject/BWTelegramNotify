package me.projectbw.BWTelegramNotify;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import net.kyori.adventure.text.Component;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

public class Bridge {

    private final ProxyServer server;
    private static TelegramBot telegramBot;

    @Inject
    public Bridge(ProxyServer server) {
        this.server = server;
        // Инициализация Telegram-бота
        loadConfig();
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        // Регистрируем команду
        server.getCommandManager().register("velocity_send", (source, args) -> {
            if (args.length < 2) {
                source.sendMessage(Component.text("Ошибка: Неверное количество аргументов. Использование: /velocity_send <action> <message>"));
                return;
            }

            String action = args[0];
            String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

            // Обработка команды и отправка сообщения в Telegram
            sendMessageToTelegram(action, message);
            source.sendMessage(Component.text("Сообщение отправлено в Telegram."));
        });
    }

    private void loadConfig() {
        if (telegramBot == null) {
            String botToken = "ВАШ_ТОКЕН";  // Укажите ваш токен
            List<String> chatIds = List.of("ВАШ_ЧАТ_ID");  // Укажите ваш chat ID
            telegramBot = new TelegramBot(botToken, chatIds);
        }
    }

    private void sendMessageToTelegram(String action, String message) {
        String fullMessage = action + ": " + message;
        telegramBot.sendMessage(fullMessage);  // Отправляем в Telegram
    }
}