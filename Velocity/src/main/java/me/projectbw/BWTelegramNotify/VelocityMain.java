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
            telegramBot.sendMessage("🔵 **Прокси-сервер запущен!**");
        }
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (telegramBot != null) {
            telegramBot.sendMessage("🔴 **Прокси-сервер выключен!**");
        }
    }

    @Subscribe
    public void onPlayerLogin(LoginEvent event) {
        String playerName = event.getPlayer().getUsername();
        if (telegramBot != null) {
            telegramBot.sendMessage("✅ **Игрок зашел**: " + playerName);
        }
    }

    @Subscribe
    public void onPlayerDisconnect(DisconnectEvent event) {
        String playerName = event.getPlayer().getUsername();
        if (telegramBot != null) {
            telegramBot.sendMessage("❌ **Игрок вышел**: " + playerName);
        }
    }

    @Subscribe
    public void onPlayerSwitchServer(ServerConnectedEvent event) {
        Player player = event.getPlayer();
        Optional<String> previousServer = event.getPreviousServer().map(server -> server.getServerInfo().getName());
        String newServer = event.getServer().getServerInfo().getName();

        if (telegramBot != null) {
            if (previousServer.isPresent()) {
                telegramBot.sendMessage("🔄 **Игрок сменил сервер**: " + player.getUsername() +
                        "\n➡ **" + previousServer.get() + "** → **" + newServer + "**");
            } else {
                telegramBot.sendMessage("➡ **Игрок зашел на сервер**: " + player.getUsername() +
                        "\n🟢 **Сервер**: " + newServer);
            }
        }
    }

    private void loadConfig() {
        if (!Files.exists(configFile)) {
            try {
                Files.createDirectories(configFile.getParent());
                Files.createFile(configFile);
                Files.writeString(configFile, "bot.token: \"\"\nbot.chat_ids: []\n");
                logger.warning("Создан новый config.yml. Заполни его перед запуском!");
                return;
            } catch (IOException e) {
                logger.severe("Ошибка при создании config.yml: " + e.getMessage());
                e.printStackTrace(); // Добавил для отладки
                return;
            }
        }

        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile.toFile());
            String botToken = config.getString("bot.token", "");
            List<String> chatIds = config.getStringList("bot.chat_ids");

            if (botToken.isEmpty() || chatIds.isEmpty()) {
                logger.severe("В config.yml не указан токен или список чатов! Бот не будет запущен.");
                return;
            }

            telegramBot = new TelegramBot(botToken, chatIds);
            logger.info("Telegram-бот запущен: " + telegramBot.getBotName() + " (@" + telegramBot.getBotUsername() + ")");
        } catch (Exception e) {  // Добавил общий `catch`, чтобы избежать неожиданных ошибок
            logger.severe("Ошибка при загрузке config.yml: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
