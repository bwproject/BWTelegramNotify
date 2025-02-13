package me.projectbw;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BWTelegramNotifyPaper extends JavaPlugin implements Listener {

    private TelegramSender telegramSender;
    private int tpsCheckInterval;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        this.telegramSender = new TelegramSender(config);
        this.tpsCheckInterval = config.getInt("tpsCheckInterval", 60); // Интервал проверки TPS

        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("\u001B[92m[PROJECTBW.RU] \u001B[93mBWTelegramNotify \u001B[32mАктивен\u001B[0m");

        telegramSender.sendMessage("[SERVER] Сервер запущен!");

        startTpsMonitor();
    }

    @Override
    public void onDisable() {
        telegramSender.sendMessage("[SERVER] Сервер выключен!");
        getLogger().info("\u001B[91m[PROJECTBW.RU] BWTelegramNotify Отключен\u001B[0m");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String ip = player.getAddress().getAddress().getHostAddress();
        String message = "[PLAYER] " + player.getName() + " зашел с IP: " + ip;
        telegramSender.sendMessage(message);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String ip = player.getAddress().getAddress().getHostAddress();
        String message = "[PLAYER] " + player.getName() + " вышел (IP: " + ip + ")";
        telegramSender.sendMessage(message);
    }

    private void startTpsMonitor() {
        new BukkitRunnable() {
            @Override
            public void run() {
                double tps = Bukkit.getServer().getTPS()[0]; // Получаем текущий TPS
                if (tps < 15) { // Предупреждение о низком TPS
                    telegramSender.sendMessage("[WARNING] Низкий TPS: " + tps);
                }
            }
        }.runTaskTimer(this, 0, tpsCheckInterval * 20L); // Интервал в секундах
    }
}
