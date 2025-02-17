package me.projectbw.BWTelegramNotify;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PaperMain extends JavaPlugin implements Listener {
    private TelegramBot telegramBot;
    private double lowTpsThreshold;

    @Override
    public void onEnable() {
        this.telegramBot = new TelegramBot();
        
        // Загружаем конфиг
        saveDefaultConfig();
        String chatId = getConfig().getString("chat_id");
        String botToken = getConfig().getString("bot_token");
        telegramBot.setConfig(chatId, botToken);

        // Получаем сообщения и порог TPS из конфигурации
        String joinMessageTemplate = getConfig().getString("join_message");
        String quitMessageTemplate = getConfig().getString("quit_message");
        lowTpsThreshold = getConfig().getDouble("low_tps_threshold");

        // Регистрация событий
        Bukkit.getPluginManager().registerEvents(this, this);
        
        // Запуск проверки TPS
        getServer().getScheduler().runTaskTimer(this, this::checkTps, 0, 100); // Проверка каждую секунду
        getLogger().info("BWTelegramNotify for Paper has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("BWTelegramNotify for Paper has been disabled!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String message = getConfig().getString("join_message").replace("{player}", event.getPlayer().getName());
        getLogger().info(message);
        telegramBot.sendMessage(message);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String message = getConfig().getString("quit_message").replace("{player}", event.getPlayer().getName());
        getLogger().info(message);
        telegramBot.sendMessage(message);
    }

    private void checkTps() {
        double tps = getServer().getTPS()[0]; // Получаем средний TPS за последние 1 секунду
        if (tps < lowTpsThreshold) {
            String message = "Warning: Server TPS is low! Current TPS: " + tps;
            getLogger().warning(message);
            telegramBot.sendMessage(message);
        }
    }
}