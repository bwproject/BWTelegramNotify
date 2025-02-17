package me.projectbw.BWTelegramNotify;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import java.util.Properties;

@Plugin(id = "bwtelegramnotify", name = "BWTelegramNotify", version = "1.0")
public class VelocityMain {
    private final ProxyServer server;
    private final Logger logger;
    private final TelegramBot telegramBot;
    private Properties config;

    @Inject
    public VelocityMain(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
        this.telegramBot = new TelegramBot();
        loadConfig();
        logger.info("BWTelegramNotify for Velocity has been enabled!");
    }

    private void loadConfig() {
        Path configPath = server.getPlatform().getAssetDirectory().resolve("config.properties");
        config = new Properties();

        if (!Files.exists(configPath)) {
            // Если конфиг не существует, создаём
            try {
                Files.createFile(configPath);
                config.setProperty("chat_id", "your_chat_id");
                config.setProperty("bot_token", "your_bot_token");
                config.store(Files.newOutputStream(configPath), null);
            } catch (IOException e) {
                logger.error("Failed to create config file.", e);
            }
        } else {
            try {
                config.load(Files.newInputStream(configPath));
                String chatId = config.getProperty("chat_id");
                String botToken = config.getProperty("bot_token");
                telegramBot.setConfig(chatId, botToken);
            } catch (IOException e) {
                logger.error("Failed to load config file.", e);
            }
        }
    }

    @Subscribe
    public void onPlayerJoin(LoginEvent event) {
        String message = "Player " + event.getPlayer().getUsername() + " has joined the server.";
        logger.info(message);
        telegramBot.sendMessage(message);
    }

    @Subscribe
    public void onPlayerLeave(DisconnectEvent event) {
        String message = "Player " + event.getPlayer().getUsername() + " has left the server.";
        logger.info(message);
        telegramBot.sendMessage(message);
    }

    @Subscribe
    public void onServerSwitch(ServerConnectedEvent event) {
        String message = "Player " + event.getPlayer().getUsername() + " switched servers.";
        logger.info(message);
        telegramBot.sendMessage(message);
    }
}