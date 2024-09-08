package mc.barrelshop.shops.ui.management.icon;

import mc.barrelshop.shops.ui.icon.SlotIndicatorIcon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StocksContainerItemIndicatorIcon extends SlotIndicatorIcon {

    private static final Map<Direction, StocksContainerItemIndicatorIcon> icons = new HashMap<>();

    static {
        for(Direction direction : Direction.values())
            icons.put(direction, new StocksContainerItemIndicatorIcon(direction));
    }

    public static StocksContainerItemIndicatorIcon get(Direction direction) { return icons.get(direction); }

    //

    protected StocksContainerItemIndicatorIcon(Direction direction) {
        super(
            ResultSlotIndicatorIcon.get(direction).config().getMaterial(),
            direction,
            "§a§lStocks Container",
            List.of(
                "§7Place a container item in this slot to set the container to use to store stocks.",
                "§7Right click to open this container."
            )
        );
    }

}
