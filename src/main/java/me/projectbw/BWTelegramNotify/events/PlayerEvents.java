package me.projectbw.BWTelegramNotify.events;

import com.velocitypowered.api.event.player.PlayerJoinEvent;
import com.velocitypowered.api.event.player.PlayerQuitEvent;
import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.Listener;

public class PlayerEvents implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Ваш код для обработки события входа игрока
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Ваш код для обработки события выхода игрока
    }
}
