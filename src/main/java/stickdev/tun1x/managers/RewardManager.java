package stickdev.tun1x.managers;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import stickdev.tun1x.StickHWSun;
import stickdev.tun1x.lang.LangManager;
import stickdev.tun1x.model.RewardCycle;
import stickdev.tun1x.util.TextUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RewardManager {

    private final StickHWSun plugin;
    private final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    public RewardManager(final StickHWSun plugin) {
        this.plugin = plugin;
    }

    public List<Player> fetchNearby() {
        final Location anchor = plugin.buildLocation();
        if (anchor == null || anchor.getWorld() == null) {
            return new ArrayList<>();
        }

        final int radius = plugin.getSettings().getAreaSize();
        final List<Player> players = new ArrayList<>();

        for (final Player player : anchor.getWorld().getPlayers()) {
            final double distanceSquared = player.getLocation().distanceSquared(anchor);
            if (distanceSquared <= radius * radius) {
                players.add(player);
            }
        }

        return players;
    }

    public void execute() {
        final List<Player> players = fetchNearby();
        if (players.isEmpty()) {
            return;
        }

        final RewardCycle currentCycle = plugin.getRewardCycle().getActive();

        if (currentCycle == RewardCycle.MONEY) {
            distributeCurrency(players);
        } else {
            distributeExperience(players);
        }
    }

    private void distributeCurrency(final List<Player> players) {
        final double min = plugin.getSettings().getCurrencyLower();
        final double max = plugin.getSettings().getCurrencyUpper();
        final double totalAmount = generateDouble(min, max);
        final double sharePerPlayer = totalAmount / players.size();

        for (final Player player : players) {
            plugin.getVaultEconomy().depositPlayer(player, sharePerPlayer);
            final String formatted = formatCurrency(sharePerPlayer);
            player.sendMessage(TextUtil.color(LangManager.MESSAGES.currencyGain
                    .replace("%amount%", formatted)));
        }
    }

    private void distributeExperience(final List<Player> players) {
        final int min = plugin.getSettings().getExperienceLower();
        final int max = plugin.getSettings().getExperienceUpper();
        final int totalExp = generateInt(min, max);
        final int sharePerPlayer = Math.max(1, totalExp / players.size());

        for (final Player player : players) {
            player.giveExp(sharePerPlayer);
        }
    }

    private String formatCurrency(final double value) {
        final long integerPart = (long) value;
        final int decimalPart = (int) Math.round((value - integerPart) * 100);

        final String integerStr = String.valueOf(integerPart);
        final StringBuilder result = new StringBuilder();

        int count = 0;
        for (int i = integerStr.length() - 1; i >= 0; i--) {
            if (count > 0 && count % 3 == 0) {
                result.insert(0, ' ');
            }
            result.insert(0, integerStr.charAt(i));
            count++;
        }

        result.append('.');
        if (decimalPart < 10) {
            result.append('0');
        }
        result.append(decimalPart);

        return result.toString();
    }

    private double generateDouble(final double lower, final double upper) {
        if (lower >= upper) return lower;
        return lower + RANDOM.nextDouble() * (upper - lower);
    }

    private int generateInt(final int lower, final int upper) {
        if (lower >= upper) return lower;
        return RANDOM.nextInt(lower, upper + 1);
    }
}