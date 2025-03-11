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
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private static final ChannelIdentifier CHANNEL = MinecraftChannelIdentifier.from("bwtelegram:notify");
    private VelocityListener velocityListener;
    private String fakePlayerName;
    private boolean fakePlayerEnabled;

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
        server.getChannelRegistrar().register(CHANNEL);

        // Отправка списка серверов
        sendServerListToTelegram();

        // Сообщение о запуске прокси
        if (telegramBot != null) {
            telegramBot.sendMessage(config.getString("messages.server_started", "🔵 **Прокси-сервер запущен!**"));
        }

        // Запуск VelocityListener
        if (config.getBoolean("velocity_listener.enabled", true)) {
            velocityListener = new VelocityListener(server, logger, this);
            server.getEventManager().register(this, velocityListener);
            logger.info("VelocityListener запущен и слушает сообщения от Paper.");
        }

        // Проверка и добавление фейкового игрока
        if (fakePlayerEnabled) {
            logger.info("Фейковый игрок включен. Используется ник: " + fakePlayerName);
            // Здесь можно реализовать механизм добавления фейкового игрока в список.
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

    private void loadConfig() {
        logger.info("Загрузка config.yml...");
        if (!Files.exists(configFile)) {
            try {
                Files.createDirectories(configFile.getParent());
                Files.createFile(configFile);
                Files.writeString(configFile, getDefaultConfig());
                logger.warning("Создан новый config.yml. Заполни его перед запуском!");
                return;
            } catch (IOException e) {
                logger.severe("Ошибка при создании config.yml: " + e.getMessage());
            }
        }

        try {
            config = YamlConfiguration.loadConfiguration(configFile.toFile());
            logger.info("config.yml загружен успешно.");
        } catch (IOException e) {
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
        logger.info("Telegram-бот запущен: " + telegramBot.getBotName() + " (@" + telegramBot.getBotUsername() + ")");

        fakePlayerEnabled = config.getBoolean("fake_player.enabled", true);
        fakePlayerName = config.getString("fake_player.name", "projectbw.ru");
    }

    private void sendServerListToTelegram() {
        StringBuilder serverList = new StringBuilder("🌐 **Доступные серверы:**\n");

        if (server.getAllServers().isEmpty()) {
            serverList.append("❌ Нет доступных серверов.");
        } else {
            for (RegisteredServer srv : server.getAllServers()) {
                serverList.append("➡ ").append(srv.getServerInfo().getName()).append("\n");
            }
        }

        logger.info(serverList.toString());
        if (telegramBot != null) {
            telegramBot.sendMessage(config.getString("messages.server_list", "**Доступные серверы:**\n%server_list%")
                    .replace("%server_list%", serverList.toString()));
        }
    }

    private String getDefaultConfig() {
        return """
                telegram:
                  token: "your-telegram-bot-token"
                  chats:
                    - "chat_id_1"
                    - "chat_id_2"
                
                messages:
                  server_started: "🔵 **Прокси-сервер запущен!**"
                  server_stopped: "🔴 **Прокси-сервер выключен!**"
                  player_logged_in: "✅ **Игрок зашел**: %player%"
                
                velocity_listener:
                  enabled: true
                  channel: "bwtelegram:notify"
                
                fake_player:
                  enabled: true
                  name: "projectbw.ru"
                """;
    }
}