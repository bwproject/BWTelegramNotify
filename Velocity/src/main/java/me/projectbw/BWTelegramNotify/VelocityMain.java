package me.projectbw.BWTelegramNotify;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Plugin(
    id = "bwtelegramnotify",
    name = "BWTelegramNotify",
    version = "1.1.0",
    description = "Плагин для уведомлений в Telegram",
    authors = {"The_Mr_Mes109"}
)
public class VelocityMain {
    private final ProxyServer server;
    private final Logger logger;
    private final Path configFile;
    private TelegramBot telegramBot;
    private YamlConfiguration config;
    private boolean fakePlayerEnabled;
    private String fakePlayerName;

    @Inject
    public VelocityMain(ProxyServer server, Logger logger, @com.velocitypowered.api.plugin.annotation.DataDirectory Path dataFolder) {
        this.server = server;
        this.logger = logger;
        this.configFile = dataFolder.resolve("config.yml");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("==================================");
        logger.info("=== BWTelegramNotify загружается ===");
        logger.info("==================================");

        loadConfig();

        // Проверка наличия обновлений
        PluginUpdater updater = new PluginUpdater();
        updater.checkForUpdates();

        // Сообщение о запуске прокси
        if (telegramBot != null) {
            telegramBot.sendMessage(config.getString("messages.server_started", "🔵 **Прокси-сервер запущен!**"));
        }

        // Регистрируем слушатель событий
        server.getEventManager().register(this, new VelocityListener(server, logger, this));

        logger.info("BWTelegramNotify успешно загружен!");
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        logger.info("BWTelegramNotify: Остановка плагина...");
        if (telegramBot != null) {
            telegramBot.sendMessage(config.getString("messages.server_stopped", "🔴 **Прокси-сервер выключен!**"));
        }
        logger.info("BWTelegramNotify успешно отключен.");
    }

    @Subscribe
    public void onPlayerLogin(LoginEvent event) {
        String playerName = event.getPlayer().getUsername();

        if (fakePlayerEnabled && playerName.equalsIgnoreCase(fakePlayerName)) {
            event.getPlayer().disconnect("Этот ник зарезервирован для системы.");
            return;
        }

        logger.info("Игрок зашел: " + playerName);
        if (telegramBot != null) {
            telegramBot.sendMessage(config.getString("messages.player_logged_in", "✅ **Игрок зашел**: %player%").replace("%player%", playerName));
        }
    }

    @Subscribe
    public void onPlayerDisconnect(DisconnectEvent event) {
        String playerName = event.getPlayer().getUsername();

        logger.info("Игрок вышел: " + playerName);
        if (telegramBot != null) {
            telegramBot.sendMessage(config.getString("messages.player_logged_out", "❌ **Игрок вышел**: %player%").replace("%player%", playerName));
        }
    }

    @Subscribe
    public void onPlayerSwitchServer(ServerConnectedEvent event) {
        Player player = event.getPlayer();
        Optional<String> previousServer = event.getPreviousServer().map(server -> server.getServerInfo().getName());
        String newServer = event.getServer().getServerInfo().getName();

        logger.info("Игрок " + player.getUsername() + " сменил сервер: " + previousServer.orElse("null") + " -> " + newServer);

        if (telegramBot != null) {
            String message;
            if (previousServer.isPresent()) {
                message = config.getString("messages.player_switched_server", "🔄 **Игрок сменил сервер**: %player%\n➡ **%previous_server%** → **%new_server%**");
                telegramBot.sendMessage(message
                        .replace("%player%", player.getUsername())
                        .replace("%previous_server%", previousServer.get())
                        .replace("%new_server%", newServer));
            } else {
                message = config.getString("messages.player_joined_server", "➡ **Игрок зашел на сервер**: %player%\n🟢 **Сервер**: %new_server%");
                telegramBot.sendMessage(message
                        .replace("%player%", player.getUsername())
                        .replace("%new_server%", newServer));
            }
        }
    }

    private void loadConfig() {
        logger.info("Загрузка config.yml...");

        if (!Files.exists(configFile)) {
            try (InputStream inputStream = getClass().getResourceAsStream("/config.yml")) {
                if (inputStream == null) {
                    logger.severe("Не найден config.yml в ресурсах! Проверьте contents JAR-файла.");
                    return;
                }

                Files.createDirectories(configFile.getParent());
                Files.copy(inputStream, configFile, StandardCopyOption.REPLACE_EXISTING);
                logger.info("config.yml скопирован из ресурсов.");
            } catch (IOException e) {
                logger.severe("Ошибка при создании config.yml: " + e.getMessage());
                return;
            }
        }

        try {
            config = YamlConfiguration.loadConfiguration(configFile.toFile());
            logger.info("config.yml загружен успешно.");
        } catch (Exception e) {
            logger.severe("Ошибка при загрузке config.yml: " + e.getMessage());
            return;
        }

        String botToken = config.getString("telegram.token", "");
        List<String> chatIds = config.getStringList("telegram.chats");

        if (botToken.isEmpty() || chatIds.isEmpty()) {
            logger.severe("В config.yml не указан токен или список чатов! Бот не будет запущен.");
            return;
        }

        telegramBot = new TelegramBot(botToken, chatIds);
        logger.info("Telegram-бот запущен.");

        fakePlayerEnabled = config.getBoolean("fake_player.enabled", true);
        fakePlayerName = config.getString("fake_player.name", "projectbw.ru");
    }

    public TelegramBot getTelegramBot() {
        return telegramBot;
    }

    // Этот метод будет передавать сообщение в Telegram через бот
    public void forwardMessageToTelegram(String message) {
        if (telegramBot != null) {
            telegramBot.sendMessage(message);
        }
    }
}