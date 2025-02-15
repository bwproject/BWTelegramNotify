package me.projectbw.BWTelegramNotify.events;

import com.destroystokyo.paper.event.server.TPSChangeEvent;
import me.projectbw.BWTelegramNotify.Notifier;

public class TPSMonitor {

    private final Notifier notifier;

    public TPSMonitor(Notifier notifier) {
        this.notifier = notifier;
    }

    @org.bukkit.event.EventHandler
    public void onTPSChange(TPSChangeEvent event) {
        double tps = event.getNewTPS()[0]; // Получаем TPS
        if (tps < 18.0) { // Если TPS меньше 18
            notifier.sendTPSWarning(tps);
        }
    }
}
