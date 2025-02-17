package me.projectbw.BWTelegramNotify;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PaperMain extends JavaPlugin implements Listener {
    private TelegramBot telegramBot;

    @Override
    public void onEnable() {
        this.telegramBot = new TelegramBot();
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("BWTelegramNotify for Paper has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("BWTelegramNotify for Paper has been disabled!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String message = "Player " + event.getPlayer().getName() + " has joined the server.";
        getLogger().info(message);
        telegramBot.sendMessage(message);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String message = "Player " + event.getPlayer().getName() + " has left the server.";
        getLogger().info(message);
        telegramBot.sendMessage(message);
    }
}
