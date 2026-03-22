package stickdev.tun1x.cmd;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import dev.rollczi.litecommands.suggestion.Suggestion;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import stickdev.tun1x.StickHWSun;
import stickdev.tun1x.lang.LangManager;
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
        final ItemStack hand = sender.getInventory().getItemInMainHand();

        if (hand.getType().isAir()) {
            sender.sendMessage(LangManager.MESSAGES.addNoItem);
            return;
        }

        if (plugin.getLootPool().register(key.toLowerCase(), hand)) {
            sender.sendMessage(LangManager.MESSAGES.addSuccess);
        } else {
            sender.sendMessage(LangManager.MESSAGES.addExists);
        }
    }

    @Execute(name = "remove")
    public void remove(@Context Player sender, @Arg String key) {
        if (plugin.getLootPool().unregister(key.toLowerCase())) {
            sender.sendMessage(LangManager.MESSAGES.removeSuccess);
        }
    }

    @Execute(name = "setcycle")
    public void setCycle(@Context Player sender, @Arg RewardCycle cycle) {
        if (cycle == null) {
            sender.sendMessage(TextUtil.color("All types: money, exp"));
            return;
        }

        plugin.getRewardCycle().setActive(cycle);
        plugin.getHologramManager().setNewLines();
        sender.sendMessage(LangManager.MESSAGES.cycleChanged);
    }
}
