package stickdev.tun1x;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import stickdev.tun1x.lang.LangManager;
import stickdev.tun1x.util.TextUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BukkitListeners implements Listener {

    private final StickHWSun plugin;
    private final Set<UUID> insideZone = ConcurrentHashMap.newKeySet();

    public BukkitListeners(StickHWSun plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent evt) {
        if (!evt.hasChangedBlock()) return;
        checkPlayer(evt.getPlayer(), evt.getTo());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent evt) {
        checkPlayer(evt.getPlayer(), evt.getTo());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent evt) {
        final Player player = evt.getPlayer();
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> checkPlayer(player, player.getLocation()), 10L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent evt) {
        insideZone.remove(evt.getPlayer().getUniqueId());
    }

    private void checkPlayer(Player player, Location loc) {
        final Location center = plugin.buildLocation();
        final World world = loc.getWorld();
        if (center == null || world == null) return;

        if (!world.equals(center.getWorld())) {
            insideZone.remove(player.getUniqueId());
            return;
        }

        final int range = plugin.getSettings().getAreaSize();
        final boolean withinRange = loc.distanceSquared(center) <= (range * range);
        final UUID playerId = player.getUniqueId();

        if (withinRange && !insideZone.contains(playerId)) {
            insideZone.add(playerId);

            player.sendMessage(LangManager.MESSAGES.areaEntry
                    .replace("%radius%", String.valueOf(range))
                    .replace("%reward%", plugin.getRewardCycle().getActive().getReward())
            );

        } else if (!withinRange && insideZone.contains(playerId)) insideZone.remove(playerId);
    }
}
