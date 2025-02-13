package me.projectbw;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class StatusCommand implements CommandExecutor {

    private final TelegramSender telegramSender;

    public StatusCommand(TelegramSender telegramSender) {
        this.telegramSender = telegramSender;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.YELLOW + "[BWTelegramNotify] Проверка статуса бота...");

        boolean isOnline = telegramSender.checkBotStatus();
        if (isOnline) {
            sender.sendMessage(ChatColor.GREEN + "[BWTelegramNotify] Бот Telegram работает.");
        } else {
            sender.sendMessage(ChatColor.RED + "[BWTelegramNotify] Бот Telegram недоступен!");
        }
        return true;
    }
}
