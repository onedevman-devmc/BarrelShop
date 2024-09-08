package mc.barrelshop.shops.ui.management.icon;

import mc.barrelshop.shops.ui.icon.SlotIndicatorIcon;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PriceSlotIndicatorIcon extends SlotIndicatorIcon {

    private static final Map<Direction, PriceSlotIndicatorIcon> icons = new HashMap<>();

    static {
        for(Direction direction : Direction.values())
            icons.put(direction, new PriceSlotIndicatorIcon(direction));
    }

    public static PriceSlotIndicatorIcon get(Direction direction) { return icons.get(direction); }

    //

    private PriceSlotIndicatorIcon(Direction direction) {
        super(
            Material.CYAN_STAINED_GLASS_PANE,
            direction,
            "§3§lPrice Slot",
            List.of(
                "§7Left click with an item on the slot to set the trade price.",
                "§7Right click to reset price."
            )
        );
    }

}
