package stickdev.tun1x;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@Getter
public final class Config {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    private final String worldName;
    private final double coordX;
    private final double coordY;
    private final double coordZ;
    private final int areaSize;

    private final int tickInterval;
    private final double currencyLower;
    private final double currencyUpper;
    private final int experienceLower;
    private final int experienceUpper;

    private final int switchPeriod;
    private final int spawnCooldown;
    private final int internalAmount;

    private final Hologram hologram;
    private final Cycles cycles;

    private final String language;

    public Config(final JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();

        this.language = parseLanguage();

        final Location location = parseLocation();
        this.worldName = location.worldName;
        this.coordX = location.coordX;
        this.coordY = location.coordY;
        this.coordZ = location.coordZ;
        this.areaSize = location.areaSize;

        final Distribution distribution = parseDistribution();
        this.tickInterval = distribution.tickInterval;
        this.currencyLower = distribution.currencyLower;
        this.currencyUpper = distribution.currencyUpper;
        this.experienceLower = distribution.experienceLower;
        this.experienceUpper = distribution.experienceUpper;

        final Rotation rotation = parseRotation();
        this.switchPeriod = rotation.switchPeriod;

        final Spawner spawner = parseSpawner();
        this.spawnCooldown = spawner.spawnCooldown;
        this.internalAmount = spawner.internalAmount;

        this.hologram = parseHologram();
        this.cycles = parseCycles();
    }

    private String parseLanguage() {
        return config.getString("language", "ru");
    }

    private Location parseLocation() {
        final ConfigurationSection section = config.getConfigurationSection("location");
        return new Location(
                getString(section, "world-name", "spawn"),
                getDouble(section, "coord-x", -121),
                getDouble(section, "coord-y", 48),
                getDouble(section, "coord-z", -28),
                boundedInt(section, "area-size", 15, 1, 500)
        );
    }

    private Distribution parseDistribution() {
        final ConfigurationSection section = config.getConfigurationSection("distribution");

        final int tickInterval = boundedInt(section, "tick-interval", 5, 1, 3600);

        final double currencyLowerRaw = getDouble(section, "currency-lower", 8500);
        final double currencyUpperRaw = getDouble(section, "currency-upper", 9700);
        double currencyLower = currencyLowerRaw;
        double currencyUpper = currencyUpperRaw;

        if (currencyLowerRaw > currencyUpperRaw) {
            plugin.getLogger().warning("distribution.currency-lower > distribution.currency-upper, values were swapped");
            currencyLower = currencyUpperRaw;
            currencyUpper = currencyLowerRaw;
        }

        final int expLowerRaw = boundedInt(section, "experience-lower", 20, 0, 10000);
        final int expUpperRaw = boundedInt(section, "experience-upper", 40, 0, 10000);
        int experienceLower = expLowerRaw;
        int experienceUpper = expUpperRaw;

        if (expLowerRaw > expUpperRaw) {
            plugin.getLogger().warning("distribution.experience-lower > distribution.experience-upper, values were swapped");
            experienceLower = expUpperRaw;
            experienceUpper = expLowerRaw;
        }

        return new Distribution(tickInterval, currencyLower, currencyUpper, experienceLower, experienceUpper);
    }

    private Rotation parseRotation() {
        final ConfigurationSection section = config.getConfigurationSection("rotation");
        return new Rotation(boundedInt(section, "switch-period", 25, 1, 1440));
    }

    private Spawner parseSpawner() {
        final ConfigurationSection section = config.getConfigurationSection("spawner");
        final int spawnCooldown = boundedInt(section, "spawn-cooldown", 20, 1, 3600);

        final ConfigurationSection internal = section != null ? section.getConfigurationSection("internal") : null;
        final int internalAmount = boundedInt(internal, "amount", 1, 1, 64);

        return new Spawner(spawnCooldown, internalAmount);
    }

    private Hologram parseHologram() {
        final ConfigurationSection section = config.getConfigurationSection("hologram");

        final List<String> lines = section != null ? section.getStringList("lines") : null;
        final List<String> defaultLines = List.of(
                "&6Ядро солнца",
                "&fСейчас выдаёт %cycle%",
                "&fЛут через &x&0&0&D&0&F&8%time% сек."
        );

        return new Hologram(
                lines != null && !lines.isEmpty() ? lines : defaultLines,
                getDouble(section, "vertical-shift", 1.4)
        );
    }

    private Cycles parseCycles() {
        final ConfigurationSection section = config.getConfigurationSection("cycles");

        final ConfigurationSection money = section != null ? section.getConfigurationSection("money") : null;
        final String moneyDisplayColor = getString(money, "display", "&#FFD700Монеты");
        final String moneyRewardColor = getString(money, "reward", "&#FFD700монеты");

        final ConfigurationSection xp = section != null ? section.getConfigurationSection("xp") : null;
        final String xpDisplayColor = getString(xp, "display", "&#00FF00Опыт");
        final String xpRewardColor = getString(xp, "reward", "&#00FF00опыт");

        return new Cycles(moneyDisplayColor, moneyRewardColor, xpDisplayColor, xpRewardColor);
    }

    private String getString(final ConfigurationSection section, final String path, final String defaultValue) {
        return section != null ? section.getString(path, defaultValue) : defaultValue;
    }

    private double getDouble(final ConfigurationSection section, final String path, final double defaultValue) {
        return section != null ? section.getDouble(path, defaultValue) : defaultValue;
    }

    private int boundedInt(final ConfigurationSection section, final String path, final int defaultValue, final int min, final int max) {
        if (section == null) return defaultValue;
        final int value = section.getInt(path, defaultValue);
        return (value < min || value > max) ? defaultValue : value;
    }

    @Getter
    @AllArgsConstructor
    private static final class Location {
        private final String worldName;
        private final double coordX;
        private final double coordY;
        private final double coordZ;
        private final int areaSize;
    }

    @Getter
    @AllArgsConstructor
    private static final class Distribution {
        private final int tickInterval;
        private final double currencyLower;
        private final double currencyUpper;
        private final int experienceLower;
        private final int experienceUpper;
    }

    @Getter
    @AllArgsConstructor
    private static final class Rotation {
        private final int switchPeriod;
    }

    @Getter
    @AllArgsConstructor
    private static final class Spawner {
        private final int spawnCooldown;
        private final int internalAmount;
    }

    @Getter
    @AllArgsConstructor
    public static final class Hologram {
        private final List<String> lines;
        private final double verticalShift;
    }

    @Getter
    @AllArgsConstructor
    public static final class Cycles {
        private final String moneyDisplayColor;
        private final String moneyRewardColor;
        private final String xpDisplayColor;
        private final String xpRewardColor;
    }
}