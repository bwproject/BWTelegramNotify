package me.projectbw.BWTelegramNotify;

import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private final ProxyServer proxyServer;

    @Inject
    public Main(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    public void onEnable() {
        logger.info("BWTelegramNotify enabled!");
        // Инициализация бота Telegram и подписка на события Velocity
    }

    public void onDisable() {
        logger.info("BWTelegramNotify disabled!");
    }
}