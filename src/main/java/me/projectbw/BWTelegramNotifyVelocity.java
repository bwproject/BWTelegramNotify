package me.projectbw;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BWTelegramNotifyVelocity {
    private static final Logger logger = LoggerFactory.getLogger(BWTelegramNotifyVelocity.class);
    private final ProxyServer proxyServer;
    private final TelegramSender telegramSender;

    public BWTelegramNotifyVelocity(ProxyServer proxyServer, TelegramSender telegramSender) {
        this.proxyServer = proxyServer;
        this.telegramSender = telegramSender;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        logger.info("[PROJECTBW.RU] BWTelegramNotify Активен");
        telegramSender.sendMessage("Сервер запущен!");
    }

    @Subscribe
    public void onPlayerJoin(PostLoginEvent event) {
        Player player = event.getPlayer();
        telegramSender.sendMessage(String.format("Игрок %s зашел на сервер.", player.getUsername()));
    }

    @Subscribe
    public void onPlayerQuit(PostLoginEvent event) {
        Player player = event.getPlayer();
        telegramSender.sendMessage(String.format("Игрок %s покинул сервер.", player.getUsername()));
    }
}