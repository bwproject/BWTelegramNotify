package me.projectbw;

import com.google.inject.Inject;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.slf4j.Logger;

public class BWTelegramNotifyVelocity {
    private final ProxyServer server;
    private final Logger logger;
    private TelegramSender telegramSender;

    @Inject
    public BWTelegramNotifyVelocity(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        logger.info("\u001B[92m[PROJECTBW.RU] \u001B[93mBWTelegramNotify \u001B[32mАктивен\u001B[0m");

        telegramSender = new TelegramSender();
        telegramSender.sendMessage("⚡ Velocity сервер запущен!");

        // Регистрируем команду /status
        server.getCommandManager().register("status", new SimpleCommand() {
            @Override
            public void execute(Invocation invocation) {
                invocation.source().sendMessage(Component.text("[TelegramNotify] Статус: Активен").color(NamedTextColor.GREEN));
            }
        });
    }

    @Subscribe
    public void onPlayerJoin(PostLoginEvent event) {
        String message = "✅ Игрок " + event.getPlayer().getUsername() + " зашел на сервер.";
        telegramSender.sendMessage(message);
    }

    @Subscribe
    public void onPlayerLeave(DisconnectEvent event) {
        String message = "❌ Игрок " + event.getPlayer().getUsername() + " вышел с сервера.";
        telegramSender.sendMessage(message);
    }
}
