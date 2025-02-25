package me.projectbw.BWTelegramNotify;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import net.kyori.adventure.text.Component;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.logging.Logger;

public class Bridge {

    private final ProxyServer server;
    private TelegramBot telegramBot;
    private static final Logger logger = Logger.getLogger(Bridge.class.getName());

    @Inject
    public Bridge(ProxyServer server) {
        this.server = server;
    }

    public void setTelegramBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        logger.info("=== Начинаем регистрацию команды... ===");

        try {
            CommandMeta commandMeta = server.getCommandManager()
                .metaBuilder("velocity_send")
                .build();

            server.getCommandManager().register(commandMeta, new VelocitySendCommand());
            logger.info("Команда /velocity_send успешно зарегистрирована.");
        } catch (Exception e) {
            logger.severe("Ошибка при регистрации команды: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendMessageToTelegram(String action, String message) {
        if (telegramBot != null) {
            String fullMessage = action + ": " + message;
            telegramBot.sendMessage(fullMessage);
        } else {
            logger.warning("Ошибка: TelegramBot не инициализирован!");
        }
    }

    // Реализация команды
    public static class VelocitySendCommand implements SimpleCommand {
        @Override
        public void execute(Invocation invocation) {
            CommandSource source = invocation.source();
            String[] args = invocation.arguments();

            logger.info("Команда /velocity_send была вызвана");

            if (args.length < 2) {
                source.sendMessage(Component.text("Ошибка: Неверное количество аргументов. Использование: /velocity_send <action> <message>"));
                return;
            }

            String action = args[0];
            String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

            logger.info("Отправка в Telegram: Action = " + action + ", Message = " + message);
            source.sendMessage(Component.text("Сообщение отправлено в Telegram."));
        }
    }
}