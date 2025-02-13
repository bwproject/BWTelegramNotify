package me.projectbw;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

@Plugin(id = "bwtelegramnotify", name = "BWTelegramNotify", version = "1.0")
public class BWTelegramNotifyVelocity {

    private final ProxyServer server;
    private final Logger logger;
    private TelegramSender telegramSender;

    @Inject
    public BWTelegramNotifyVelocity(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        logger.info("\u001B[92m[PROJECTBW.RU] \u001B[93mBWTelegramNotify \u001B[32mАктивен\u001B[0m");
        telegramSender.sendMessage("[PROXY] Прокси-сервер запущен!");
    }

    @Subscribe
    public void onPlayerJoin(PostLoginEvent event) {
        String playerName = event.getPlayer().getUsername();
        String ip = event.getPlayer().getRemoteAddress().getAddress().getHostAddress();
        telegramSender.sendMessage("[PROXY] Игрок " + playerName + " зашел с IP: " + ip);
    }

    @Subscribe
    public void onPlayerQuit(DisconnectEvent event) {
        String playerName = event.getPlayer().getUsername();
        String ip = event.getPlayer().getRemoteAddress().getAddress().getHostAddress();
        telegramSender.sendMessage("[PROXY] Игрок " + playerName + " вышел (IP: " + ip + ")");
    }
}
