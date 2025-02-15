package me.projectbw.BWTelegramNotify.events;

import com.destroystokyo.paper.event.server.TPSChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TPSMonitor implements Listener {
    
    @EventHandler
    public void onTPSChange(TPSChangeEvent event) {
        // Ваш код для обработки изменения TPS
    }
}
