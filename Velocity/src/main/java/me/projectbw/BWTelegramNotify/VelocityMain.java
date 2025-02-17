package me.projectbw.BWTelegramNotify;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerLoginEvent;
import com.velocitypowered.api.event.player.PlayerDisconnectEvent;
import com.velocitypowered.api.event.player.PlayerSwitchServerEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

@Plugin(id = "bwtelegramnotify", name = "BWTelegramNotify", version = "1.0")
public class VelocityMain {
    private final ProxyServer server;
    private final Logger logger;
    private final TelegramBot telegramBot;

    @Inject
    public VelocityMain(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
        this.telegramBot = new TelegramBot();
        logger.info("BWTelegramNotify for Velocity has been enabled!");
    }

    // Обработчик для события входа игрока
    @Subscribe
    public void onPlayerJoin(PlayerLoginEvent event) {
        String message = "Player " + event.getPlayer().getUsername() + " has joined the server.";
        logger.info(message);
        telegramBot.sendMessage(message);
    }

    // Обработчик для события выхода игрока
    @Subscribe
    public void onPlayerLeave(PlayerDisconnectEvent event) {
        String message = "Player " + event.getPlayer().getUsername() + " has left the server.";
        logger.info(message);
        telegramBot.sendMessage(message);
    }

    // Обработчик для события смены сервера игроком
    @Subscribe
    public void onServerSwitch(PlayerSwitchServerEvent event) {
        String message = "Player " + event.getPlayer().getUsername() + " switched servers.";
        logger.info(message);
        telegramBot.sendMessage(message);
    }
}