package me.projectbw;

import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.Listener;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent.Response;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;

import org.slf4j.Logger;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class BWTelegramNotifyVelocity implements Listener {
    private TelegramSender telegramSender;
    private TPSListener tpsListener;
    private final Logger logger;

    public BWTelegramNotifyVelocity(Logger logger) {
        this.logger = logger;
    }

    public void onEnable() {
        // Цветной лог в консоль
        logger.info("\u001b[32m[INFO] BWTelegramNotify плагин активен!");  // Зеленый цвет

        // Инициализация и регистрация listener
        this.telegramSender = new TelegramSender("your_bot_token", "your_chat_id");  // Пример значений
        this.tpsListener = new TPSListener(telegramSender, 18.0, 60);  // Порог TPS 18 и интервал 60 секунд

        // Регистрация TPSListener
        this.getServer().getEventManager().register(this, this.tpsListener);
    }

    public class StatusCommand implements SimpleCommand {
        @Override
        public void execute(Invocation invocation) {
            String status = telegramSender.checkBotStatus();
            invocation.source().sendMessage(status);
        }
    }
}