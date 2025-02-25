package me.projectbw.BWTelegramNotify.Velocity;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import net.kyori.adventure.text.Component;
import javax.inject.Inject;
import java.util.List;

@Plugin(id = "bwtelegramnotify", name = "BWTelegramNotify", version = "1.0.0", description = "Плагин для уведомлений в Telegram", authors = {"The_Mr_Mes109"})
public class Bridge {

    private final ProxyServer server;
    private final TelegramBot telegramBot;

    @Inject
    public Bridge(ProxyServer server, TelegramBot telegramBot) {
        this.server = server;
        this.telegramBot = telegramBot;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        // Регистрируем команду для обработки входящих сообщений
        server.getCommandManager().register("velocity_send", new VelocitySendCommand());
    }

    private class VelocitySendCommand implements Command {

        @Override
        public void execute(CommandSource source, String[] args) {
            if (args.length < 2) {
                source.sendMessage(Component.text("Ошибка: Неверное количество аргументов. Использование: /velocity_send <action> <message>"));
                return;
            }

            String action = args[0];
            String message = String.join(" ", args, 1, args.length);

            // Обработка команды и отправка сообщения в Telegram
            sendMessageToTelegram(action, message);
            source.sendMessage(Component.text("Сообщение отправлено в Telegram."));
        }
    }

    private void sendMessageToTelegram(String action, String message) {
        String fullMessage = action + ": " + message;
        telegramBot.sendMessage(fullMessage);  // Отправляем в Telegram
    }
}