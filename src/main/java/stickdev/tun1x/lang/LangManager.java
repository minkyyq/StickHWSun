package stickdev.tun1x.lang;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import stickdev.tun1x.StickHWSun;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LangManager {

    private final StickHWSun plugin;
    private FileConfiguration lang;
    private String currentLang;

    public LangManager(StickHWSun plugin) {
        this.plugin = plugin;
        this.currentLang = plugin.cfg().getLanguage();
        loadLanguage();
    }

    private void loadLanguage() {
        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }

        File langFile = new File(langFolder, currentLang + ".yml");
        
        if (!langFile.exists()) {
            plugin.saveResource("lang/" + currentLang + ".yml", false);
        }

        lang = YamlConfiguration.loadConfiguration(langFile);
        
        InputStream defStream = plugin.getResource("lang/" + currentLang + ".yml");
        if (defStream != null) {
            InputStreamReader reader = new InputStreamReader(defStream, StandardCharsets.UTF_8);
            FileConfiguration defConfig = YamlConfiguration.loadConfiguration(reader);
            lang.setDefaults(defConfig);
        }
    }

    public String get(String path) {
        return lang.getString(path, path);
    }

    public String get(String path, String def) {
        return lang.getString(path, def);
    }

    public void reload() {
        this.currentLang = plugin.cfg().getLanguage();
        loadLanguage();
    }
}
