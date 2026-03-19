package stickdev.tun1x.handler;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import stickdev.tun1x.StickHWSun;
import stickdev.tun1x.model.RewardCycle;
import stickdev.tun1x.util.TextUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ZoneHandler implements Listener {

    private final StickHWSun core;
    private final Set<UUID> insideZone = new HashSet<>();

    public ZoneHandler(StickHWSun core) {
        this.core = core;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent evt) {
        if (evt.getTo() == null) return;
        if (!evt.hasChangedBlock()) return;
        checkPlayer(evt.getPlayer(), evt.getTo());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent evt) {
        if (evt.getTo() == null) return;
        checkPlayer(evt.getPlayer(), evt.getTo());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent evt) {
        Player player = evt.getPlayer();
        core.getServer().getScheduler().runTaskLater(core, () -> checkPlayer(player, player.getLocation()), 10L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent evt) {
        insideZone.remove(evt.getPlayer().getUniqueId());
    }

    private void checkPlayer(Player player, Location loc) {
        Location center = core.buildLocation();
        if (center == null || center.getWorld() == null) return;
        if (loc.getWorld() == null || !loc.getWorld().equals(center.getWorld())) {
            insideZone.remove(player.getUniqueId());
            return;
        }

        int range = core.cfg().getAreaSize();
        double distSquared = loc.distanceSquared(center);
        boolean withinRange = distSquared <= (range * range);
        UUID playerId = player.getUniqueId();

        if (withinRange && !insideZone.contains(playerId)) {
            insideZone.add(playerId);
            RewardCycle mode = core.cycleEngine().getActive();
            String text = core.lang().get("notifications.area-entry")
                    .replace("%radius%", String.valueOf(range))
                    .replace("%reward%", mode.getReward());
            player.sendMessage(TextUtil.color(text));
        } else if (!withinRange && insideZone.contains(playerId)) {
            insideZone.remove(playerId);
        }
    }
}
