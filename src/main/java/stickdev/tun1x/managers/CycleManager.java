package stickdev.tun1x.managers;

import org.bukkit.scheduler.BukkitRunnable;
import stickdev.tun1x.StickHWSun;
import stickdev.tun1x.model.RewardCycle;

public class CycleManager {

    private final StickHWSun core;
    private RewardCycle currentMode;
    private BukkitRunnable scheduler;

    public CycleManager(StickHWSun core) {
        this.core = core;
        this.currentMode = RewardCycle.MONEY;
    }

    public void begin() {
        long interval = core.getSettings().getSwitchPeriod() * 1200L;
        scheduler = new BukkitRunnable() {
            @Override
            public void run() {
                currentMode = currentMode.shift();
                core.getHologramManager().init();
            }
        };
        scheduler.runTaskTimer(core, interval, interval);
    }

    public void terminate() {
        if (scheduler != null) {
            scheduler.cancel();
            scheduler = null;
        }
    }

    public RewardCycle getActive() {
        return currentMode;
    }

    public void setActive(RewardCycle mode) {
        this.currentMode = mode;
    }
}
