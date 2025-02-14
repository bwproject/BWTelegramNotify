package me.projectbw;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

@Plugin(id = "bwtelegramnotify", name = "BWTelegramNotify", version = "1.0", authors = {"YourName"})
public class BWTelegramNotifyVelocity {

    private final ProxyServer server;
    private final Logger logger;
    private TelegramSender telegramSender;

    @Inject
    public BWTelegramNotifyVelocity(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    // Пустой конструктор для Guice (если нужно)
    public BWTelegramNotifyVelocity() {
        this.server = null;
        this.logger = null;
    }

    @Subscribe
    public void onPlayerLogin(LoginEvent event) {
        telegramSender.sendMessage("Игрок " + event.getPlayer().getUsername() + " зашел на сервер.");
    }

    @Subscribe
    public void onPlayerDisconnect(DisconnectEvent event) {
        telegramSender.sendMessage("Игрок " + event.getPlayer().getUsername() + " вышел с сервера.");
    }

    @Subscribe
    public void onServerChange(ServerConnectedEvent event) {
        telegramSender.sendMessage("Игрок " + event.getPlayer().getUsername() + " сменил сервер на " + event.getServer().getServerInfo().getName());
    }
}