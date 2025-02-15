package me.projectbw.BWTelegramNotify;

public class Notifier {

    public void sendPlayerNotification(String playerName, String action) {
        // Логика отправки уведомления в Telegram о входе/выходе игрока
        System.out.println("Player " + playerName + " has " + action);
    }

    public void sendServerSwitchNotification(String playerName, String serverName) {
        // Логика отправки уведомления о смене сервера
        System.out.println("Player " + playerName + " switched to server " + serverName);
    }

    public void sendTPSWarning(double tps) {
        // Логика отправки уведомления о низком TPS
        System.out.println("Warning: TPS is low (" + tps + ")");
    }

    public void sendServerStatusNotification(String status) {
        // Логика отправки уведомления о статусе сервера
        System.out.println("Server status: " + status);
    }
}
