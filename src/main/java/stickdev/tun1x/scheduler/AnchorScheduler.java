package stickdev.tun1x.scheduler;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.scheduler.BukkitRunnable;
import stickdev.tun1x.StickHWSun;

public class AnchorScheduler {

    private final StickHWSun plugin;

    public AnchorScheduler(StickHWSun plugin) {
        this.plugin = plugin;
    }

    public void activate() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Location anchor = plugin.buildLocation();
                if (anchor == null || anchor.getWorld() == null) return;

                Block b = anchor.getBlock();
                if (b.getType() != Material.RESPAWN_ANCHOR) return;

                int elapsed = plugin.dropScheduler().getElapsed();
                int total = plugin.dropScheduler().getDelay();
                
                if (total == 0) return;

                RespawnAnchor data = (RespawnAnchor) b.getBlockData();
                int level = Math.min((elapsed * 4) / total, 4);
                data.setCharges(level);
                b.setBlockData(data, false);

                if (elapsed >= total) {
                    data.setCharges(0);
                    b.setBlockData(data, false);
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }
}
