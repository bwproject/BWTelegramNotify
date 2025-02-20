package me.projectbw.BWTelegramNotify;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class TPSMonitor {
    private final TelegramBot telegramBot;
    private final PaperMain plugin;
    private static final double TPS_THRESHOLD = 15.0;

    public TPSMonitor(PaperMain plugin, TelegramBot telegramBot) {
        this.plugin = plugin;
        this.telegramBot = telegramBot;
    }

    public void startMonitoring() {
        new BukkitRunnable() {
            @Override
            public void run() {
                double tps = Bukkit.getServer().getTPS()[0];
                if (tps < TPS_THRESHOLD) {
                    String message = "Внимание: низкий TPS: " + tps;
                    Bukkit.getLogger().warning(message);
                    telegramBot.sendMessage(message);
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 1200L);
    }
}
