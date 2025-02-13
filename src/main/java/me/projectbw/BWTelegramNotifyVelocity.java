package me.projectbw;

import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.Listener;

@Plugin(id = "bwtelegramnotify", name = "BWTelegramNotify", version = "1.0")
public class BWTelegramNotifyVelocity implements Listener {

    private final TelegramSender telegramSender;
    private final ProxyServer server;

    // Конструктор с передачей зависимости TelegramSender и ProxyServer
    public BWTelegramNotifyVelocity(TelegramSender telegramSender, ProxyServer server) {
        this.telegramSender = telegramSender;
        this.server = server;
    }

    // Обработчик события включения сервера
    @EventHandler
    public void onProxyInitialize(ProxyInitializeEvent event) {
        telegramSender.sendMessage("Сервер включен.");
    }

    // Обработчик события выключения сервера
    @EventHandler
    public void onProxyShutdown(ProxyShutdownEvent event) {
        telegramSender.sendMessage("Сервер выключен.");
    }

    // Обработчик события входа игрока на сервер
    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        Player player = event.getPlayer();
        telegramSender.sendMessage("Игрок " + player.getUsername() + " вошел на сервер.");
    }

    // Обработчик события выхода игрока с сервера
    @EventHandler
    public void onPlayerQuit(DisconnectEvent event) {
        Player player = event.getPlayer();
        telegramSender.sendMessage("Игрок " + player.getUsername() + " покинул сервер.");
    }
}