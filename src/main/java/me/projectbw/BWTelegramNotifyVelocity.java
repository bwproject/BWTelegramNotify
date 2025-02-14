package me.projectbw;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Plugin(id = "bwtelegramnotify", name = "BWTelegramNotify", version = "1.0")
public class BWTelegramNotifyVelocity {
    private final ProxyServer server;
    private final TelegramSender telegramSender;
    private static final Logger logger = LoggerFactory.getLogger("BWTelegramNotify");

    public BWTelegramNotifyVelocity(ProxyServer server) {
        this.server = server;
        this.telegramSender = new TelegramSender("BOT_TOKEN", "CHAT_ID"); // Укажите токен
        server.getEventManager().register(this, this);
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        telegramSender.sendMessage("Сервер включен.");
        logger.info("Сервер запущен.");
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        telegramSender.sendMessage("Сервер выключен.");
        logger.info("Сервер выключен.");
    }

    @Subscribe
    public void onPlayerJoin(PostLoginEvent event) {
        Player player = event.getPlayer();
        telegramSender.sendMessage("Игрок " + player.getUsername() + " вошел на сервер.");
    }

    @Subscribe
    public void onPlayerQuit(DisconnectEvent event) {
        Player player = event.getPlayer();
        telegramSender.sendMessage("Игрок " + player.getUsername() + " покинул сервер.");
    }
}