package me.projectbw.BWTelegramNotify;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.List;

public class PaperMain extends JavaPlugin implements Listener {
    private static boolean running = false;
    private TelegramBot telegramBot;
    private double tpsThreshold;
    private boolean updateEnabled;
    private boolean isVelocity;

    @Override
    public void onEnable() {
        running = true;
        saveDefaultConfig();
        reloadConfig();

        String botToken = getConfig().getString("telegram.botToken", "").trim();
        List<String> chatIds = getConfig().getStringList("telegram.chatIds");

        if (botToken.isEmpty() || chatIds.isEmpty()) {
            getLogger().severe("❌ Ошибка: botToken или chatIds не указаны в config.yml! Отключение плагина...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        tpsThreshold = getConfig().getDouble("settings.tps", 15.0);
        updateEnabled = getConfig().getBoolean("settings.update", true);

        // Определяем, работает ли сервер через Velocity
        isVelocity = isVelocityEnabled();

        if (isVelocity) {
            getLogger().info("✅ Сервер запущен через Paper за Velocity.");
            sendToVelocity("server_started", getConfig().getString("messages.server_started", "✅ **Paper-сервер запущен через Velocity!**"));
        } else {
            getLogger().info("✅ Сервер запущен через Paper.");
            telegramBot = new TelegramBot(botToken, chatIds);
            telegramBot.sendMessage(getConfig().getString("messages.server_started", "✅ **Paper-сервер запущен!**"));
        }

        if (updateEnabled) {
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> new PluginUpdater().checkForUpdates());
        }

        startTpsMonitor();
    }

    @Override
    public void onDisable() {
        running = false;

        if (telegramBot != null) {
            telegramBot.sendMessage(getConfig().getString("messages.server_stopped", "⛔ **Paper-сервер выключен!**"));
        }

        sendToVelocity("server_stopped", getConfig().getString("messages.server_stopped", "⛔ **Paper-сервер выключен!**"));

        getLogger().info("⛔ BWTelegramNotify отключен!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String message = getConfig().getString("messages.player_join", "🔵 **Игрок зашел на сервер:** {player}")
                .replace("{player}", event.getPlayer().getName());

        if (telegramBot != null) {
            telegramBot.sendMessage(message);
        } else {
            sendToVelocity("player_join", message);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String message = getConfig().getString("messages.player_quit", "⚪ **Игрок вышел с сервера:** {player}")
                .replace("{player}", event.getPlayer().getName());

        if (telegramBot != null) {
            telegramBot.sendMessage(message);
        } else {
            sendToVelocity("player_quit", message);
        }
    }

    private void sendToVelocity(String action, String message) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "velocity_send " + action + " " + message);
    }

    private void startTpsMonitor() {
        new BukkitRunnable() {
            @Override
            public void run() {
                double tps = Bukkit.getTPS()[0];
                if (tps < tpsThreshold) {
                    String message = getConfig().getString("messages.low_tps", "⚠ **Низкий TPS:** {tps}")
                            .replace("{tps}", String.format("%.2f", tps));
                    if (telegramBot != null) {
                        telegramBot.sendMessage(message);
                    } else {
                        sendToVelocity("low_tps", message);
                    }
                }
            }
        }.runTaskTimer(this, 600L, 1200L);
    }

    private boolean isVelocityEnabled() {
        File paperConfigPath = new File(getServer().getWorldContainer(), "paper-global.yml");

        if (!paperConfigPath.exists()) {
            getLogger().warning("⚠ Файл paper-global.yml не найден! Проверяю в config/...");
            paperConfigPath = new File(getServer().getWorldContainer(), "config/paper-global.yml");

            if (!paperConfigPath.exists()) {
                getLogger().warning("⚠ Не найден ни один файл paper-global.yml! Невозможно определить, работает ли сервер за Velocity.");
                return false;
            }
        }

        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(paperConfigPath);
            boolean velocityEnabled = config.getBoolean("proxies.velocity.enabled", false);
            getLogger().info("🔍 Проверка paper-global.yml: proxies.velocity.enabled = " + velocityEnabled);
            return velocityEnabled;
        } catch (Exception e) {
            getLogger().severe("❌ Ошибка при чтении paper-global.yml: " + e.getMessage());
            return false;
        }
    }

    private class StatusCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            String message = "📢 BWTelegramNotify:\n"
                    + "Сервер: " + (isVelocity ? "Paper за Velocity" : "Paper");

            sender.sendMessage(message);
            getLogger().info(message);
            return true;
        }
    }
}