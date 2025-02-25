package me.projectbw.BWTelegramNotify;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class PaperMain extends JavaPlugin implements Listener {
    private static boolean running = false;
    private TelegramBot telegramBot;
    private double tpsThreshold;
    private boolean updateEnabled;

    @Override
    public void onEnable() {
        running = true;
        saveDefaultConfig();
        reloadConfig();

        String botToken = getConfig().getString("telegram.botToken", "").trim();
        List<String> chatIds = getConfig().getStringList("telegram.chatIds");

        // Проверяем, что токен и список чатов не пустые
        if (botToken.isEmpty() || chatIds.isEmpty()) {
            getLogger().severe("❌ Ошибка: botToken или chatIds не указаны в config.yml! Отключение плагина...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        tpsThreshold = getConfig().getDouble("settings.tps", 15.0);
        updateEnabled = getConfig().getBoolean("settings.update", true);

        if (Bukkit.getPluginManager().getPlugin("Velocity") != null) {
            // Сервер работает через Velocity, отправляем сообщения в Velocity
            sendToVelocity("server_started", getConfig().getString("messages.server_started", "✅ **Paper-сервер запущен!**"));
        } else {
            // Сервер работает на Paper, создаем и запускаем Telegram-бота
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
        // Отправляем команду на сервер Velocity, если он используется
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

    private class StatusCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            String message = "📢 BWTelegramNotify:\n"
                    + "Сервер: " + (Bukkit.getPluginManager().getPlugin("Velocity") != null ? "Velocity" : "Paper");

            sender.sendMessage(message);
            getLogger().info(message);
            return true;
        }
    }
}