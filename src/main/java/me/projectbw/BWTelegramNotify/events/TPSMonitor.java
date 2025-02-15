package me.projectbw.BWTelegramNotify.events;

import me.projectbw.BWTelegramNotify.Notifier;
import me.projectbw.BWTelegramNotify.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TPSMonitor {
    private static final Logger logger = LoggerFactory.getLogger(TPSMonitor.class);
    private Notifier notifier;

    public TPSMonitor(Config config) {
        this.notifier = new Notifier(config);  // Передача конфигурации в конструктор Notifier
    }

    public void startMonitoring() {
        logger.info("TPS Monitoring started.");
        // Добавьте код для мониторинга TPS и уведомлений
    }
}
