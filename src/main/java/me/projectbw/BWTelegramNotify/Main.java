package java.me.projectbw.BWTelegramNotify;

// Основной класс плагина
import com.velocitypowered.api.plugin.Plugin;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Plugin(id = "BWTelegramNotify", name = "BWTelegramNotify", version = "1.0-SNAPSHOT")
public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("BWTelegramNotify успешно запущен!");
        // Инициализация бота, конфигурации и прочее
    }
}
