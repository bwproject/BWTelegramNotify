package me.projectbw.BWTelegramNotify;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerJoinEvent;
import com.velocitypowered.api.event.player.PlayerQuitEvent;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private final ProxyServer proxy;

    public Main(ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Subscribe
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        logger.info(player.getUsername() + " has joined the server.");
    }

    @Subscribe
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        logger.info(player.getUsername() + " has left the server.");
    }

    public void init() {
        EventManager eventManager = proxy.getEventManager();
        eventManager.register(this);
        logger.info("BWTelegramNotify Plugin Initialized.");
    }
}
