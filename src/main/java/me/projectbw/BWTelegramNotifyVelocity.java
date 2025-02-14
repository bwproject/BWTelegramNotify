package me.projectbw;

import com.velocitypowered.api.event.Listener;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.event.proxy.PlayerJoinEvent;
import com.velocitypowered.api.event.proxy.PlayerLeaveEvent;
import com.velocitypowered.api.event.handler.EventHandler;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.event.proxy.ProxyPingEvent.Response;

public class BWTelegramNotifyVelocity {
    private final ProxyServer server;
    private final TelegramSender telegramSender;

    public BWTelegramNotifyVelocity(ProxyServer server, TelegramSender telegramSender) {
        this.server = server;
        this.telegramSender = telegramSender;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String message = "Игрок " + player.getUsername() + " присоединился к серверу!";
        telegramSender.sendMessage(message);
    }

    @EventHandler
    public void onPlayerLeave(PlayerLeaveEvent event) {
        Player player = event.getPlayer();
        String message = "Игрок " + player.getUsername() + " покинул сервер.";
        telegramSender.sendMessage(message);
    }

    @EventHandler
    public void onProxyPing(ProxyPingEvent event) {
        Response response = event.getResponse();
        String message = "Статус сервера: " + response.getVersion().getName();
        telegramSender.sendMessage(message);
    }
}