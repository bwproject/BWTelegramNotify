package me.projectbw.BWTelegramNotify;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

public class PaperMain extends JavaPlugin implements Listener {

    private Logger logger;
    private static final double TPS_THRESHOLD = 15.0;
    private static final String VELOCITY_SERVER_HOST = "localhost";  // IP Velocity-сервера
    private static final int VELOCITY_SERVER_PORT = 12345;  // Порт для связи с Velocity

    @Override
    public void onEnable() {
        this.logger = getLogger();
        logger.info("BWTelegramNotify плагин включен!");

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
        sendViaSocket(message);
    }

    private String formatMessage(String messageKey, String... args) {
        // Для простоты будем просто заменять placeholder в сообщении на реальные значения
        String messageTemplate = getMessageTemplate(messageKey);
        return String.format(messageTemplate, (Object[]) args);
    }

    private String getMessageTemplate(String messageKey) {
        // Вернем строку, которая будет использоваться для каждого типа сообщения.
        // Здесь можно загружать шаблоны из конфигурации
        switch (messageKey) {
            case "server_started":
                return "✅ Сервер {server} запущен!";
            case "server_stopped":
                return "⛔ Сервер {server} остановлен!";
            case "player_join":
                return "🔵 Игрок {player} присоединился к серверу {server}!";
            case "player_quit":
                return "⚪ Игрок {player} покинул сервер {server}!";
            case "low_tps":
                return "⚠ Низкий TPS: {tps} на сервере {server}";
            default:
                return "Неизвестное сообщение.";
        }
    }

    private void sendViaSocket(String message) {
        try (Socket socket = new Socket(VELOCITY_SERVER_HOST, VELOCITY_SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println(message); // Отправляем сообщение на Velocity через сокет
        } catch (IOException e) {
            logger.severe("Ошибка при отправке сообщения на Velocity через сокет: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void checkTPS() {
        double tps = Bukkit.getServer().getTPS()[0];
        if (tps < TPS_THRESHOLD) {
            sendMessageToVelocity("low_tps", String.valueOf(tps), getServerName());
        }
    }

    // Запуск мониторинга TPS
    private void startTPSMonitoring() {
        new BukkitRunnable() {
            @Override
            public void run() {
                checkTPS();
            }
        }.runTaskTimerAsynchronously(this, 0L, 1200L); // Каждые 60 секунд
    }
}