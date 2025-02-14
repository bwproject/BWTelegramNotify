package me.projectbw.BWTelegramNotify;

import com.velocitypowered.api.event.Listener;
import com.velocitypowered.api.event.connection.PlayerJoinEvent;
import com.velocitypowered.api.event.connection.PlayerLeaveEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class BWTelegramNotifyVelocity implements Listener {
    private static final Logger logger = LoggerFactory.getLogger(BWTelegramNotifyVelocity.class);
    
    private final ProxyServer proxyServer;

    @Inject
    public BWTelegramNotifyVelocity(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Subscribe
    public void onPlayerJoin(PlayerJoinEvent event) {
        logger.info("Player joined: " + event.getPlayer().getUsername());
        // Логика отправки уведомлений в Telegram
    }

    @Subscribe
    public void onPlayerLeave(PlayerLeaveEvent event) {
        logger.info("Player left: " + event.getPlayer().getUsername());
        // Логика отправки уведомлений в Telegram
    }
}