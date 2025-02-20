package me.projectbw.BWTelegramNotify;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class PaperMain extends JavaPlugin implements Listener {
    private TelegramBot telegramBot;
    private static boolean running = false;

    @Override
    public void onEnable() {
        running = true;
        telegramBot = new TelegramBot("YOUR_BOT_TOKEN", List.of("CHAT_ID"));

        getLogger().info("\n==============================\n"
                        + "=== Плагин BWTelegramNotify активен ===\n"
                        + "==============================");

        getLogger().info("\u001B[32mTelegram-бот запущен: " + telegramBot.getBotName() + " (@" + telegramBot.getBotUsername() + ")\u001B[0m");

        getServer().getPluginManager().registerEvents(this, this);
        getCommand("bwstatusbot").setExecutor(new StatusCommand());

        telegramBot.sendMessage("✅ **Paper-сервер запущен!**");

        startTpsMonitor();
    }

    @Override
    public void onDisable() {
        running = false;
        telegramBot.sendMessage("⛔ **Paper-сервер выключен!**");
        getLogger().info("\u001B[31m⛔ BWTelegramNotify отключен!\u001B[0m");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        telegramBot.sendMessage("🔵 **Игрок зашел на сервер:** " + player.getName());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        telegramBot.sendMessage("⚪ **Игрок вышел с сервера:** " + player.getName());
    }

    private void startTpsMonitor() {
        new BukkitRunnable() {
            @Override
            public void run() {
                double tps = Bukkit.getTPS()[0];
                if (tps < 15) {
                    telegramBot.sendMessage("⚠ **Низкий TPS!**\nТекущий TPS: " + String.format("%.2f", tps));
                }
            }
        }.runTaskTimer(this, 0, 1200);
    }

    public static boolean isRunning() {
        return running;
    }

    private class StatusCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            String message = "📢 BWTelegramNotify:\n"
                    + "Бот: " + telegramBot.getBotName() + " (@" + telegramBot.getBotUsername() + ")\n"
                    + "Сервер: PaperMC";

            sender.sendMessage(message);
            getLogger().info(message);
            return true;
        }
    }
}