package me.projectbw;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TelegramNotifyPaper extends JavaPlugin implements Listener {
    private TelegramSender telegramSender;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        telegramSender = new TelegramSender(getConfig());
        
        // Вывод сообщения в консоль
        getLogger().info("Plugins ProjectBW Активен");

        // Отправка уведомления в Telegram
        telegramSender.sendMessage("Сервер запущен!");

        Bukkit.getPluginManager().registerEvents(this, this);
        startTpsMonitor();
    }

    @Override
    public void onDisable() {
        telegramSender.sendMessage("Сервер выключен!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        telegramSender.sendMessage("Игрок " + event.getPlayer().getName() + " зашел на сервер с IP: " + event.getPlayer().getAddress().getAddress().getHostAddress());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        telegramSender.sendMessage("Игрок " + event.getPlayer().getName() + " покинул сервер.");
    }

    private void startTpsMonitor() {
        new BukkitRunnable() {
            @Override
            public void run() {
                double tps = Bukkit.getTPS()[0];
                if (tps < getConfig().getDouble("telegram.low_tps_threshold")) {
                    telegramSender.sendMessage("⚠ Внимание! TPS сервера опустился ниже " + tps + "!");
                }
            }
        }.runTaskTimerAsynchronously(this, 0, getConfig().getInt("telegram.tps_check_interval") * 20L);
    }
}