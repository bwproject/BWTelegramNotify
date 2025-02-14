package me.projectbw.BWTelegramNotify;

import com.velocitypowered.api.event.Listener;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.PlayerJoinEvent;
import com.velocitypowered.api.event.proxy.PlayerLeaveEvent;
import com.velocitypowered.api.event.proxy.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.ProxyServer;

public class BWTelegramNotifyVelocity implements Listener {

    private final ProxyServer server;

    public BWTelegramNotifyVelocity(ProxyServer server) {
        this.server = server;
    }

    @Subscribe
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Отправка уведомления о входе игрока
        // Используйте методы для отправки сообщений в Telegram
        String playerName = event.getPlayer().getUsername();
        sendTelegramMessage("Player " + playerName + " has joined the server.");
    }

    @Subscribe
    public void onPlayerLeave(PlayerLeaveEvent event) {
        // Отправка уведомления о выходе игрока
        String playerName = event.getPlayer().getUsername();
        sendTelegramMessage("Player " + playerName + " has left the server.");
    }

    @Subscribe
    public void onServerPreConnect(ServerPreConnectEvent event) {
        // Отправка уведомления о смене сервера игроком
        String playerName = event.getPlayer().getUsername();
        String serverName = event.getServer().getServerInfo().getName();
        sendTelegramMessage("Player " + playerName + " is connecting to server " + serverName + ".");
    }

    private void sendTelegramMessage(String message) {
        // Реализуйте отправку сообщения в Telegram
        System.out.println("Sending message to Telegram: " + message);
    }
}