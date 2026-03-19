package stickdev.tun1x.registry;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import stickdev.tun1x.StickHWSun;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ItemRegistry {

    private final StickHWSun core;
    private File dataFile;
    private FileConfiguration yaml;

    public ItemRegistry(StickHWSun core) {
        this.core = core;
        setup();
    }

    private void setup() {
        dataFile = new File(core.getDataFolder(), "items.yml");
        if (!dataFile.exists()) {
            try {
                core.getDataFolder().mkdirs();
                dataFile.createNewFile();
            } catch (IOException ignored) {
            }
        }
        yaml = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void save() {
        try {
            yaml.save(dataFile);
        } catch (IOException ignored) {
        }
    }

    public void reload() {
        yaml = YamlConfiguration.loadConfiguration(dataFile);
    }

    public boolean register(String identifier, ItemStack item) {
        String path = "pool." + identifier;
        if (yaml.contains(path)) return false;
        yaml.set(path, item);
        save();
        return true;
    }

    public boolean unregister(String identifier) {
        String path = "pool." + identifier;
        if (!yaml.contains(path)) return false;
        yaml.set(path, null);
        save();
        return true;
    }

    public ItemStack fetch(String identifier) {
        return yaml.getItemStack("pool." + identifier);
    }

    public List<String> keys() {
        if (!yaml.contains("pool")) return new ArrayList<>();
        Set<String> keySet = yaml.getConfigurationSection("pool").getKeys(false);
        return new ArrayList<>(keySet);
    }

    public List<ItemStack> all() {
        List<ItemStack> collection = new ArrayList<>();
        for (String identifier : keys()) {
            ItemStack item = fetch(identifier);
            if (item != null) collection.add(item.clone());
        }
        return collection;
    }

    public ItemStack random() {
        List<ItemStack> collection = all();
        if (collection.isEmpty()) return null;
        int index = (int) (Math.random() * collection.size());
        return collection.get(index);
    }
}
