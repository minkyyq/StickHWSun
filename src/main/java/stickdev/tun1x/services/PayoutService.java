package stickdev.tun1x.services;

import lombok.AllArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;
import stickdev.tun1x.managers.RewardManager;

@AllArgsConstructor
public class PayoutService extends BukkitRunnable {

    private final RewardManager rewardManager;

    @Override
    public void run() {
        rewardManager.execute();
    }

}
