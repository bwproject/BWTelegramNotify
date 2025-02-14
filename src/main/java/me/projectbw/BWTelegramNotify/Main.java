package me.projectbw.BWTelegramNotify;

import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import javax.inject.Inject;

@Plugin(id = "BWTelegramNotify", name = "BWTelegramNotify", version = "1.0-SNAPSHOT")
public class Main {

    private final ProxyServer server;
    private final Logger logger;

    @Inject
    public Main(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    public void onEnable() {
        // Инициализация плагина
        logger.info("BWTelegramNotify has been enabled.");
        server.getEventManager().register(this, new BWTelegramNotifyVelocity(server));
    }

    public void onDisable() {
        // Логика завершения работы плагина
        logger.info("BWTelegramNotify has been disabled.");
    }
}
