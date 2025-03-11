package me.projectbw.BWTelegramNotify;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.ProxyServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Logger;

@Plugin(
    id = "velocitylistener",
    name = "VelocityListener",
    version = "1.0.0",
    description = "Listener для обработки сообщений от Paper и отправки их через сокет"
)
public class VelocityListener {

    private static final ChannelIdentifier CHANNEL = MinecraftChannelIdentifier.from("bwtelegram:notify");
    private final ProxyServer server;
    private final Logger logger;
    private String paperHost;
    private int paperPort;

    @Inject
    public VelocityListener(ProxyServer server, Logger logger, @com.velocitypowered.api.plugin.annotation.DataDirectory Path dataFolder) {
        this.server = server;
        this.logger = logger;
        loadConfig(dataFolder.resolve("config.yml"));
    }

    // Загрузка конфигурации для подключения к Paper
    private void loadConfig(Path configFile) {
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile.toFile());
            paperHost = config.getString("velocity.host", "localhost");
            paperPort = config.getInt("velocity.port", 12345);
            logger.info("Конфигурация VelocityListener загружена успешно.");
        } catch (IOException e) {
            logger.severe("Ошибка при загрузке конфигурации: " + e.getMessage());
        }
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getIdentifier().equals(CHANNEL)) return;

        String message = new String(event.getData(), StandardCharsets.UTF_8);
        logger.info("📩 Получено сообщение от Paper: " + message);

        sendMessageToPaper(message);
    }

    // Отправка сообщения через сокет на Paper
    private void sendMessageToPaper(String message) {
        try (Socket socket = new Socket(paperHost, paperPort);
             OutputStream out = socket.getOutputStream()) {
            out.write(message.getBytes(StandardCharsets.UTF_8));
            out.flush();
            logger.info("Сообщение отправлено на Paper: " + message);
        } catch (IOException e) {
            logger.severe("Ошибка при отправке сообщения на Paper: " + e.getMessage());
        }
    }
}