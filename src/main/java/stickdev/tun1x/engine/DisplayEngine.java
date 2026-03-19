package stickdev.tun1x.engine;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import stickdev.tun1x.StickHWSun;
import stickdev.tun1x.model.RewardCycle;
import stickdev.tun1x.util.TextUtil;

import java.util.ArrayList;
import java.util.List;

public class DisplayEngine {

    private final StickHWSun core;
    private final List<ArmorStand> holograms = new ArrayList<>();
    private int countdown;

    public DisplayEngine(StickHWSun core) {
        this.core = core;
        this.countdown = 20;
    }

    public void init() {
        cleanup();
        removeOrphaned();

        Location pos = core.buildLocation();
        if (pos == null || pos.getWorld() == null) return;

        Block block = pos.getBlock();
        if (block.getType() != Material.RESPAWN_ANCHOR) {
            block.setType(Material.RESPAWN_ANCHOR);
        }

        double height = core.getSettings().getVerticalShift();
        Location center = pos.clone().add(0.5, height, 0.5);

        holograms.add(createStand(center.clone().add(0, 0.6, 0), renderHeader()));
        holograms.add(createStand(center.clone().add(0, 0.3, 0), renderMode()));
        holograms.add(createStand(center, renderCountdown()));
    }

    public void removeOrphaned() {
        Location pos = core.buildLocation();
        if (pos == null || pos.getWorld() == null) return;

        for (Entity ent : pos.getWorld().getNearbyEntities(pos, 4, 6, 4)) {
            if (ent.getType() != EntityType.ARMOR_STAND) continue;
            ArmorStand stand = (ArmorStand) ent;
            if (!stand.isVisible() && stand.isMarker() && stand.isCustomNameVisible()) {
                stand.remove();
            }
        }
    }

    private ArmorStand createStand(Location loc, String text) {
        ArmorStand stand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setCustomName(text);
        stand.setCustomNameVisible(true);
        stand.setMarker(true);
        stand.setSmall(true);
        stand.setInvulnerable(true);
        return stand;
    }

    public void refresh() {
        if (holograms.size() != 3) return;
        holograms.get(0).setCustomName(renderHeader());
        holograms.get(1).setCustomName(renderMode());
        holograms.get(2).setCustomName(renderCountdown());
    }

    public void updateTimer(int seconds) {
        this.countdown = seconds;
        if (holograms.size() == 3) {
            holograms.get(2).setCustomName(renderCountdown());
        }
    }

    public int getTimer() {
        return countdown;
    }

    public void cleanup() {
        for (ArmorStand stand : holograms) {
            if (stand != null && !stand.isDead()) {
                stand.remove();
            }
        }
        holograms.clear();
    }

    private String renderHeader() {
        return TextUtil.color(core.getSettings().getHeaderText());
    }

    private String renderMode() {
        RewardCycle mode = core.getRewardCycle().getActive();
        String colored = TextUtil.color(mode.getDisplay());
        return TextUtil.color(core.getSettings().getStatusText().replace("%cycle%", colored));
    }

    private String renderCountdown() {
        return TextUtil.color(core.getSettings().getCountdownText().replace("%time%", String.valueOf(countdown)));
    }
}
