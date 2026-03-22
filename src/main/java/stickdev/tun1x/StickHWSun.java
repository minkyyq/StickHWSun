package stickdev.tun1x;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import stickdev.tun1x.cmd.PoolCommand;
import stickdev.tun1x.managers.CycleManager;
import stickdev.tun1x.managers.HologramManager;
import stickdev.tun1x.managers.RewardManager;
import stickdev.tun1x.lang.LangManager;
import stickdev.tun1x.registry.ItemRegistry;
import stickdev.tun1x.services.AnchorService;
import stickdev.tun1x.services.DropService;
import stickdev.tun1x.services.PayoutService;
import stickdev.tun1x.util.TextUtil;

@Getter
public final class StickHWSun extends JavaPlugin {

    private static StickHWSun instance;
    private Economy vaultEconomy;
    private Config settings;
    private LangManager langManager;
    private CycleManager rewardCycle;
    private RewardManager payoutSystem;
    private HologramManager hologramManager;
    private ItemRegistry lootPool;
    private DropService dropService;
    private LiteCommands<CommandSender> commandFramework;

    private static final int startup = 40;

    @Override
    public void onEnable() {
        instance = this;

        final PluginManager pluginManager = super.getServer().getPluginManager();
        if (!setupVault(pluginManager)) {
            super.getLogger().severe("Vault не найден");
            pluginManager.disablePlugin(this);
            return;
        }

        TextUtil.init(BukkitAudiences.create(this));

        settings = new Config(this);
        langManager = new LangManager(this);
        lootPool = new ItemRegistry(this);
        rewardCycle = new CycleManager(this);
        payoutSystem = new RewardManager(this);
        hologramManager = new HologramManager(this);
        dropService = new DropService(this);

        super.getServer().getScheduler().runTaskLater(this, () -> {
            final Location pos = buildLocation();

            if (pos != null && pos.getWorld() != null) {
                hologramManager.init();
                rewardCycle.begin();

                final int oneSecond = 20;
                final int period = settings.getTickInterval() * oneSecond;
                new PayoutService(payoutSystem)
                        .runTaskTimer(this, period, period);

                dropService.activate();

                new AnchorService(this)
                        .runTaskTimer(this, oneSecond, oneSecond);
            }
        }, startup);

        pluginManager.registerEvents(new BukkitListeners(this), this);

        commandFramework = LiteBukkitFactory.builder("stickhwsun", this)
                .commands(new PoolCommand(this))
                .build();
    }

    @Override
    public void onDisable() {
        if (commandFramework != null) commandFramework.unregister();
        if (rewardCycle != null) rewardCycle.terminate();
        if (hologramManager != null) hologramManager.cleanup();
        if (dropService != null) dropService.stop();

        TextUtil.close();
        instance = null;
    }

    private boolean setupVault(PluginManager pluginManager) {
        if (pluginManager.getPlugin("Vault") == null) return false;

        final RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;

        vaultEconomy = rsp.getProvider();
        return true;
    }

    public Location buildLocation() {
        final World w = Bukkit.getWorld(settings.getWorldName());
        if (w == null) return null;

        return new Location(w, settings.getCoordX(), settings.getCoordY(), settings.getCoordZ());
    }

    public static StickHWSun inst() {
        return instance;
    }
}
