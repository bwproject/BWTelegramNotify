package me.projectbw.BWTelegramNotify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Notifier {
    private static final Logger logger = LoggerFactory.getLogger(Notifier.class);

    public Notifier() {
        // Конструктор без параметров
        logger.info("Notifier Initialized without configuration.");
    }

    public Notifier(Config config) {
        // Конструктор с передачей конфигурации
        logger.info("Notifier Initialized with configuration.");
        // Логика с конфигом
    }

    public void sendNotification(String message) {
        logger.info("Sending notification: " + message);
        // Логика отправки уведомлений
    }
}
