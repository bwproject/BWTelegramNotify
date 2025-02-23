package me.projectbw.BWTelegramNotify;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

import java.util.List;

@Plugin(id = "bwtelegramnotify", name = "BWTelegramNotify", version = "1.3")
public class VelocityMain {
    private static boolean running = false;
    private final ProxyServer server;
    private final Logger logger;
    private TelegramBot telegramBot;

    @Inject
    public VelocityMain(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
        server.getCommandManager().register("bwstatusbot", new StatusCommand());
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        running = true;
        telegramBot = new TelegramBot("YOUR_BOT_TOKEN", List.of("CHAT_ID"));

        logger.info("\n==============================\n"
                  + "=== Плагин BWTelegramNotify активен ===\n"
                  + "==============================");

        logger.info("\u001B[32mTelegram-бот запущен: " + telegramBot.getBotName() + " (@" + telegramBot.getBotUsername() + ")\u001B[0m");

        telegramBot.sendMessage("✅ **Velocity-сервер запущен!**");
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        running = false;
        if (telegramBot != null) {
            telegramBot.sendMessage("⛔ **Velocity-сервер выключен!**");
        }
        logger.info("\u001B[31m⛔ BWTelegramNotify отключен!\u001B[0m");
    }

    @Subscribe
    public void onPlayerLogin(LoginEvent event) {
        telegramBot.sendMessage("🔵 **Игрок зашел на сервер:** " + event.getPlayer().getUsername());
    }

    @Subscribe
    public void onPlayerDisconnect(DisconnectEvent event) {
        telegramBot.sendMessage("⚪ **Игрок вышел с сервера:** " + event.getPlayer().getUsername());
    }

    @Subscribe
    public void onPlayerChangeServer(ServerConnectedEvent event) {
        telegramBot.sendMessage("🔄 **Игрок сменил сервер:** " + event.getPlayer().getUsername() +
                "\n➡ Новый сервер: " + event.getServer().getServerInfo().getName());
    }

    public static boolean isRunning() {
        return running;
    }

    private class StatusCommand implements SimpleCommand {
        @Override
        public void execute(Invocation invocation) {
            String message = "📢 BWTelegramNotify:\n"
                    + "Бот: " + telegramBot.getBotName() + " (@" + telegramBot.getBotUsername() + ")\n"
                    + "Сервер: Velocity";

            invocation.source().sendMessage(Component.text(message));
            logger.info(message);
        }
    }
}
