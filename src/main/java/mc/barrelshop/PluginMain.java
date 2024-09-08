package mc.barrelshop;

import mc.barrelshop.shop.BarrelShopManager;
import mc.compendium.chestinterface.ChestInterfaceApi;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;

public class PluginMain extends JavaPlugin {

    private static PluginMain instance = null;
    public static PluginMain instance() { return instance; }

    //

    private boolean dependencySetupSucceeded = false;

    private BarrelShopManager shopManager;

    //

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        this.setupDependencies();
        if(!this.doesDependencySetupSucceeded()) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        ChestInterfaceApi.getInstance().enable(this);

        //

        this.shopManager = new BarrelShopManager(this, Paths.get(getDataFolder().getPath(), "shops", "data").toFile());
        this.shopManager.runShopTradesAnimationLoop();

        //

        Bukkit.getPluginManager().registerEvents(this.shopManager(), this);

        //

        this.reloadConfig();
        this.getConfig().setDefaults(YamlConfiguration.loadConfiguration(Objects.requireNonNull(this.getTextResource("config.yml"))));
        this.getConfig().options().copyDefaults(true);

        this.saveConfig();
    }

    @Override
    public void onDisable() {
        if(!this.doesDependencySetupSucceeded()) return;

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

    //

    public boolean doesDependencySetupSucceeded() {
        return this.dependencySetupSucceeded;
    }

    private void setupDependencies() {
        boolean dependencyPluginsValid = true;

        for(Map.Entry<String, Boolean> dependencyPluginInfo : Map.ofEntries(
            Map.entry("Compendium", this.isDevBuild())
        ).entrySet()) {
            boolean shouldProcessDependency = dependencyPluginInfo.getValue();

            if(shouldProcessDependency) {
                String dependencyPluginName = dependencyPluginInfo.getKey();

                Plugin dependencyPlugin = Bukkit.getPluginManager().getPlugin(dependencyPluginName);
                if (dependencyPlugin == null) {
                    getLogger().warning("\033[31m\033[1mUnable to find dependency plugin " + dependencyPluginName + ".");
                    dependencyPluginsValid = false;
                }
            }
        }

        this.dependencySetupSucceeded = dependencyPluginsValid;
    }

    private boolean isDevBuild() {
        try {
            Class.forName(this.getClass().getPackage().getName() + ".Dev");
            return true;
        }
        catch (ClassNotFoundException e) { return false; }
    }

}
