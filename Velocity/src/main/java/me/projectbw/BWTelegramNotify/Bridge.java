package me.projectbw.BWTelegramNotify;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import net.kyori.adventure.text.Component;
import javax.inject.Inject;
import java.util.Arrays;

public class Bridge {

    private final ProxyServer server;
    private TelegramBot telegramBot;  // Не статический

    @Inject
    public Bridge(ProxyServer server) {
        this.server = server;
    }

    // Сеттер для TelegramBot
    public void setTelegramBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        // Регистрация команды с правильным методом
        CommandMeta commandMeta = server.getCommandManager().metaBuilder("velocity_send")
            .build();

        // Регистрация команды через явную реализацию класса
        server.getCommandManager().register(commandMeta, new VelocitySendCommand());
    }

    private void sendMessageToTelegram(String action, String message) {
        if (telegramBot != null) {
            String fullMessage = action + ": " + message;
            telegramBot.sendMessage(fullMessage);  // Отправляем в Telegram
        }
    }

    // Явная реализация команды
    public class VelocitySendCommand implements Command {
        @Override
        public void execute(CommandSource source, String[] args) {
            if (args.length < 2) {
                source.sendMessage(Component.text("Ошибка: Неверное количество аргументов. Использование: /velocity_send <action> <message>"));
                return;
            }

            String action = args[0];
            String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

            // Обработка команды и отправка сообщения в Telegram
            sendMessageToTelegram(action, message);
            source.sendMessage(Component.text("Сообщение отправлено в Telegram."));
        }
    }
}