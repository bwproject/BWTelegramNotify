package me.projectbw.BWTelegramNotify.events;

import com.destroystokyo.paper.event.server.TPSChangeEvent;
import me.projectbw.BWTelegramNotify.Notifier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TPSMonitor implements Listener {

    private final Notifier notifier;

    public TPSMonitor(Notifier notifier) {
        this.notifier = notifier;
    }

    @EventHandler
    public void onTPSChange(TPSChangeEvent event) {
        double tps = event.getNewTPS();
        if (tps < 18.0) {
            notifier.sendTPSWarning(tps);
        }
    }
}
