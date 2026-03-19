package stickdev.tun1x;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import stickdev.tun1x.cmd.PoolCommand;
import stickdev.tun1x.config.ConfigManager;
import stickdev.tun1x.engine.CycleEngine;
import stickdev.tun1x.engine.DisplayEngine;
import stickdev.tun1x.engine.DistributionEngine;
import stickdev.tun1x.handler.ZoneHandler;
import stickdev.tun1x.lang.LangManager;
import stickdev.tun1x.registry.ItemRegistry;
import stickdev.tun1x.scheduler.AnchorScheduler;
import stickdev.tun1x.scheduler.DropScheduler;
import stickdev.tun1x.scheduler.PayoutScheduler;
import stickdev.tun1x.util.TextUtil;

@Getter
public final class StickHWSun extends JavaPlugin {

    private static StickHWSun instance;
    private Economy vaultEconomy;
    private ConfigManager settings;
    private LangManager langManager;
    private CycleEngine rewardCycle;
    private DistributionEngine payoutSystem;
    private DisplayEngine hologramSystem;
    private ItemRegistry lootPool;
    private DropScheduler itemDropper;
    private LiteCommands<CommandSender> commandFramework;

    @Override
    public void onEnable() {
        instance = this;

        if (!setupVault()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        TextUtil.init(BukkitAudiences.create(this));

        settings = new ConfigManager(this);
        langManager = new LangManager(this);
        lootPool = new ItemRegistry(this);
        rewardCycle = new CycleEngine(this);
        payoutSystem = new DistributionEngine(this);
        hologramSystem = new DisplayEngine(this);
        itemDropper = new DropScheduler(this);

        Server server = getServer();
        server.getScheduler().runTaskLater(this, () -> {
            Location pos = buildLocation();
            if (pos != null && pos.getWorld() != null) {
                hologramSystem.init();
                rewardCycle.begin();
                new PayoutScheduler(this).activate();
                itemDropper.activate();
                new AnchorScheduler(this).activate();
            }
        }, 40L);

        server.getPluginManager().registerEvents(new ZoneHandler(this), this);

        commandFramework = LiteBukkitFactory.builder("stickhwsun", this)
                .commands(new PoolCommand(this))
                .build();
    }

    @Override
    public void onDisable() {
        if (commandFramework != null) commandFramework.unregister();
        if (rewardCycle != null) rewardCycle.terminate();
        if (hologramSystem != null) hologramSystem.cleanup();
        TextUtil.close();
        instance = null;
    }

    private boolean setupVault() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        vaultEconomy = rsp.getProvider();
        return vaultEconomy != null;
    }

    public Location buildLocation() {
        World w = Bukkit.getWorld(settings.getWorldName());
        if (w == null) return null;
        return new Location(w, settings.getCoordX(), settings.getCoordY(), settings.getCoordZ());
    }

    public static StickHWSun inst() {
        return instance;
    }

    public Economy eco() {
        return vaultEconomy;
    }

    public ConfigManager cfg() {
        return settings;
    }

    public CycleEngine cycleEngine() {
        return rewardCycle;
    }

    public DistributionEngine distributionEngine() {
        return payoutSystem;
    }

    public DisplayEngine displayEngine() {
        return hologramSystem;
    }

    public ItemRegistry itemRegistry() {
        return lootPool;
    }

    public DropScheduler dropScheduler() {
        return itemDropper;
    }

    public LangManager lang() {
        return langManager;
    }
}
