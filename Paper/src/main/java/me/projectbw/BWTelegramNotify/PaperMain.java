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
import org.json.JSONObject;
import org.json.JSONArray;

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
        if (!getDataFolder().exists() && !getDataFolder().mkdirs()) {
            logger.warning("Не удалось создать папку конфигурации!");
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

    private void loadConfig() {
        File configFile = new File(getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            saveResource("config.yml", false);
            logger.info("Создан новый config.yml");
        }

        this.config = YamlConfiguration.loadConfiguration(configFile);

        if (config == null) {
            logger.warning("Конфигурация не была загружена!");
        }

        // Устанавливаем значения по умолчанию
        config.addDefault("messages.server_started", "✅ **Сервер {server} запущен!**");
        config.addDefault("messages.server_stopped", "⛔ **Сервер {server} выключен!**");
        config.addDefault("messages.player_join", "🔵 **Игрок {player} зашел на сервер {server}**");
        config.addDefault("messages.player_quit", "⚪ **Игрок {player} вышел с сервера {server}**");
        config.options().copyDefaults(true);
        saveConfig();
    }

    private String getServerName() {
        return Bukkit.getServer().getName();
    }

    private void checkTPS() {
        double[] tpsArray = Bukkit.getServer().getTPS();
        if (tpsArray.length > 0) {
            double tps = tpsArray[0];
            if (tps < TPS_THRESHOLD) {
                logger.warning("Внимание: низкий TPS: " + tps);
            }
        } else {
            logger.warning("Не удалось получить данные о TPS.");
        }
    }

    private void startTPSMonitoring() {
        new BukkitRunnable() {
            @Override
            public void run() {
                checkTPS();
            }
        }.runTaskTimerAsynchronously(this, 0L, 1200L);
    }

    public void checkForUpdates() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(GITHUB_API_URL).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                logger.warning("Ошибка получения обновлений: " + responseCode);
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            String latestVersion = jsonResponse.getString("tag_name");

            JSONArray assets = jsonResponse.optJSONArray("assets");
            if (assets == null) {
                logger.warning("Не найдено файлов для загрузки.");
                return;
            }

            String downloadUrl = null;
            for (int i = 0; i < assets.length(); i++) {
                JSONObject asset = assets.getJSONObject(i);
                String assetName = asset.getString("name");
                if (assetName.startsWith("BWTelegramNotify-Paper")) {
                    downloadUrl = asset.getString("browser_download_url");
                    break;
                }
            }

            if (downloadUrl == null) {
                logger.warning("Не найдено подходящих файлов для обновления.");
                return;
            }

            logger.info("Доступна новая версия: " + latestVersion);
            downloadNewVersion(downloadUrl, latestVersion);

        } catch (Exception e) {
            logger.warning("Ошибка при проверке обновлений: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void downloadNewVersion(String downloadUrl, String latestVersion) {
        try {
            logger.info("Загрузка обновления с: " + downloadUrl);
            URL url = new URL(downloadUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                logger.warning("Ошибка загрузки файла: " + responseCode);
                return;
            }

            File pluginFile = new File("plugins/BWTelegramNotify-Paper.jar");
            try (InputStream inputStream = connection.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(pluginFile)) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            logger.info("Плагин обновлен до версии " + latestVersion + "!");
        } catch (Exception e) {
            logger.warning("Ошибка при загрузке обновления: " + e.getMessage());
            e.printStackTrace();
        }
    }
}