// VelocityMain.java
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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private YamlConfiguration config;
    private TelegramBot telegramBot;
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

        try {
            loadConfig();
        } catch (IOException e) {
            logger.severe("Ошибка при загрузке конфигурации: " + e.getMessage());
            return;
        }

        // Сообщение о запуске прокси
        if (telegramBot != null) {
            telegramBot.sendMessage(config.getString("messages.server_started", "🔵 **Прокси-сервер запущен!**"));
        }

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

    private void loadConfig() throws IOException {  // добавлено "throws IOException"
        logger.info("Загрузка config.yml...");

        // Копирование из ресурсов, если файла нет
        if (!Files.exists(configFile)) {
            try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.yml")) {
                if (in == null) {
                    logger.severe("Не найден config.yml в ресурсах! Проверь, что он есть в src/main/resources.");
                    return;
                }

                Files.createDirectories(configFile.getParent());
                Files.copy(in, configFile);
                logger.warning("Создан новый config.yml из ресурсов.");
            } catch (IOException e) {
                logger.severe("Ошибка при копировании config.yml: " + e.getMessage());
                throw e;  // выбрасываем исключение дальше
            }
        }

        // Загрузка конфига
        config = YamlConfiguration.loadConfiguration(configFile.toFile());
        logger.info("config.yml загружен успешно.");

        // Чтение настроек
        fakePlayerEnabled = config.getBoolean("fake_player.enabled", true);
        fakePlayerName = config.getString("fake_player.name", "projectbw.ru");

        // Настройка Telegram-бота
        String botToken = config.getString("telegram.token", "");
        if (!botToken.isEmpty()) {
            telegramBot = new TelegramBot(botToken, config.getStringList("telegram.chats"));
            logger.info("Telegram-бот запущен.");
        } else {
            logger.warning("Токен Telegram-бота не указан, бот не будет работать.");
        }
    }

    public boolean isFakePlayerEnabled() {
        return fakePlayerEnabled;
    }

    public String getFakePlayerName() {
        return fakePlayerName;
    }

    // Новый метод для передачи сообщения в Telegram
    public void forwardMessageToTelegram(String message) {
        if (telegramBot != null) {
            telegramBot.sendMessage(message);
        } else {
            logger.warning("Telegram-бот не настроен. Сообщение не отправлено.");
        }
    }
}