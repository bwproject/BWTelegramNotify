package me.projectbw;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.Player;

import javax.inject.Inject;
import java.util.logging.Logger;

@Plugin(id = "telegramnotify", name = "TelegramNotify", version = "1.0")
public class TelegramNotifyVelocity {
    private final ProxyServer server;
    private final Logger logger;
    private final TelegramSender telegramSender;

    @Inject
    public TelegramNotifyVelocity(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
        this.telegramSender = new TelegramSender();

        // Вывод сообщения в консоль
        logger.info("Plugins ProjectBW Активен");

        // Отправка уведомления в Telegram
        telegramSender.sendMessage("Velocity сервер запущен!");
    }

    @Subscribe
    public void onPlayerJoin(PostLoginEvent event) {
        Player player = event.getPlayer();
        telegramSender.sendMessage("Игрок " + player.getUsername() + " зашел на сервер.");
    }

    @Subscribe
    public void onPlayerLeave(DisconnectEvent event) {
        Player player = event.getPlayer();
        telegramSender.sendMessage("Игрок " + player.getUsername() + " покинул сервер.");
    }
}