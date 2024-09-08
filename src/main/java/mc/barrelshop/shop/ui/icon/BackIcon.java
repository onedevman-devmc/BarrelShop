package mc.barrelshop.shop.ui.icon;

import mc.compendium.chestinterface.components.ChestIcon;
import mc.compendium.chestinterface.components.configurations.ChestIconConfig;
import mc.compendium.chestinterface.events.ChestIconClickEvent;
import mc.compendium.events.EventHandler;
import mc.barrelshop.shop.ui.InventoryHistory;
import org.bukkit.Material;

import java.util.List;

public class BackIcon extends ChestIcon {

    @EventHandler
    protected void onClick(ChestIconClickEvent event) {
        event.setCancelled(true);

        if(!this.history.empty()) {
            event.getPlayer().openInventory(this.history.pop().get());
        }
    }

    //

    private final InventoryHistory history;

    //

    public BackIcon(InventoryHistory history) {
        super(new ChestIconConfig(Material.ARROW, "Back", 1, List.of(), false));

        this.history = history;
    }

}
