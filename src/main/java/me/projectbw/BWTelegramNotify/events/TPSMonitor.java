package me.projectbw.BWTelegramNotify.events;

import com.destroystokyo.paper.event.server.TPSChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import me.projectbw.BWTelegramNotify.Notifier;
import me.projectbw.BWTelegramNotify.Config;

public class TPSMonitor implements Listener {

    private Config config;
    private Notifier notifier;

    public TPSMonitor(Config config) {
        this.config = config;
        this.notifier = new Notifier(config);
    }

    @EventHandler
    public void onTPSChange(TPSChangeEvent event) {
        if (event.getNewTPS() < Double.parseDouble(config.get("tps_warning_threshold"))) {
            notifier.sendTPSWarning(event.getNewTPS());
        }
    }
}
