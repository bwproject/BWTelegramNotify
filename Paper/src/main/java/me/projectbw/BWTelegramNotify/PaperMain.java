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
    private boolean updateEnabled;

    @Override
    public void onEnable() {
        running = true;
        saveDefaultConfig();
        reloadConfig();

        List<String> chatIds = getConfig().getStringList("telegram.chatIds");
        String botToken = getConfig().getString("telegram.botToken", "").trim();
        tpsThreshold = getConfig().getDouble("settings.tps", 15.0);
        updateEnabled = getConfig().getBoolean("settings.update", true);

        if (botToken.isEmpty() || chatIds.isEmpty()) {
            getLogger().severe("❌ Ошибка: botToken или chatIds не указаны в config.yml! Отключение плагина...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        telegramBot = new TelegramBot(botToken, chatIds);
        pluginUpdater = new PluginUpdater();

        Bukkit.getPluginManager().registerEvents(this, this);

        if (getCommand("bwstatusbot") != null) {
            getCommand("bwstatusbot").setExecutor(new StatusCommand());
        } else {
            getLogger().warning("⚠ Команда /bwstatusbot не зарегистрирована в plugin.yml!");
        }

        getServer().getConsoleSender().sendMessage("\n§a==============================\n"
                + "§a=== Плагин BWTelegramNotify активен ===\n"
                + "§a==============================");

        getLogger().info("✅ Telegram-бот запущен: " + telegramBot.getBotName() + " (@" + telegramBot.getBotUsername() + ")");

        telegramBot.sendMessage(getConfig().getString("messages.server_started", "✅ **Paper-сервер запущен!**"));

        if (updateEnabled) {
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> pluginUpdater.checkForUpdates());
        }

        startTpsMonitor();
    }

    @Override
    public void onDisable() {
        running = false;

        if (telegramBot != null) {
            telegramBot.sendMessage(getConfig().getString("messages.server_stopped", "⛔ **Paper-сервер выключен!**"));
        }

        getLogger().info("⛔ BWTelegramNotify отключен!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (telegramBot == null) return;

        String message = getConfig().getString("messages.player_join", "🔵 **Игрок зашел на сервер:** {player}")
                .replace("{player}", event.getPlayer().getName());
        telegramBot.sendMessage(message);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (telegramBot == null) return;

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
                    if (telegramBot == null) return;

                    String message = getConfig().getString("messages.low_tps", "⚠ **Низкий TPS:** {tps}")
                            .replace("{tps}", String.format("%.2f", tps));
                    telegramBot.sendMessage(message);
                }
            }
        }.runTaskTimer(this, 600L, 1200L);
    }

    private class StatusCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (telegramBot == null) {
                sender.sendMessage("❌ Бот не запущен! Проверьте конфигурацию.");
                return true;
            }

            String message = "📢 BWTelegramNotify:\n"
                    + "Бот: " + telegramBot.getBotName() + " (@" + telegramBot.getBotUsername() + ")\n"
                    + "Сервер: Paper";

            sender.sendMessage(message);
            getLogger().info(message);
            return true;
        }
    }
}