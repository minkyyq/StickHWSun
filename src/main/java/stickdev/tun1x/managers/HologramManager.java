package stickdev.tun1x.managers;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import stickdev.tun1x.Config;
import stickdev.tun1x.StickHWSun;
import stickdev.tun1x.util.TextUtil;

import java.util.ArrayList;
import java.util.List;

public class HologramManager {

    private final StickHWSun plugin;
    private final String HOLOGRAM_NAME = "hw_sun";
    @Setter
    private int time;

    public HologramManager(StickHWSun plugin) {
        this.plugin = plugin;
        this.time = plugin.getSettings().getSpawnCooldown();
    }

    public void init() {
        this.cleanup();

        final Location pos = plugin.buildLocation();
        if (pos == null || pos.getWorld() == null) return;

        final Block block = pos.getBlock();
        if (block.getType() != Material.RESPAWN_ANCHOR) block.setType(Material.RESPAWN_ANCHOR);

        final Config.Hologram hologramConfig = plugin.getSettings().getHologram();
        final Location center = pos.clone().add(0, hologramConfig.getVerticalShift() + 1, 0).toCenterLocation();

        DHAPI.createHologram(HOLOGRAM_NAME, center, this.getHologramLines());

        new BukkitRunnable() {
            @Override
            public void run() {
                if (time <= 0) {
                    time = plugin.getSettings().getSpawnCooldown();
                    setNewLines();
                }

                time--;
                setNewLines();
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private List<String> getHologramLines() {
        final List<String> originalLines = plugin.getSettings().getHologram().getLines();
        final List<String> newLines = new ArrayList<>();

        for (String line : originalLines) {
            line = line.replace("%time%", String.valueOf(time));
            line = line.replace("%cycle%", TextUtil.color(plugin.getRewardCycle().getActive().getDisplay()));
            newLines.add(line);
        }

        return newLines;
    }

    public void setNewLines() {
        final Hologram hologram = DHAPI.getHologram(HOLOGRAM_NAME);
        if (hologram != null) DHAPI.setHologramLines(hologram, getHologramLines());
    }

    public void cleanup() {
        final Hologram hologram = DHAPI.getHologram(HOLOGRAM_NAME);
        if (hologram != null) hologram.delete();
    }

}
