package me.projectbw.BWTelegramNotify;

import com.destroystokyo.paper.event.server.ServerListPingEvent;
import com.google.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.logging.Logger;

public class PaperMain extends JavaPlugin implements Listener {
    private static final Logger logger = Logger.getLogger("BWTelegramNotify");
    private TelegramBot telegramBot;
    private List<String> chatIds;
    private double lowTpsThreshold;
    
    @Inject
    public PaperMain() {
    }

    @Override
    public void onEnable() {
        // Загружаем конфигурацию
        saveDefaultConfig();
        String botUsername = getConfig().getString("bot.username");
        String botToken = getConfig().getString("bot.token");
        this.chatIds = getConfig().getStringList("chatIds");
        this.lowTpsThreshold = getConfig().getDouble("low_tps_threshold", 16.0); // Пример: 16 TPS
        
        // Инициализируем бота
        telegramBot = new TelegramBot(botUsername, botToken);
        
        // Подключаем слушатели событий
        getServer().getPluginManager().registerEvents(this, this);
        
        // Логируем сообщение о старте плагина
        logger.info("BWTelegramNotify плагин включен!");
        
        // Запускаем задачу для отслеживания TPS каждые 10 секунд
        new BukkitRunnable() {
            @Override
            public void run() {
                checkLowTps();
            }
        }.runTaskTimer(this, 0L, 200L); // 200L == 10 секунд
    }

    // Отслеживание низкого TPS
    private void checkLowTps() {
        double tps = getServer().getTPS()[0];
        if (tps < lowTpsThreshold) {
            String message = String.format("Внимание! Низкий TPS: %.2f", tps);
            telegramBot.sendMessages(chatIds, message);
            logger.warning("Низкий TPS: " + tps);
        }
    }

    // Отправка сообщения о входе игрока
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();
        String joinMessage = getConfig().getString("notifications.paper.join");
        joinMessage = joinMessage.replace("{player}", playerName);
        telegramBot.sendMessages(chatIds, joinMessage);

        logger.info("Игрок " + playerName + " вошел в игру. Сообщение отправлено в Telegram.");
    }

    // Отправка сообщения о выходе игрока
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String playerName = event.getPlayer().getName();
        String quitMessage = getConfig().getString("notifications.paper.quit");
        quitMessage = quitMessage.replace("{player}", playerName);
        telegramBot.sendMessages(chatIds, quitMessage);

        logger.info("Игрок " + playerName + " покинул сервер. Сообщение отправлено в Telegram.");
    }
}
