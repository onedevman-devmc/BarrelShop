package mc.barrelshop.shop.ui.management.icon;

import mc.barrelshop.shop.BarrelShop;
import mc.compendium.chestinterface.components.AbstractChestIcon;
import mc.compendium.chestinterface.components.configurations.ChestIconConfig;
import mc.compendium.chestinterface.events.ChestIconClickEvent;
import mc.compendium.events.EventHandler;
import org.bukkit.Material;

import java.util.List;

public class ToggleTradesAnimationIconIcon extends AbstractChestIcon<ToggleTradesAnimationIconIcon.Config> {

    public static class Config extends ChestIconConfig {

        private final BarrelShop shop;

        //

        public Config(BarrelShop shop) {
            super(Material.GLOW_ITEM_FRAME, "§7§lToggle Trades Animation Icon Item", 1, List.of(), false);

            //

            this.shop = shop;
        }

        //

        public BarrelShop getShop() { return this.shop; }

        //

        @Override
        public boolean getEnchanted() { return this.shop.doesTradesCanBeAnimated(); }

        @Override
        public List<String> getDescription() {
            return List.of("§7Current state§8:§r " + (this.getEnchanted() ? "§aon" : "§4off") + "§r");
        }

    }

    //

    @EventHandler
    public void onClick(ChestIconClickEvent event) {
        event.setCancelled(true);

        BarrelShop shop = this.config().getShop();

        shop.setTradesCanBeAnimated(!shop.doesTradesCanBeAnimated());
        shop.requestUpdateToManagers();

        event.getInventory().setItem(event.getSlot(), this.toBukkit());
    }

    //

    public ToggleTradesAnimationIconIcon(BarrelShop shop) {
        super(new Config(shop));
    }

}
