package mc.barrelshop.shops.ui.icon.config;

import mc.compendium.chestinterface.components.configurations.ChestIconConfig;
import org.bukkit.Material;

import java.util.List;

public class EmptyIconConfig extends ChestIconConfig {

    public EmptyIconConfig() {
        super(Material.AIR, "", 0, List.of(), false);
    }

}