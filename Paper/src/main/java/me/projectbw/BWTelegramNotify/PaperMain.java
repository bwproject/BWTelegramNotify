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
import java.util.logging.Logger;

public class PaperMain extends JavaPlugin implements Listener {
    private YamlConfiguration config;
    private Logger logger;
    private static final double TPS_THRESHOLD = 15.0;

    @Override
    public void onEnable() {
        this.logger = getLogger();

        // Создаем папку плагина, если она не существует
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // Загружаем конфигурацию
        loadConfig();

        // Логируем сообщение при запуске сервера
        String message = config.getString("messages.server_started", "✅ **Сервер {server} запущен!**")
                .replace("{server}", getServerName());
        logger.info(message);

        // Регистрируем события
        getServer().getPluginManager().registerEvents(this, this);

        // Запускаем мониторинг TPS
        startTPSMonitoring();

        // Проверка на обновления плагина
        try {
            checkForPluginUpdates();  // Вызываем метод для проверки обновлений
        } catch (IOException e) {
            logger.severe("Ошибка при проверке обновлений плагина: " + e.getMessage());
            e.printStackTrace();
        }

        logger.info("BWTelegramNotify успешно загружен!");
    }

    @Override
    public void onDisable() {
        String message = config.getString("messages.server_stopped", "⛔ **Сервер {server} выключен!**")
                .replace("{server}", getServerName());
        logger.info(message);
        logger.info("BWTelegramNotify отключен.");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String message = config.getString("messages.player_join", "🔵 **Игрок {player} зашел на сервер {server}**")
                .replace("{player}", event.getPlayer().getName())
                .replace("{server}", getServerName());
        logger.info(message);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String message = config.getString("messages.player_quit", "⚪ **Игрок {player} вышел с сервера {server}**")
                .replace("{player}", event.getPlayer().getName())
                .replace("{server}", getServerName());
        logger.info(message);
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        checkTPS();
    }

    private void loadConfig() {
        // Инициализация конфигурации
        this.config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));

        if (config == null) {
            getLogger().warning("Конфигурация не была загружена!");
        }

        // Загружаем дефолтные значения, если они не существуют
        config.addDefault("messages.server_started", "✅ **Сервер {server} запущен!**");
        config.addDefault("messages.server_stopped", "⛔ **Сервер {server} выключен!**");
        config.addDefault("messages.player_join", "🔵 **Игрок {player} зашел на сервер {server}**");
        config.addDefault("messages.player_quit", "⚪ **Игрок {player} вышел с сервера {server}**");
        config.options().copyDefaults(true);  // Копируем дефолтные значения в конфиг
        saveConfig();  // Сохраняем конфигурацию (если она была изменена)
    }

    private String getServerName() {
        return Bukkit.getServer().getName();
    }

    private void checkTPS() {
        try {
            double tps = Bukkit.getServer().getTPS()[0];
            if (tps < TPS_THRESHOLD) {
                String message = "⚠ Внимание: низкий TPS: " + tps;
                Bukkit.getLogger().warning(message);
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
        }.runTaskTimerAsynchronously(this, 0L, 1200L);
    }

    // Проверка на наличие обновлений плагина
    private void checkForPluginUpdates() throws IOException {
        PluginUpdater pluginUpdater = new PluginUpdater();
        pluginUpdater.checkForUpdates();  // Вызываем метод из PluginUpdater для проверки обновлений
    }
}