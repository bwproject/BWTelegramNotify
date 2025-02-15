package me.projectbw.BWTelegramNotify;

import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.Listener;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import javax.inject.Inject;

@Plugin(id = "bwtelegramnotify", name = "BWTelegramNotify", version = "1.0-SNAPSHOT", authors = {"YourName"})
public class Main implements Listener {

    private final ProxyServer server;

    @Inject
    public Main(ProxyServer server) {
        this.server = server;
    }

    // Метод, вызываемый при инициализации плагина
    @EventHandler
    public void onProxyInitialize(ProxyInitializeEvent event) {
        // Логика при старте плагина
        System.out.println("BWTelegramNotify plugin is enabled!");

        // Здесь можно зарегистрировать обработчики событий, например:
        server.getEventManager().register(this, this);
    }

    // Метод, вызываемый при остановке плагина
    public void onDisable() {
        // Логика при остановке плагина
        System.out.println("BWTelegramNotify plugin is disabled!");
    }
}
