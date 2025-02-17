package me.projectbw.BWTelegramNotify;

import org.bukkit.Bukkit;

public class TelegramBot {
    private final String botToken = "YOUR_BOT_TOKEN";
    private final String chatId = "YOUR_CHAT_ID";

    public void sendMessage(String message) {
        // Логика отправки сообщения в Telegram
        Bukkit.getLogger().info("Sending message to Telegram: " + message);
    }
}
