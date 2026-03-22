package stickdev.tun1x.services;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.scheduler.BukkitRunnable;
import stickdev.tun1x.StickHWSun;

public class AnchorService extends BukkitRunnable {

    private final StickHWSun plugin;
    private DropService ITEM_DROPPER_SERVICE;

    public AnchorService(StickHWSun plugin) {
        this.plugin = plugin;
        this.ITEM_DROPPER_SERVICE = plugin.getDropService();
    }

    @Override
    public void run() {
        final Location anchor = plugin.buildLocation();
        if (anchor == null || anchor.getWorld() == null) return;

        final Block block = anchor.getBlock();
        if (block.getType() != Material.RESPAWN_ANCHOR) return;

        final int elapsed = ITEM_DROPPER_SERVICE.getElapsed();
        final int total = ITEM_DROPPER_SERVICE.getDelay();
        if (total == 0) return;

        final RespawnAnchor data = (RespawnAnchor) block.getBlockData();
        final int level = Math.min((elapsed * 4) / total, 4);

        data.setCharges(level);
        block.setBlockData(data, false);

        if (elapsed >= total) {
            data.setCharges(0);
            block.setBlockData(data, false);
        }
    }
}
