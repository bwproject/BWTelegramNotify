package me.projectbw.BWTelegramNotify;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.plugin.PluginDisableEvent;  // Используем событие PluginDisableEvent
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

public class PaperMain extends JavaPlugin implements Listener {
    private YamlConfiguration config;
    private Logger logger;
    private static final String GITHUB_API_URL = "https://api.github.com/repos/bwproject/BWTelegramNotify/releases/latest";
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

        // Проверяем наличие обновлений
        checkForUpdates();

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

    // Используем событие отключения плагина для обработки выключения сервера
    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin() == this) {
            String message = config.getString("messages.server_stopped", "⛔ **Сервер {server} выключен!**")
                    .replace("{server}", getServerName());
            logger.info(message);
        }
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
        double tps = Bukkit.getServer().getTPS()[0];
        if (tps < TPS_THRESHOLD) {
            String message = "Внимание: низкий TPS: " + tps;
            Bukkit.getLogger().warning(message);
        }
    }

    // Запуск мониторинга TPS
    private void startTPSMonitoring() {
        new BukkitRunnable() {
            @Override
            public void run() {
                double tps = Bukkit.getServer().getTPS()[0];
                if (tps < TPS_THRESHOLD) {
                    String message = "Внимание: низкий TPS: " + tps;
                    Bukkit.getLogger().warning(message);
                }
            }
        }.runTaskTimerAsynchronously(this, 0L, 1200L);
    }

    // Проверка на наличие обновлений плагина
    public void checkForUpdates() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(GITHUB_API_URL).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            String latestVersion = jsonResponse.getString("tag_name");

            JSONArray assets = jsonResponse.getJSONArray("assets");
            String downloadUrl = null;

            // Ищем файл с "BWTelegramNotify-Paper" в имени
            for (int i = 0; i < assets.length(); i++) {
                JSONObject asset = assets.getJSONObject(i);
                String assetName = asset.getString("name");
                if (assetName.startsWith("BWTelegramNotify-Paper")) {
                    downloadUrl = asset.getString("browser_download_url");
                    break;
                }
            }

            if (downloadUrl == null) {
                System.out.println("Не удалось найти нужный файл для загрузки.");
                return;
            }

            System.out.println("Новая версия доступна: " + latestVersion);
            downloadNewVersion(downloadUrl, latestVersion);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downloadNewVersion(String downloadUrl, String latestVersion) {
        try {
            System.out.println("Загрузка файла: " + downloadUrl);
            URL url = new URL(downloadUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream inputStream = connection.getInputStream();
            FileOutputStream outputStream = new FileOutputStream("plugins/BWTelegramNotify-Paper.jar");

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            System.out.println("Плагин обновлен до версии " + latestVersion + "!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}