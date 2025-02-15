package java.me.projectbw.BWTelegramNotify;

public class Utils {
    public static boolean isLowTPS(double tps) {
        return tps < 19.0;
    }
}
