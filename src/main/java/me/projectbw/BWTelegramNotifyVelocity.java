package me.projectbw;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.connection.PreLogoutEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BWTelegramNotifyVelocity {
    private static final Logger logger = LoggerFactory.getLogger(BWTelegramNotifyVelocity.class);
    private final ProxyServer proxyServer;
    private final TelegramSender telegramSender;

    private final double lowTpsThreshold;
    private final int tpsCheckInterval;
    
    public BWTelegramNotifyVelocity(ProxyServer proxyServer, TelegramSender telegramSender) {
        this.proxyServer = proxyServer;
        this.telegramSender = telegramSender;
        
        // Извлекаем параметры из конфига
        this.lowTpsThreshold = proxyServer.getConfiguration().getDouble("telegram.low_tps_threshold", 15.0);
        this.tpsCheckInterval = proxyServer.getConfiguration().getInt("telegram.tps_check_interval", 10);
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        logger.info("[PROJECTBW.RU] BWTelegramNotify для Velocity активен.");
        telegramSender.sendMessage("Сервер Velocity запущен!");
    }

    @Subscribe
    public void onPlayerJoin(PostLoginEvent event) {
        Player player = event.getPlayer();
        telegramSender.sendMessage(String.format("Игрок %s зашел на сервер Velocity.", player.getUsername()));
    }

    @Subscribe
    public void onPlayerQuit(PreLogoutEvent event) {
        Player player = event.getPlayer();
        telegramSender.sendMessage(String.format("Игрок %s покинул сервер Velocity.", player.getUsername()));
    }

    public void sendServerChangeMessage(Player player, String newServer) {
        telegramSender.sendMessage(String.format("Игрок %s сменил сервер на %s.", player.getUsername(), newServer));
    }
    
    public void sendLowTpsAlert(double currentTps) {
        if (currentTps < lowTpsThreshold) {
            telegramSender.sendMessage(String.format("Предупреждение! TPS ниже порога (%f): %f", lowTpsThreshold, currentTps));
        }
    }
}