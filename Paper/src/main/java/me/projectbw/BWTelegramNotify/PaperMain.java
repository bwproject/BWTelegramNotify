package me.projectbw.BWTelegramNotify;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.server.ServerShutdownEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class PaperMain extends JavaPlugin implements Listener {
    private TelegramBot telegramBot;
    private YamlConfiguration config;
    private Logger logger;

    @Override
    public void onEnable() {
        this.logger = getLogger();
        loadConfig();

        if (telegramBot != null) {
            String message = config.getString("messages.server_started", "✅ **Сервер {server} запущен!**")
                    .replace("{server}", getServerName());
            telegramBot.sendMessage(message);
        }

        getServer().getPluginManager().registerEvents(this, this);
        logger.info("BWTelegramNotify успешно загружен!");
    }

    @Override
    public void onDisable() {
        if (telegramBot != null) {
            String message = config.getString("messages.server_stopped", "⛔ **Сервер {server} выключен!**")
                    .replace("{server}", getServerName());
            telegramBot.sendMessage(message);
        }
        logger.info("BWTelegramNotify отключен.");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String message = config.getString("messages.player_join", "🔵 **Игрок {player} зашел на сервер {server}**")
                .replace("{player}", event.getPlayer().getName())
                .replace("{server}", getServerName());
        telegramBot.sendMessage(message);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String message = config.getString("messages.player_quit", "⚪ **Игрок {player} вышел с сервера {server}**")
                .replace("{player}", event.getPlayer().getName())
                .replace("{server}", getServerName());
        telegramBot.sendMessage(message);
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        checkTPS();
    }

    // Закрывающая фигурная скобка для класса
}