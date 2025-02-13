package me.projectbw;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private final TelegramSender telegramSender;

    public PlayerListener(TelegramSender telegramSender) {
        this.telegramSender = telegramSender;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String message = "✅ Игрок " + event.getPlayer().getName() + " зашел на сервер.";
        telegramSender.sendMessage(message);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String message = "❌ Игрок " + event.getPlayer().getName() + " вышел с сервера.";
        telegramSender.sendMessage(message);
    }
}
