package stickdev.tun1x.engine;

import org.bukkit.scheduler.BukkitRunnable;
import stickdev.tun1x.StickHWSun;
import stickdev.tun1x.model.RewardCycle;

public class CycleEngine {

    private final StickHWSun core;
    private RewardCycle currentMode;
    private BukkitRunnable scheduler;

    public CycleEngine(StickHWSun core) {
        this.core = core;
        this.currentMode = RewardCycle.MONEY;
    }

    public void begin() {
        long interval = core.getSettings().getSwitchPeriod() * 1200L;
        scheduler = new BukkitRunnable() {
            @Override
            public void run() {
                currentMode = currentMode.shift();
                core.getHologramSystem().refresh();
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
