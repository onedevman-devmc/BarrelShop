package mc.barrelshop;

import mc.barrelshop.shop.BarrelShopManager;
import mc.compendium.chestinterface.ChestInterfaceApi;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Paths;

public class PluginMain extends JavaPlugin {

    private static PluginMain instance = null;
    public static PluginMain instance() { return instance; }

    //

    private BarrelShopManager shopManager;

    //

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        ChestInterfaceApi.getInstance().enable(this);

        //

        this.shopManager = new BarrelShopManager(this, Paths.get(getDataFolder().getPath(), "shops", "data").toFile());
        this.shopManager.runShopTradesAnimationLoop();

        //

        Bukkit.getPluginManager().registerEvents(this.shopManager(), this);

        //

        this.reloadConfig();
        this.getConfig().setDefaults(YamlConfiguration.loadConfiguration(this.getTextResource("config.yml")));
        this.getConfig().options().copyDefaults(true);

        this.saveConfig();
    }

    @Override
    public void onDisable() {
        this.shopManager.stopShopTradesAnimationLoop();

        try {
            this.shopManager.getShopRegistry().saveAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ChestInterfaceApi.getInstance().disable();

        this.reloadConfig();
        this.saveConfig();
    }

    //

    public BarrelShopManager shopManager() { return this.shopManager; }

}
