package me.projectbw.BWTelegramNotify;

import com.destroystokyo.paper.event.server.ServerListPingEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class PaperMain extends JavaPlugin implements Listener {

    private TelegramBot telegramBot;
    private List<String> chatIds;
    private long lastTpsCheck = System.currentTimeMillis();

    @Override
    public void onEnable() {
        // Регистрируем события
        getServer().getPluginManager().registerEvents(this, this);
        // Инициализация бота
        telegramBot = new TelegramBot("YOUR_BOT_USERNAME", "YOUR_BOT_TOKEN");
        chatIds = getConfig().getStringList("telegram.chat_ids");
    }

    // Обработчик события входа игрока
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();
        String joinMessage = getConfig().getString("notifications.paper.join").replace("{player}", playerName);
        sendTelegramMessage(joinMessage);
    }

    // Обработчик события выхода игрока
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String playerName = event.getPlayer().getName();
        String quitMessage = getConfig().getString("notifications.paper.quit").replace("{player}", playerName);
        sendTelegramMessage(quitMessage);
    }

    // Обработчик пинга от клиента
    @EventHandler
    public void onPing(ServerListPingEvent event) {
        String pingMessage = "Игроки сейчас могут подключаться!";
        sendTelegramMessage(pingMessage);
    }

    // Проверка TPS на сервере
    @Override
    public void onTick() {
        long currentTime = System.currentTimeMillis();

        // Проверка TPS каждые 5 секунд
        if (currentTime - lastTpsCheck >= 5000) {
            lastTpsCheck = currentTime;

            double tps = getServer().getTPS()[0]; // Получаем текущий TPS (самый быстрый)
            if (tps < 18.0) { // Порог для низкого TPS (например, 18 TPS)
                String tpsMessage = getConfig().getString("notifications.paper.low_tps").replace("{tps}", String.valueOf(tps));
                sendTelegramMessage(tpsMessage);
            }
        }
    }

    // Отправка сообщений в Telegram
    private void sendTelegramMessage(String message) {
        telegramBot.sendMessages(chatIds, message);
    }
}
