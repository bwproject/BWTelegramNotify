package me.projectbw.BWTelegramNotify;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.charset.StandardCharsets;

public class PaperMain extends JavaPlugin implements Listener {
    private static boolean running = false;
    private static final String CHANNEL = "bwtelegram:notify";

    @Override
    public void onEnable() {
        running = true;
        saveDefaultConfig();
        getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL);

        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("bwstatusbot").setExecutor((sender, command, label, args) -> {
            sender.sendMessage("📢 BWTelegramNotify работает через Velocity");
            return true;
        });

        getServer().getConsoleSender().sendMessage("\n§a==============================\n"
                + "§a=== Плагин BWTelegramNotify активен ===\n"
                + "§a==============================");

        sendToVelocity("✅ **Paper-сервер запущен!**");
    }

    @Override
    public void onDisable() {
        running = false;
        sendToVelocity("⛔ **Paper-сервер выключен!**");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        sendToVelocity("🔵 **Игрок зашел на сервер:** " + event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        sendToVelocity("⚪ **Игрок вышел с сервера:** " + event.getPlayer().getName());
    }

    private void sendToVelocity(String message) {
        byte[] data = message.getBytes(StandardCharsets.UTF_8);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendPluginMessage(this, CHANNEL, data);
            break; // Достаточно одного игрока
        }
    }
}