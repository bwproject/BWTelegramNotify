package me.projectbw.BWTelegramNotify;

import com.velocitypowered.api.event.player.PlayerJoinEvent;
import com.velocitypowered.api.event.player.PlayerQuitEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.event.Subscribe;
import net.kyori.adventure.text.Component;

@Plugin(id = "bwtelegramnotify", name = "BWTelegramNotify", version = "1.0")
public class Main {

    @Subscribe
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Send message to Telegram about player join
        System.out.println("Player joined: " + event.getPlayer().getUsername());
    }

    @Subscribe
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Send message to Telegram about player quit
        System.out.println("Player quit: " + event.getPlayer().getUsername());
    }
}
