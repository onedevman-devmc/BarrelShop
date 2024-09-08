package mc.barrelshop.shop.events;

import mc.compendium.events.Event;

public abstract class PersistentBarrelShopRegistryEvent extends Event {

    public PersistentBarrelShopRegistryEvent() {
        this(true);
    }

    public PersistentBarrelShopRegistryEvent(boolean cancellable) {
        super(cancellable);
    }

}
