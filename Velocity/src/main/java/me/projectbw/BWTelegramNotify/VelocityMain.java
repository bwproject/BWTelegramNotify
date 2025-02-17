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
import java.nio.file.Paths;

@Plugin(id = "bwtelegramnotify", name = "BWTelegramNotify", version = "1.0")
public class VelocityMain {
    private final ProxyServer server;
    private final Logger logger;
    private final TelegramBot telegramBot;

    @Inject
    public VelocityMain(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
        this.telegramBot = new TelegramBot();
        logger.info("BWTelegramNotify for Velocity has been enabled!");
    }

    // Обработчик для события входа игрока
    @Subscribe
    public void onPlayerJoin(LoginEvent event) {
        String message = "Player " + event.getPlayer().getUsername() + " has joined the server.";
        logger.info(message);
        telegramBot.sendMessage(message);
    }

    // Обработчик для события выхода игрока
    @Subscribe
    public void onPlayerLeave(DisconnectEvent event) {
        String message = "Player " + event.getPlayer().getUsername() + " has left the server.";
        logger.info(message);
        telegramBot.sendMessage(message);
    }

    // Обработчик для события смены сервера игроком
    @Subscribe
    public void onServerSwitch(ServerConnectedEvent event) {
        String message = "Player " + event.getPlayer().getUsername() + " switched servers.";
        logger.info(message);
        telegramBot.sendMessage(message);
    }

    // Чтение конфигурации для бота (например, получение пути для конфигурации)
    public void loadConfig() {
        Path path = Paths.get("plugins/BWTelegramNotify/config.json"); // Пример пути к конфигу
        logger.info("Config path: " + path);

        // Обрабатываем конфигурацию (если требуется, используйте JSON парсинг)
        Path parentPath = path.getParent();  // Получаем родительскую директорию
        logger.info("Parent directory: " + parentPath);
    }
}