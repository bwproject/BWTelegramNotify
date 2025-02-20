package me.projectbw.BWTelegramNotify;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class PaperMain extends JavaPlugin implements Listener {
    private TelegramBot telegramBot;
    private FileConfiguration config;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();

        this.telegramBot = new TelegramBot(config.getString("bot.token"), config.getString("bot.chat_id"));
        Bukkit.getPluginManager().registerEvents(this, this);

        // Цветной лог
        String border = ChatColor.AQUA + "==============================";
        String message = ChatColor.GREEN + "=== Плагин BWTelegramNotify активен ===";

        getLogger().info(border);
        getLogger().info(message);
        getLogger().info(border);
    }

    @Override
    public void onDisable() {
        getLogger().info("§c==============================");
        getLogger().info("§4=== Плагин BWTelegramNotify отключен ===");
        getLogger().info("§c==============================");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String message = "Игрок " + event.getPlayer().getName() + " зашел на сервер.";
        getLogger().info(ChatColor.YELLOW + message);
        telegramBot.sendMessage(message);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String message = "Игрок " + event.getPlayer().getName() + " вышел с сервера.";
        getLogger().info(ChatColor.RED + message);
        telegramBot.sendMessage(message);
    }
}
