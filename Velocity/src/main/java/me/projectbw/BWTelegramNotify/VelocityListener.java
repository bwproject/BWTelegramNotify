// VelocityListener.java
package me.projectbw.BWTelegramNotify;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class VelocityListener {
    private final ProxyServer server;
    private final Logger logger;
    private final VelocityMain velocityMain;
    private static final ChannelIdentifier CHANNEL = MinecraftChannelIdentifier.from("bwtelegram:notify");

    public VelocityListener(ProxyServer server, Logger logger, VelocityMain velocityMain) {
        this.server = server;
        this.logger = logger;
        this.velocityMain = velocityMain;

        // Регистрируем канал для приема сообщений от Paper
        server.getChannelRegistrar().register(CHANNEL);
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getIdentifier().equals(CHANNEL)) {
            return;
        }

        String message = new String(event.getData(), StandardCharsets.UTF_8);
        logger.info("📩 Получено сообщение от Paper: " + message);

        // Передаем сообщение в VelocityMain для отправки в Telegram
        velocityMain.forwardMessageToTelegram(message);
    }
}