package stickdev.tun1x.scheduler;

import org.bukkit.scheduler.BukkitRunnable;
import stickdev.tun1x.StickHWSun;

public class PayoutScheduler {

    private final StickHWSun plugin;

    public PayoutScheduler(StickHWSun plugin) {
        this.plugin = plugin;
    }

    public void activate() {
        int period = plugin.getSettings().getTickInterval() * 20;
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getPayoutSystem().execute();
            }
        }.runTaskTimer(plugin, period, period);
    }
}
