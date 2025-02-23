package me.projectbw.BWTelegramNotify;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Plugin(
        id = "bwtelegramnotify",
        name = "BWTelegramNotify",
        version = "1.0-SNAPSHOT",
        description = "Отправляет уведомления в Telegram о событиях на сервере",
        authors = {"The_Mr_Mes109"},
        dependencies = {@Dependency(id = "velocity")}
)
public class VelocityMain {
    private final ProxyServer server;
    private final Logger logger;
    private final Path configFile;
    private TelegramBot telegramBot;
    private YamlConfiguration config;

    @Inject
    public VelocityMain(ProxyServer server, Logger logger, @DataDirectory Path dataFolder) {
        this.server = server;
        this.logger = logger;
        this.configFile = dataFolder.resolve("config.yml");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("==================================");
        logger.info("===    BWTelegramNotify загружен   ===");
        logger.info("==================================");

        loadConfig();

        if (telegramBot != null) {
            String serverStartedMessage = config.getString("messages.server_started", "🔵 **Прокси-сервер запущен!**");
            telegramBot.sendMessage(serverStartedMessage);
        }
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (telegramBot != null) {
            String serverStoppedMessage = config.getString("messages.server_stopped", "🔴 **Прокси-сервер выключен!**");
            telegramBot.sendMessage(serverStoppedMessage);
        }
    }

    @Subscribe
    public void onPlayerLogin(LoginEvent event) {
        String playerName = event.getPlayer().getUsername();
        if (telegramBot != null) {
            String playerLoggedInMessage = config.getString("messages.player_logged_in", "✅ **Игрок зашел**: %player%");
            playerLoggedInMessage = playerLoggedInMessage.replace("%player%", playerName);
            telegramBot.sendMessage(playerLoggedInMessage);
        }
    }

    @Subscribe
    public void onPlayerDisconnect(DisconnectEvent event) {
        String playerName = event.getPlayer().getUsername();
        if (telegramBot != null) {
            String playerLoggedOutMessage = config.getString("messages.player_logged_out", "❌ **Игрок вышел**: %player%");
            playerLoggedOutMessage = playerLoggedOutMessage.replace("%player%", playerName);
            telegramBot.sendMessage(playerLoggedOutMessage);
        }
    }

    @Subscribe
    public void onPlayerSwitchServer(ServerConnectedEvent event) {
        Player player = event.getPlayer();
        Optional<String> previousServer = event.getPreviousServer().map(server -> server.getServerInfo().getName());
        String newServer = event.getServer().getServerInfo().getName();

        if (telegramBot != null) {
            if (previousServer.isPresent()) {
                String playerSwitchedServerMessage = config.getString("messages.player_switched_server", "🔄 **Игрок сменил сервер**: %player%\n➡ **%previous_server%** → **%new_server%**");
                playerSwitchedServerMessage = playerSwitchedServerMessage.replace("%player%", player.getUsername())
                        .replace("%previous_server%", previousServer.get())
                        .replace("%new_server%", newServer);
                telegramBot.sendMessage(playerSwitchedServerMessage);
            } else {
                String playerJoinedServerMessage = config.getString("messages.player_joined_server", "➡ **Игрок зашел на сервер**: %player%\n🟢 **Сервер**: %new_server%");
                playerJoinedServerMessage = playerJoinedServerMessage.replace("%player%", player.getUsername())
                        .replace("%new_server%", newServer);
                telegramBot.sendMessage(playerJoinedServerMessage);
            }
        }
    }

    private void loadConfig() {
        if (!Files.exists(configFile)) {
            try {
                Files.createDirectories(configFile.getParent());
                Files.createFile(configFile);
                Files.writeString(configFile, "telegram:\n  token: \"\"\n  chats: []\n\nmessages:\n  server_started: \"🔵 **Прокси-сервер запущен!**\"\n  server_stopped: \"🔴 **Прокси-сервер выключен!**\"\n  player_logged_in: \"✅ **Игрок зашел**: %player%\"\n  player_logged_out: \"❌ **Игрок вышел**: %player%\"\n  player_switched_server: \"🔄 **Игрок сменил сервер**: %player%\n➡ **%previous_server%** → **%new_server%**\"\n  player_joined_server: \"➡ **Игрок зашел на сервер**: %player%\n🟢 **Сервер**: %new_server%\"");
                logger.warning("Создан новый config.yml. Заполни его перед запуском!");
                return;
            } catch (IOException e) {
                logger.severe("Ошибка при создании config.yml: " + e.getMessage());
            }
        }

        config = YamlConfiguration.loadConfiguration(configFile.toFile());
        String botToken = config.getString("telegram.token", "");
        List<String> chatIds = config.getStringList("telegram.chats");

        if (botToken.isEmpty() || chatIds.isEmpty()) {
            logger.severe("В config.yml не указан токен или список чатов! Бот не будет запущен.");
            return;
        }

        telegramBot = new TelegramBot(botToken, chatIds);
        logger.info("Telegram-бот запущен: " + telegramBot.getBotName() + " (@" + telegramBot.getBotUsername() + ")");
    }
}
