package me.projectbw.BWTelegramNotify.events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerJoinEvent;
import com.velocitypowered.api.event.player.PlayerQuitEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerEvents {
    private static final Logger logger = LoggerFactory.getLogger(PlayerEvents.class);

    @Subscribe
    public void onPlayerJoin(PlayerJoinEvent event) {
        logger.info("Player " + event.getPlayer().getUsername() + " has joined.");
    }

    @Subscribe
    public void onPlayerQuit(PlayerQuitEvent event) {
        logger.info("Player " + event.getPlayer().getUsername() + " has quit.");
    }
}
