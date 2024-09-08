package mc.barrelshop.shops.ui.management.icon;

import mc.barrelshop.shops.ui.icon.SlotIndicatorIcon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EarningsContainerItemIndicatorIcon extends SlotIndicatorIcon {

    private static final Map<Direction, EarningsContainerItemIndicatorIcon> icons = new HashMap<>();

    static {
        for(Direction direction : Direction.values())
            icons.put(direction, new EarningsContainerItemIndicatorIcon(direction));
    }

    public static EarningsContainerItemIndicatorIcon get(Direction direction) { return icons.get(direction); }

    //

    protected EarningsContainerItemIndicatorIcon(Direction direction) {
        super(
            PriceSlotIndicatorIcon.get(direction).config().getMaterial(),
            direction,
            "§3§lEarnings Container",
            List.of(
                "§7Place a container item in this slot to set the container to use to store earnings.",
                "§7Right click to open this container."
            )
        );
    }

}
