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
        saveDefaultConfig();
        this.telegramSender = new TelegramSender(
            getConfig().getString("telegram.bot_token"), 
            getConfig().getStringList("telegram.chat_ids")
        );
        
        getServer().getPluginManager().registerEvents(this, this);
        telegramSender.sendMessage(getConfig().getString("server.startup_message"));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        telegramSender.sendMessage(getConfig().getString("server.player_join_message").replace("{player}", event.getPlayer().getName()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        telegramSender.sendMessage(getConfig().getString("server.player_quit_message").replace("{player}", event.getPlayer().getName()));
    }

    public void onServerShutdown() {
        telegramSender.sendMessage(getConfig().getString("server.server_status_message.off"));
    }
}