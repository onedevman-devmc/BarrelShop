package mc.barrelshop.shop.ui.management.icon;

import mc.barrelshop.shop.BarrelShop;
import mc.barrelshop.shop.ui.management.input.ShopNameInput;
import mc.compendium.chestinterface.components.ChestIcon;
import mc.compendium.chestinterface.components.configurations.ChestIconConfig;
import mc.compendium.chestinterface.events.ChestIconClickEvent;
import mc.compendium.events.EventHandler;
import org.bukkit.Material;

import java.util.List;

public class ChangeShopNameIcon extends ChestIcon {

    @EventHandler
    public void onClick(ChestIconClickEvent event) {
        event.setCancelled(true);
        event.getPlayer().openInventory(new ShopNameInput(this.shop).toBukkit());
    }

    //

    private final BarrelShop shop;

    //

    public ChangeShopNameIcon(BarrelShop shop) {
        super(
            new ChestIconConfig(
                Material.NAME_TAG,
                "§7§lShop Name",
                1,
                List.of(
                    "§7Current§8:§r " + shop.getName(),
                    "Click to change the shop name."
                ),
                false
            )
        );

        //

        this.shop = shop;
    }

    //

    public BarrelShop getShop() { return this.shop; }

}
