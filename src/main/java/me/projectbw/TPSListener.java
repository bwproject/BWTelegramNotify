package me.projectbw;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class TPSListener implements Listener {
    private final TelegramSender telegramSender;
    private final double tpsThreshold;  // Порог TPS
    private final int checkInterval;    // Интервал проверки TPS (в секундах)

    public TPSListener(TelegramSender telegramSender, double tpsThreshold, int checkInterval) {
        this.telegramSender = telegramSender;
        this.tpsThreshold = tpsThreshold;
        this.checkInterval = checkInterval;
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        double tps = getTPS(); // Получаем текущий TPS
        if (tps < tpsThreshold) {
            String message = String.format("⚠️ Низкий TPS: %.2f. Порог: %.2f", tps, tpsThreshold);
            telegramSender.sendMessage(message);
        }
    }

    private double getTPS() {
        double[] tps = new double[3];
        Bukkit.getServer().getServerInfo().getTPS(tps);  // Получаем данные о TPS
        return tps[0];  // Возвращаем среднее значение TPS за последние 5 секунд
    }
}
