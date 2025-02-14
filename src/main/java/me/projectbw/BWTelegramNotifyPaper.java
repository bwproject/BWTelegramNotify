package me.projectbw;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;

public class BWTelegramNotifyPaper extends JavaPlugin {
    private TelegramSender telegramSender;
    private TPSListener tpsListener;

    @Override
    public void onEnable() {
        // Цветной лог в консоль
        getLogger().info("\u001b[32m[INFO] BWTelegramNotify плагин активен!");  // Зеленый цвет

        // Инициализация и регистрация listener
        this.telegramSender = new TelegramSender("your_bot_token", "your_chat_id");  // Пример значений
        this.tpsListener = new TPSListener(telegramSender, 18.0, 60);  // Порог TPS 18 и интервал 60 секунд

        // Регистрация TPSListener
        Bukkit.getServer().getPluginManager().registerEvents(this.tpsListener, this);
    }
}