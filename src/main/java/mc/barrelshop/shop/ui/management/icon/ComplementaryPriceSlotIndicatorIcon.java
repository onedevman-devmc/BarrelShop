package mc.barrelshop.shop.ui.management.icon;

import mc.barrelshop.shop.ui.icon.SlotIndicatorIcon;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComplementaryPriceSlotIndicatorIcon extends SlotIndicatorIcon {

    private static final Map<Direction, ComplementaryPriceSlotIndicatorIcon> icons = new HashMap<>();

    static {
        for(Direction direction : Direction.values())
            icons.put(direction, new ComplementaryPriceSlotIndicatorIcon(direction));
    }

    public static ComplementaryPriceSlotIndicatorIcon get(Direction direction) { return icons.get(direction); }

    //

    private ComplementaryPriceSlotIndicatorIcon(Direction direction) {
        super(
            Material.LIGHT_BLUE_STAINED_GLASS_PANE,
            direction,
            "§b§lComplementary Price Slot",
            List.of(
                "§7Left click with an item on the slot to set the trade complementary price.",
                "§7Right click to reset price."
            )
        );
    }

}
