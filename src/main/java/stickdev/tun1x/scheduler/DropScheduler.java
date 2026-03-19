package stickdev.tun1x.scheduler;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import stickdev.tun1x.StickHWSun;

import java.util.ArrayList;
import java.util.List;

public class DropScheduler {

    private final StickHWSun plugin;
    private int elapsed;

    public DropScheduler(StickHWSun plugin) {
        this.plugin = plugin;
        this.elapsed = 0;
    }

    public void activate() {
        int delay = plugin.cfg().getSpawnCooldown();
        int tickDelay = delay * 20;
        plugin.displayEngine().updateTimer(delay);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                elapsed++;
                
                int remaining = delay - elapsed;
                if (remaining < 0) remaining = 0;
                plugin.displayEngine().updateTimer(remaining);
                
                if (elapsed >= delay) {
                    spawn();
                    elapsed = 0;
                    plugin.displayEngine().updateTimer(delay);
                }
            }
        }.runTaskTimer(plugin, tickDelay, 20L);
    }

    private void spawn() {
        Location anchor = plugin.buildLocation();
        if (anchor == null || anchor.getWorld() == null) return;

        List<Player> nearby = plugin.distributionEngine().fetchNearby();
        if (nearby.isEmpty()) return;

        List<ItemStack> items = fetchPool();
        if (items.isEmpty()) return;

        Location drop = anchor.clone().add(0.5, 1.2, 0.5);
        for (ItemStack stack : items) {
            if (stack != null) {
                anchor.getWorld().dropItemNaturally(drop, stack);
            }
        }
    }

    private List<ItemStack> fetchPool() {
        List<ItemStack> result = new ArrayList<>();
        int qty = plugin.cfg().getInternalAmount();

        for (int i = 0; i < qty; i++) {
            ItemStack stack = plugin.itemRegistry().random();
            if (stack != null) result.add(stack);
        }

        return result;
    }

    public int getDelay() {
        return plugin.cfg().getSpawnCooldown();
    }

    public int getElapsed() {
        return elapsed;
    }
}
