package stickdev.tun1x.cmd;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import stickdev.tun1x.StickHWSun;
import stickdev.tun1x.model.RewardCycle;
import stickdev.tun1x.util.TextUtil;

@Command(name = "stickhwsun")
@Permission("stickhwsun.admin")
public class PoolCommand {

    private final StickHWSun plugin;

    public PoolCommand(StickHWSun plugin) {
        this.plugin = plugin;
    }

    @Execute(name = "add")
    public void add(@Context Player sender, @Arg String key) {
        ItemStack hand = sender.getInventory().getItemInMainHand();

        if (hand == null || hand.getType() == Material.AIR) {
            sender.sendMessage(TextUtil.color(plugin.getLangManager().get("commands.add-no-item")));
            return;
        }

        String k = key.toLowerCase();

        if (plugin.getLootPool().register(k, hand)) {
            sender.sendMessage(TextUtil.color(plugin.getLangManager().get("commands.add-success")));
        } else {
            sender.sendMessage(TextUtil.color(plugin.getLangManager().get("commands.add-exists")));
        }
    }

    @Execute(name = "remove")
    public void remove(@Context Player sender, @Arg String key) {
        String k = key.toLowerCase();

        if (plugin.getLootPool().unregister(k)) {
            sender.sendMessage(TextUtil.color(plugin.getLangManager().get("commands.remove-success")));
        }
    }

    @Execute(name = "setcycle")
    public void setCycle(@Context Player sender, @Arg String type) {
        RewardCycle cycle;
        
        if (type.equalsIgnoreCase("money")) {
            cycle = RewardCycle.MONEY;
        } else if (type.equalsIgnoreCase("exp")) {
            cycle = RewardCycle.XP;
        } else {
            return;
        }

        plugin.getRewardCycle().setActive(cycle);
        plugin.getHologramSystem().refresh();
        sender.sendMessage(TextUtil.color(plugin.getLangManager().get("commands.cycle-changed")));
    }

    private String extractName(ItemStack stack) {
        if (stack.hasItemMeta()) {
            ItemMeta meta = stack.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                return meta.getDisplayName();
            }
        }
        return stack.getType().name();
    }
}
