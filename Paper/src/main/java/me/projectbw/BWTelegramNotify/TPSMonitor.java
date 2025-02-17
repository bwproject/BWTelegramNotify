package me.projectbw.BWTelegramNotify;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class TPSMonitor {
    private final TelegramBot telegramBot;
    private static final double TPS_THRESHOLD = 15.0;

    public TPSMonitor(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void startMonitoring() {
        new BukkitRunnable() {
            @Override
            public void run() {
                double tps = Bukkit.getServer().getTPS()[0];
                if (tps < TPS_THRESHOLD) {
                    String message = "Warning: Server TPS is low: " + tps;
                    Bukkit.getLogger().warning(message);
                    telegramBot.sendMessage(message);
                }
            }
        }.runTaskTimerAsynchronously(null, 0L, 1200L);
    }
}
