package me.projectbw;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BWTelegramNotifyPaper extends JavaPlugin implements Listener {

    private TelegramSender telegramSender;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("§aПлагин BWTelegramNotify активен.");
        telegramSender = new TelegramSender("your-bot-token", "your-chat-id");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String message = "Игрок " + event.getPlayer().getName() + " присоединился к серверу.";
        telegramSender.sendMessage(message);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String message = "Игрок " + event.getPlayer().getName() + " покинул сервер.";
        telegramSender.sendMessage(message);
    }

    @Override
    public void onDisable() {
        getLogger().info("Плагин BWTelegramNotify отключен.");
    }
}