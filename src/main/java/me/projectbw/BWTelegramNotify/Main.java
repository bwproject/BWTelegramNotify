package me.projectbw.BWTelegramNotify;

import com.velocitypowered.api.event.Listener;
import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.player.PlayerJoinEvent;
import com.velocitypowered.api.event.player.PlayerQuitEvent;
import me.projectbw.BWTelegramNotify.events.PlayerEvents;

public class Main implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Логика для уведомления при входе игрока на Velocity
        Notifier notifier = new Notifier();
        notifier.sendPlayerNotification(event.getPlayer().getUsername(), "joined the server");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Логика для уведомления при выходе игрока с Velocity
        Notifier notifier = new Notifier();
        notifier.sendPlayerNotification(event.getPlayer().getUsername(), "left the server");
    }
}
