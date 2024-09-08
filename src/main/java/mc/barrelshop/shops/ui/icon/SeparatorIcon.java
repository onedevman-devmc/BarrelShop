package mc.barrelshop.shops.ui.icon;

import mc.compendium.chestinterface.components.ChestIcon;
import mc.compendium.chestinterface.components.configurations.ChestIconConfig;
import mc.compendium.chestinterface.events.ChestIconClickEvent;
import mc.compendium.events.EventHandler;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public class SeparatorIcon extends ChestIcon {

    private static final SeparatorIcon INSTANCE = new SeparatorIcon();
    public static SeparatorIcon getInstance() { return INSTANCE; }

    //

    @EventHandler
    protected void onClick(ChestIconClickEvent event) {
        event.setCancelled(true);
    }

    //

    private SeparatorIcon() {
        super(
            new ChestIconConfig(
                Material.GRAY_STAINED_GLASS_PANE,
                " ",
                1,
                List.of(),
                false
            )
        );
    }

    //

    @Override
    public ItemStack toBukkit() {
        ItemStack result = super.toBukkit();

        ItemMeta meta = Objects.requireNonNull(result.getItemMeta());

        meta.setHideTooltip(true);
        result.setItemMeta(meta);

        return result;
    }

}
