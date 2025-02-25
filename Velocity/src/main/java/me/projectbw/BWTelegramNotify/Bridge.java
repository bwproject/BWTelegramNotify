package me.projectbw.BWTelegramNotify;

import com.velocitypowered.api.command.Command;
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
        // Logging before registration
        logger.info("Starting command registration...");
        
        try {
            server.getCommandManager().register(
                CommandMeta.builder("velocity_send")
                    .build(),
                new VelocitySendCommand()
            );
            logger.info("Command registered successfully.");
        } catch (Exception e) {
            logger.severe("Error registering command: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendMessageToTelegram(String action, String message) {
        if (telegramBot != null) {
            String fullMessage = action + ": " + message;
            telegramBot.sendMessage(fullMessage);
        }
    }

    // Command class implementation
    public class VelocitySendCommand implements SimpleCommand {
        @Override
        public void execute(CommandSource source, String[] args) {
            logger.info("Executing /velocity_send command...");

            if (args.length < 2) {
                source.sendMessage(Component.text("Ошибка: Неверное количество аргументов. Использование: /velocity_send <action> <message>"));
                return;
            }

            String action = args[0];
            String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

            sendMessageToTelegram(action, message);
            source.sendMessage(Component.text("Сообщение отправлено в Telegram."));
        }
    }
}