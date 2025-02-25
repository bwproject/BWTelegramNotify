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
    private PluginUpdater pluginUpdater;
    private double tpsThreshold;

    @Override
    public void onEnable() {
        running = true;
        saveDefaultConfig();
        reloadConfig();

        List<String> chatIds = getConfig().getStringList("telegram.chatIds");
        String botToken = getConfig().getString("telegram.token");
        tpsThreshold = getConfig().getDouble("tps_threshold", 15.0);

        telegramBot = new TelegramBot(botToken, chatIds);
        pluginUpdater = new PluginUpdater();

        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("bwstatusbot").setExecutor(new StatusCommand());

        getServer().getConsoleSender().sendMessage("\n§a==============================\n"
                + "§a=== Плагин BWTelegramNotify активен ===\n"
                + "§a==============================");

        getLogger().info("Telegram-бот запущен: " + telegramBot.getBotName() + " (@" + telegramBot.getBotUsername() + ")");

        telegramBot.sendMessage(getConfig().getString("messages.server_started", "✅ **Paper-сервер запущен!**"));

        // Проверка обновлений при запуске
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> pluginUpdater.checkForUpdates());

        // Запуск мониторинга TPS
        startTpsMonitor();
    }

    @Override
    public void onDisable() {
        running = false;
        telegramBot.sendMessage(getConfig().getString("messages.server_stopped", "⛔ **Paper-сервер выключен!**"));
        getLogger().info("⛔ BWTelegramNotify отключен!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String message = getConfig().getString("messages.player_join", "🔵 **Игрок зашел на сервер:** {player}")
                .replace("{player}", event.getPlayer().getName());
        telegramBot.sendMessage(message);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String message = getConfig().getString("messages.player_quit", "⚪ **Игрок вышел с сервера:** {player}")
                .replace("{player}", event.getPlayer().getName());
        telegramBot.sendMessage(message);
    }

    private void startTpsMonitor() {
        new BukkitRunnable() {
            @Override
            public void run() {
                double tps = Bukkit.getTPS()[0];
                if (tps < tpsThreshold) {
                    String message = getConfig().getString("messages.low_tps", "⚠ **Низкий TPS:** {tps}")
                            .replace("{tps}", String.format("%.2f", tps));
                    telegramBot.sendMessage(message);
                }
            }
        }.runTaskTimer(this, 600L, 1200L); // Проверка каждую минуту (20 тиков * 60 сек)
    }

    private class StatusCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            String message = "📢 BWTelegramNotify:\n"
                    + "Бот: " + telegramBot.getBotName() + " (@" + telegramBot.getBotUsername() + ")\n"
                    + "Сервер: Paper";

            sender.sendMessage(message);
            getLogger().info(message);
            return true;
        }
    }
}