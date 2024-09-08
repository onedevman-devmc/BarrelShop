package mc.barrelshop.shop.ui.management.icon;

import mc.barrelshop.shop.ui.icon.SlotIndicatorIcon;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultSlotIndicatorIcon extends SlotIndicatorIcon {

    private static final Map<Direction, ResultSlotIndicatorIcon> icons = new HashMap<>();

    static {
        for(Direction direction : Direction.values())
            icons.put(direction, new ResultSlotIndicatorIcon(direction));
    }

    public static ResultSlotIndicatorIcon get(Direction direction) { return icons.get(direction); }

    //

    private ResultSlotIndicatorIcon(Direction direction) {
        super(
            Material.LIME_STAINED_GLASS_PANE,
            direction,
            "§a§lResult Slot",
            List.of(
                "§7Left click with an item on the slot to set the trade result.",
                "§7Right click to reset price."
            )
        );
    }

}
