package me.projectbw.BWTelegramNotify;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

public class PaperMain extends JavaPlugin implements Listener {

    private YamlConfiguration config;
    private Logger logger;
    private static final double TPS_THRESHOLD = 15.0;
    private static final String VELOCITY_SERVER_ADDRESS = "http://velocity-server-address";  // URL для отправки сообщений на Velocity

    @Override
    public void onEnable() {
        try {
            this.logger = getLogger();

            // Создаем папку плагина, если она не существует
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }

            // Загружаем конфигурацию
            loadConfig();

            // Логируем сообщение при запуске сервера
            sendMessageToVelocity("server_started", getServerName());

            // Регистрируем события
            getServer().getPluginManager().registerEvents(this, this);

            // Запускаем мониторинг TPS
            startTPSMonitoring();

            logger.info("BWTelegramNotify успешно загружен!");
        } catch (IOException e) {
            logger.severe("Ошибка при загрузке конфигурации: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        sendMessageToVelocity("server_stopped", getServerName());
        logger.info("BWTelegramNotify отключен.");
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

    private void loadConfig() {
        // Инициализация конфигурации
        this.config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));

        // Загружаем дефолтные значения, если они не существуют
        config.addDefault("messages.server_started", "✅ **Сервер {server} запущен!**");
        config.addDefault("messages.server_stopped", "⛔ **Сервер {server} выключен!**");
        config.addDefault("messages.player_join", "🔵 **Игрок {player} зашел на сервер {server}**");
        config.addDefault("messages.player_quit", "⚪ **Игрок {player} вышел с сервера {server}**");
        config.addDefault("messages.low_tps", "⚠ Внимание: низкий TPS: {tps} на сервере {server}");
        config.options().copyDefaults(true);  // Копируем дефолтные значения в конфиг
        saveConfig();  // Сохраняем конфигурацию (если она была изменена)
    }

    private String getServerName() {
        return Bukkit.getServer().getName();
    }

    private void sendMessageToVelocity(String messageKey, String... args) {
        // Получаем шаблон сообщения из конфигурации
        String messageTemplate = config.getString("messages." + messageKey, "Сообщение не найдено");
        
        // Форматируем сообщение
        String message = String.format(messageTemplate, (Object[]) args);

        // Отправляем сообщение на сервер Velocity
        sendToVelocity(message);
    }

    private void sendToVelocity(String message) {
        try {
            URL url = new URL(VELOCITY_SERVER_ADDRESS + "/send-message?message=" + message);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.getResponseCode();  // Просто для выполнения запроса
        } catch (IOException e) {
            logger.severe("Ошибка при отправке сообщения на Velocity: " + e.getMessage());
        }
    }

    private void checkTPS() {
        try {
            double tps = Bukkit.getServer().getTPS()[0];
            if (tps < TPS_THRESHOLD) {
                sendMessageToVelocity("low_tps", String.valueOf(tps), getServerName());
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("Ошибка при получении TPS: " + e.getMessage());
            e.printStackTrace();
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