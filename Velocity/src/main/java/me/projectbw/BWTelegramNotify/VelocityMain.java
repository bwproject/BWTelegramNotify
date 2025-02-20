package me.projectbw.BWTelegramNotify;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

@Plugin(id = "bwtelegramnotify", name = "BWTelegramNotify", version = "1.0")
public class VelocityMain {
    private final ProxyServer server;
    private final Logger logger;
    private final TelegramBot telegramBot;

    private static final String BORDER = "\u001B[36m==============================\u001B[0m";
    private static final String MESSAGE = "\u001B[32m=== Плагин BWTelegramNotify активен ===\u001B[0m";

    @Inject
    public VelocityMain(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
        this.telegramBot = new TelegramBot();

        // Цветной лог в консоль
        logger.info(BORDER);
        logger.info(MESSAGE);
        logger.info(BORDER);
    }

    @Subscribe
    public void onPlayerJoin(LoginEvent event) {
        String message = "Игрок " + event.getPlayer().getUsername() + " зашел на сервер.";
        logger.info("\u001B[33m" + message + "\u001B[0m");
        telegramBot.sendMessage(message);
    }

    @Subscribe
    public void onPlayerQuit(DisconnectEvent event) {
        String message = "Игрок " + event.getPlayer().getUsername() + " вышел с сервера.";
        logger.info("\u001B[31m" + message + "\u001B[0m");
        telegramBot.sendMessage(message);
    }

    @Subscribe
    public void onServerSwitch(ServerConnectedEvent event) {
        String message = "Игрок " + event.getPlayer().getUsername() + " сменил сервер на " + event.getServer().getServerInfo().getName();
        logger.info("\u001B[34m" + message + "\u001B[0m");
        telegramBot.sendMessage(message);
    }
}
