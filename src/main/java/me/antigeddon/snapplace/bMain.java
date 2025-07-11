package me.antigeddon.snapplace;

import me.antigeddon.snapplace.Command.cEditSign;
import me.antigeddon.snapplace.Place.bPlace;
import me.antigeddon.snapplace.Place.bPlaceOnInteractable;
import me.antigeddon.snapplace.Slab.slAntiMerge;
import me.antigeddon.snapplace.Slab.slPlaceBetween;
import me.antigeddon.snapplace.Snow.sSnow;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.Bukkit.getLogger;

public class bMain extends JavaPlugin implements Listener {

    private static bMain instance;
    public static bConfig config;

    @Override
    public void onEnable() {
        instance = this;
        getServer().getLogger().info("[SnapPlace] 1.0 initialized");
        config = new bConfig(this, "config.yml");
        config.load();
        config.printAllConfig(getLogger());

        cEditSign cEditSignInstance = new cEditSign(this);
        this.getCommand("editsign").setExecutor(cEditSignInstance);
        getServer().getPluginManager().registerEvents(cEditSignInstance, this);
        getServer().getPluginManager().registerEvents(new slPlaceBetween(), this);
        getServer().getPluginManager().registerEvents(new slAntiMerge(), this);
        getServer().getPluginManager().registerEvents(new bPlaceOnInteractable(), this);
        getServer().getPluginManager().registerEvents(new bPlace(), this);
        getServer().getPluginManager().registerEvents(new sSnow(), this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getServer().getLogger().info("[SnapPlace] 1.0 is asleep ~ZZZzzzz...");
    }

    public static bConfig getPluginConfig() {
        return config;
    }

    public static bMain getInstance() {
        return instance;
    }

    public String pName() {
        return "§f[§6Snap§ePlace§f] ";
    }
}