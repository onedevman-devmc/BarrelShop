package mc.barrelshop.shops.ui.icon;

import mc.compendium.chestinterface.components.ChestIcon;
import mc.compendium.chestinterface.components.configurations.ChestIconConfig;
import mc.compendium.chestinterface.events.ChestIconClickEvent;
import mc.compendium.events.EventHandler;
import org.bukkit.Material;

import java.util.List;

public class SlotIndicatorIcon extends ChestIcon {

    public enum Direction {
        UP("▲"),
        DOWN("▼"),
        LEFT("◀"),
        RIGHT("▶");

        //

        private final String indicator;

        //

        Direction(String indicator) {
            this.indicator = indicator;
        }

        //

        public String getIndicator() { return this.indicator; }

    }

    //

    @EventHandler
    public void onClick(ChestIconClickEvent event) {
        event.setCancelled(true);
    }

    //

    protected SlotIndicatorIcon(Material material, Direction direction, String text, List<String> description) {
        super(
            new ChestIconConfig(
                material,
                "§7§l" + direction.getIndicator() + " " + text + " " + "§7§l" + direction.getIndicator(),
                1,
                description,
                false
            )
        );
    }

}
