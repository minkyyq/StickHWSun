package stickdev.tun1x.config;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class ConfigManager {
    
    private final JavaPlugin plugin;
    private FileConfiguration config;

    private String worldName;
    private double coordX;
    private double coordY;
    private double coordZ;
    private int areaSize;
    
    private int tickInterval;
    private double currencyLower;
    private double currencyUpper;
    private int experienceLower;
    private int experienceUpper;
    
    private int switchPeriod;
    private int spawnCooldown;
    private int internalAmount;
    
    private String headerText;
    private String statusText;
    private String countdownText;
    private double verticalShift;
    
    private String moneyDisplayColor;
    private String moneyRewardColor;
    private String xpDisplayColor;
    private String xpRewardColor;
    
    private String language;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
        parse();
    }

    private void parse() {
        parseLanguage();
        parseLocation();
        parseDistribution();
        parseRotation();
        parseSpawner();
        parseVisual();
        parseCycles();
    }

    private void parseLanguage() {
        language = config.getString("language", "ru");
    }

    private void parseLocation() {
        worldName = config.getString("location.world-name", "spawn");
        coordX = config.getDouble("location.coord-x", -121);
        coordY = config.getDouble("location.coord-y", 48);
        coordZ = config.getDouble("location.coord-z", -28);
        areaSize = getInt("location.area-size", 15, 1, 500);
    }

    private void parseDistribution() {
        tickInterval = getInt("distribution.tick-interval", 5, 1, 3600);
        currencyLower = config.getDouble("distribution.currency-lower", 8500);
        currencyUpper = config.getDouble("distribution.currency-upper", 9700);
        experienceLower = getInt("distribution.experience-lower", 20, 0, 10000);
        experienceUpper = getInt("distribution.experience-upper", 40, 0, 10000);
        
        if (currencyLower > currencyUpper) {
            plugin.getLogger().warning("currency-lower > currency-upper");
            double temp = currencyLower;
            currencyLower = currencyUpper;
            currencyUpper = temp;
        }
        
        if (experienceLower > experienceUpper) {
            plugin.getLogger().warning("experience-lower > experience-upper");
            int temp = experienceLower;
            experienceLower = experienceUpper;
            experienceUpper = temp;
        }
    }

    private void parseRotation() {
        switchPeriod = getInt("rotation.switch-period", 25, 1, 1440);
    }

    private void parseSpawner() {
        spawnCooldown = getInt("spawner.spawn-cooldown", 20, 1, 3600);
        internalAmount = getInt("spawner.internal.amount", 1, 1, 64);
    }

    private void parseVisual() {
        headerText = config.getString("visual.header-text", "&6Ядро солнца");
        statusText = config.getString("visual.status-text", "&fСейчас выдаёт %cycle%");
        countdownText = config.getString("visual.countdown-text", "&fЛут через &x&0&0&D&0&F&8%time% сек.");
        verticalShift = config.getDouble("visual.vertical-shift", 1.4);
    }

    private void parseCycles() {
        moneyDisplayColor = config.getString("cycles.money.display", "&#FFD700Монеты");
        moneyRewardColor = config.getString("cycles.money.reward", "&#FFD700монеты");
        xpDisplayColor = config.getString("cycles.xp.display", "&#00FF00Опыт");
        xpRewardColor = config.getString("cycles.xp.reward", "&#00FF00опыт");
    }

    private int getInt(String path, int def, int min, int max) {
        int value = config.getInt(path, def);
        if (value < min || value > max) {
            return def;
        }
        return value;
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        parse();
        plugin.getLogger().info("конфиг перезагружен");
    }
}
