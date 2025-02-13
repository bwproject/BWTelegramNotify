package me.projectbw;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class BWTelegramNotifyPaper extends JavaPlugin {
    private TelegramSender telegramSender;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        telegramSender = new TelegramSender(config);

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[PROJECTBW.RU] " + ChatColor.YELLOW + "BWTelegramNotify " + ChatColor.DARK_GREEN + "Активен");

        telegramSender.sendMessage("⚡ Paper сервер запущен!");

        getCommand("status").setExecutor(new StatusCommand(telegramSender));

        Bukkit.getPluginManager().registerEvents(new PlayerListener(telegramSender), this);
    }

    @Override
    public void onDisable() {
        telegramSender.sendMessage("⛔ Paper сервер выключен!");
    }
}
