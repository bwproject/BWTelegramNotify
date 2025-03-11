package me.projectbw.BWTelegramNotify;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class PaperMain extends JavaPlugin implements Listener, PluginMessageListener {

    private Logger logger;
    private static final double TPS_THRESHOLD = 15.0;
    private static final String CHANNEL = "bwtelegram:notify";

    @Override
    public void onEnable() {
        this.logger = getLogger();
        logger.info("BWTelegramNotify плагин включен!");

        // Загружаем конфиг
        saveDefaultConfig();

        // Регистрируем канал плагиновых сообщений
        getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL);
        getServer().getMessenger().registerIncomingPluginChannel(this, CHANNEL, this);

        // Регистрируем события
        getServer().getPluginManager().registerEvents(this, this);

        // Логируем сообщение при старте сервера
        sendMessageToVelocity("server_started", getServerName());

        // Запускаем мониторинг TPS
        startTPSMonitoring();
    }

    @Override
    public void onDisable() {
        sendMessageToVelocity("server_stopped", getServerName());
        logger.info("BWTelegramNotify плагин отключен.");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        sendMessageToVelocity("player_join", event.getPlayer().getName(), getServerName());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        sendMessageToVelocity("player_quit", event.getPlayer().getName(), getServerName());
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        checkTPS();
    }

    private String getServerName() {
        return Bukkit.getServer().getName();
    }

    private void sendMessageToVelocity(String messageKey, String... args) {
        String message = formatMessage(messageKey, args);
        sendViaPluginMessage(message);
    }

    private String formatMessage(String messageKey, String... args) {
        String messageTemplate = getMessageTemplate(messageKey);
        return String.format(messageTemplate, (Object[]) args);
    }

    private String getMessageTemplate(String messageKey) {
        switch (messageKey) {
            case "server_started":
                return "✅ Сервер %s запущен!";
            case "server_stopped":
                return "⛔ Сервер %s остановлен!";
            case "player_join":
                return "🔵 Игрок %s присоединился к серверу %s!";
            case "player_quit":
                return "⚪ Игрок %s покинул сервер %s!";
            case "low_tps":
                return "⚠ Низкий TPS: %s на сервере %s";
            default:
                return "Неизвестное сообщение.";
        }
    }

    private void sendViaPluginMessage(String message) {
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendPluginMessage(this, CHANNEL, messageBytes);
            return; // Отправляем только одному игроку, так как сообщение дойдет до Velocity
        }
        logger.warning("Нет игроков онлайн, сообщение не отправлено в Velocity.");
    }

    private void checkTPS() {
        double tps = Bukkit.getServer().getTPS()[0];
        if (tps < TPS_THRESHOLD) {
            sendMessageToVelocity("low_tps", String.valueOf(tps), getServerName());
        }
    }

    private void startTPSMonitoring() {
        new BukkitRunnable() {
            @Override
            public void run() {
                checkTPS();
            }
        }.runTaskTimerAsynchronously(this, 0L, 1200L); // Каждые 60 секунд
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals(CHANNEL)) return;
        String receivedMessage = new String(message, StandardCharsets.UTF_8);
        logger.info("📩 Получено сообщение из Velocity: " + receivedMessage);
    }
}