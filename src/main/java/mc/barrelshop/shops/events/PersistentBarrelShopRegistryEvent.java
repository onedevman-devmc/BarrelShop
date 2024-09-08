package mc.barrelshop.shops.events;

import mc.compendium.events.Event;
import org.bukkit.Location;

public abstract class PersistentBarrelShopRegistryEvent extends Event {

    public PersistentBarrelShopRegistryEvent() {
        this(true);
    }

    public PersistentBarrelShopRegistryEvent(boolean cancellable) {
        super(cancellable);
    }

}
