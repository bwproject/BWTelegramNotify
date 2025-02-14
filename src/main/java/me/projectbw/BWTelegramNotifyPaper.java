package me.projectbw;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BWTelegramNotifyPaper extends JavaPlugin implements Listener {
    private static final Logger logger = LoggerFactory.getLogger(BWTelegramNotifyPaper.class);
    private TelegramSender telegramSender;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.telegramSender = new TelegramSender(
            getConfig().getString("telegram.bot_token"), 
            getConfig().getStringList("telegram.chat_ids")
        );

        getServer().getPluginManager().registerEvents(this, this);

        // Цветной лог в консоль о запуске плагина
        String message = "§aПлагин BWTelegramNotify активен!";
        logger.info(message);

        // Также выводим через Adventure
        Component componentMessage = Component.text("Плагин BWTelegramNotify активен!")
                .color(TextColor.color(0x00FF00)); // Зеленый цвет
        logger.info(componentMessage.toString());

        // Отправка в Telegram
        telegramSender.sendMessage("Плагин BWTelegramNotify активен на сервере!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();
        telegramSender.sendMessage(getConfig().getString("server.player_join_message").replace("{player}", playerName));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String playerName = event.getPlayer().getName();
        telegramSender.sendMessage(getConfig().getString("server.player_quit_message").replace("{player}", playerName));
    }
}