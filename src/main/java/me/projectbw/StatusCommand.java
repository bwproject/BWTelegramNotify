package me.projectbw;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class StatusCommand implements CommandExecutor {
    private final TelegramSender telegramSender;

    public StatusCommand(TelegramSender telegramSender) {
        this.telegramSender = telegramSender;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("status")) {
            String statusMessage = telegramSender.checkBotStatus();
            sender.sendMessage("§aСтатус Telegram-бота: " + statusMessage);
            return true;
        }
        return false;
    }
}