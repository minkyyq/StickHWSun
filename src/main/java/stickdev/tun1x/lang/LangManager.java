package stickdev.tun1x.lang;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import stickdev.tun1x.StickHWSun;
import stickdev.tun1x.util.TextUtil;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LangManager {

    private final StickHWSun plugin;
    private FileConfiguration lang;
    private final String currentLang;

    public LangManager(StickHWSun plugin) {
        this.plugin = plugin;
        this.currentLang = plugin.getSettings().getLanguage();
        this.loadLanguage();
    }

    private void loadLanguage() {
        final File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists()) langFolder.mkdirs();

        final File langFile = new File(langFolder, currentLang + ".yml");
        if (!langFile.exists()) plugin.saveResource("lang/" + currentLang + ".yml", false);

        lang = YamlConfiguration.loadConfiguration(langFile);

        final InputStream defStream = plugin.getResource("lang/" + currentLang + ".yml");
        if (defStream != null) {
            InputStreamReader reader = new InputStreamReader(defStream, StandardCharsets.UTF_8);
            FileConfiguration defConfig = YamlConfiguration.loadConfiguration(reader);
            lang.setDefaults(defConfig);
        }

        parseMessages();
    }

    private void parseMessages() {
        final ConfigurationSection notifications = lang.getConfigurationSection("notifications");
        final ConfigurationSection commands = lang.getConfigurationSection("commands");

        if (notifications != null) {
            MESSAGES.areaEntry = TextUtil.color(notifications.getString("area-entry"));
            MESSAGES.currencyGain = TextUtil.color(notifications.getString("currency-gain"));
        }

        if (commands != null) {
            MESSAGES.addSuccess = TextUtil.color(commands.getString("add-success"));
            MESSAGES.addNoItem = TextUtil.color(commands.getString("add-no-item"));
            MESSAGES.addExists = TextUtil.color(commands.getString("add-exists"));
            MESSAGES.removeSuccess = TextUtil.color(commands.getString("remove-success"));
            MESSAGES.cycleChanged = TextUtil.color(commands.getString("cycle-changed"));
        }
    }

    public static class MESSAGES {
        public static String areaEntry;
        public static String currencyGain;
        public static String addSuccess;
        public static String addNoItem;
        public static String addExists;
        public static String removeSuccess;
        public static String cycleChanged;
    }
}