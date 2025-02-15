package me.projectbw.BWTelegramNotify;

import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import javax.inject.Inject;

@Plugin(id = "bwtelegramnotify", name = "BWTelegramNotify", version = "1.0-SNAPSHOT")
public class Main {

    private final ProxyServer server;

    @Inject
    public Main(ProxyServer server) {
        this.server = server;
    }

    public void onEnable() {
        // Логика при старте плагина
        System.out.println("BWTelegramNotify plugin is enabled!");
    }

    public void onDisable() {
        // Логика при остановке плагина
        System.out.println("BWTelegramNotify plugin is disabled!");
    }
}
