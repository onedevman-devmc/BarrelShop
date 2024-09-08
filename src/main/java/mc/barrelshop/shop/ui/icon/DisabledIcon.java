package mc.barrelshop.shop.ui.icon;

import mc.compendium.chestinterface.components.ChestIcon;
import mc.compendium.chestinterface.components.configurations.ChestIconConfig;
import mc.compendium.chestinterface.events.ChestIconClickEvent;
import mc.compendium.events.EventHandler;
import org.bukkit.Material;

import java.util.List;

public class DisabledIcon extends ChestIcon {

    private static final DisabledIcon INSTANCE = new DisabledIcon();
    public static DisabledIcon getInstance() { return INSTANCE; }

    //

    @EventHandler
    protected void onClick(ChestIconClickEvent event) {
        event.setCancelled(true);
    }

    //

    private DisabledIcon() {
        super(
            new ChestIconConfig(
                Material.BARRIER,
                "Disabled",
                1,
                List.of(),
                false
            )
        );
    }

}
