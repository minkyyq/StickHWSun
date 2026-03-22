package stickdev.tun1x.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import stickdev.tun1x.StickHWSun;
import stickdev.tun1x.managers.HologramManager;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class DropService {

    private final StickHWSun plugin;

    @Getter
    private int elapsed;

    private BukkitRunnable task;

    public DropService(StickHWSun plugin) {
        this.plugin = plugin;
        this.elapsed = 0;
    }

    public void activate() {
        if (task != null) return;

        final int delay = plugin.getSettings().getSpawnCooldown();
        final HologramManager hologramManager = plugin.getHologramManager();

        task = new BukkitRunnable() {
            @Override
            public void run() {
                elapsed++;

                int remaining = delay - elapsed;
                if (remaining < 0) remaining = 0;
                hologramManager.setTime(remaining);

                if (elapsed >= delay) {
                    spawn();
                    elapsed = 0;
                }
            }
        };

        task.runTaskTimer(plugin, 0, 20L);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }

        elapsed = 0;
    }

    private void spawn() {
        final Location anchor = plugin.buildLocation();
        if (anchor == null || anchor.getWorld() == null) return;

        final List<Player> nearby = plugin.getPayoutSystem().fetchNearby();
        if (nearby.isEmpty()) return;

        final List<ItemStack> items = fetchPool();
        if (items.isEmpty()) return;

        final Location drop = anchor.clone().add(0.5, 1.2, 0.5);

        for (ItemStack stack : items) {
            if (stack != null && !stack.getType().isAir()) anchor.getWorld().dropItemNaturally(drop, stack);
        }
    }

    private List<ItemStack> fetchPool() {
        final List<ItemStack> result = new ArrayList<>();
        final int qty = plugin.getSettings().getInternalAmount();

        if (qty <= 0) return result;

        for (int i = 0; i < qty; i++) {
            final ItemStack stack = plugin.getLootPool().random();
            if (stack != null && !stack.getType().isAir()) result.add(stack);
        }

        return result;
    }

    public int getDelay() {
        return plugin.getSettings().getSpawnCooldown();
    }
}