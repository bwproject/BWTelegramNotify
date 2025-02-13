package me.projectbw;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class TelegramNotifyPaper extends JavaPlugin implements Listener {
    private TelegramSender telegramSender;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        this.telegramSender = new TelegramSender(config);

        getServer().getPluginManager().registerEvents(this, this);

        getLogger().info("Plugins ProjectBW Активен");
        telegramSender.sendMessage("Paper сервер запущен!");
    }

    @Override
    public void onDisable() {
        telegramSender.sendMessage("Paper сервер выключен!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        telegramSender.sendMessage("Игрок зашел: " + event.getPlayer().getName() + " (IP: " + event.getPlayer().getAddress().getAddress() + ")");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        telegramSender.sendMessage("Игрок вышел: " + event.getPlayer().getName());
    }
}