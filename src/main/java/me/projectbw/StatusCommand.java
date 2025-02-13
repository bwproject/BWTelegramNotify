package me.projectbw;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StatusCommand implements CommandExecutor {
    private final TelegramSender telegramSender;

    public StatusCommand(TelegramSender telegramSender) {
        this.telegramSender = telegramSender;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean isActive = telegramSender.checkBotStatus();
        sender.sendMessage(ChatColor.GREEN + "[TelegramNotify] Статус: " + (isActive ? "Активен" : "Неактивен"));
        return true;
    }
}
