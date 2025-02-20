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

import java.util.List;

public class PaperMain extends JavaPlugin implements Listener {
    private static boolean running = false;
    private TelegramBot telegramBot;

    @Override
    public void onEnable() {
        running = true;
        saveDefaultConfig();
        List<String> chatIds = getConfig().getStringList("telegram.chatIds");
        String botToken = getConfig().getString("telegram.token");

        telegramBot = new TelegramBot(botToken, chatIds);

        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("bwstatusbot").setExecutor(new StatusCommand());

        getServer().getConsoleSender().sendMessage("\n§a==============================\n"
                + "§a=== Плагин BWTelegramNotify активен ===\n"
                + "§a==============================");

        getLogger().info("Telegram-бот запущен: " + telegramBot.getBotName() + " (@" + telegramBot.getBotUsername() + ")");

        telegramBot.sendMessage("✅ **Paper-сервер запущен!**");
    }

    @Override
    public void onDisable() {
        running = false;
        telegramBot.sendMessage("⛔ **Paper-сервер выключен!**");
        getLogger().info("⛔ BWTelegramNotify отключен!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        telegramBot.sendMessage("🔵 **Игрок зашел на сервер:** " + event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        telegramBot.sendMessage("⚪ **Игрок вышел с сервера:** " + event.getPlayer().getName());
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