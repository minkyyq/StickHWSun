package stickdev.tun1x.engine;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import stickdev.tun1x.StickHWSun;
import stickdev.tun1x.model.RewardCycle;
import stickdev.tun1x.util.TextUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DistributionEngine {

    private final StickHWSun plugin;

    public DistributionEngine(StickHWSun plugin) {
        this.plugin = plugin;
    }

    public List<Player> fetchNearby() {
        Location anchor = plugin.buildLocation();
        if (anchor == null || anchor.getWorld() == null) return new ArrayList<>();

        int radius = plugin.cfg().getAreaSize();
        List<Player> players = new ArrayList<>();

        for (Player p : anchor.getWorld().getPlayers()) {
            double distSq = p.getLocation().distanceSquared(anchor);
            if (distSq <= radius * radius) {
                players.add(p);
            }
        }
        return players;
    }

    public void execute() {
        List<Player> players = fetchNearby();
        if (players.isEmpty()) return;

        RewardCycle current = plugin.cycleEngine().getActive();
        
        if (current == RewardCycle.MONEY) {
            distributeCurrency(players);
        } else {
            distributeExperience(players);
        }
    }

    private void distributeCurrency(List<Player> players) {
        double min = plugin.cfg().getCurrencyLower();
        double max = plugin.cfg().getCurrencyUpper();
        double pool = generateDouble(min, max);
        double share = pool / players.size();
        
        for (Player player : players) {
            plugin.eco().depositPlayer(player, share);
            String formatted = formatCurrency(share);
            String notification = plugin.lang().get("notifications.currency-gain")
                    .replace("%amount%", formatted);
            player.sendMessage(TextUtil.color(notification));
        }
    }

    private void distributeExperience(List<Player> players) {
        int min = plugin.cfg().getExperienceLower();
        int max = plugin.cfg().getExperienceUpper();
        int pool = generateInt(min, max);
        int share = Math.max(1, pool / players.size());
        
        for (Player player : players) {
            player.giveExp(share);
        }
    }

    private String formatCurrency(double value) {
        long intPart = (long) value;
        int decPart = (int) Math.round((value - intPart) * 100);
        
        String intStr = String.valueOf(intPart);
        StringBuilder result = new StringBuilder();
        
        int count = 0;
        for (int i = intStr.length() - 1; i >= 0; i--) {
            if (count > 0 && count % 3 == 0) {
                result.insert(0, ' ');
            }
            result.insert(0, intStr.charAt(i));
            count++;
        }
        
        result.append('.');
        if (decPart < 10) {
            result.append('0');
        }
        result.append(decPart);
        
        return result.toString();
    }

    private double generateDouble(double lower, double upper) {
        if (lower >= upper) return lower;
        return lower + ThreadLocalRandom.current().nextDouble() * (upper - lower);
    }

    private int generateInt(int lower, int upper) {
        if (lower >= upper) return lower;
        return ThreadLocalRandom.current().nextInt(lower, upper + 1);
    }
}
